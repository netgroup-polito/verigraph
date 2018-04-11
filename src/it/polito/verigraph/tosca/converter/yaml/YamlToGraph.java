/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.converter.yaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.YamlParsingUtils;
import it.polito.verigraph.tosca.serializer.YamlConfigSerializer;
import it.polito.verigraph.tosca.yaml.beans.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;

public class YamlToGraph {
    public static Graph mapTopologyTemplateYaml(ServiceTemplateYaml yamlServiceTemplate) throws BadRequestException {
        Graph graph = new Graph();
        Map<Long, Node> graphNodes = new HashMap<>();
        Map<String, NodeTemplateYaml> nodes = new HashMap<>();
        Map<String, RelationshipTemplateYaml> relats = new HashMap<>();

        nodes = yamlServiceTemplate.getTopology_template().getNode_templates();

        for (Map.Entry<String, NodeTemplateYaml> nodeYamlEntry : nodes.entrySet()) {
            Node node = mapNodeTemplateYaml(nodeYamlEntry.getValue());

            try {
                graphNodes.put(Long.valueOf(nodeYamlEntry.getKey()), node);
            } catch (NumberFormatException e) {
                throw new BadRequestException("The NodeTemplate ID must be a number.");
            }

        }

        // Add Neighbours to the Nodes of the list
        relats = yamlServiceTemplate.getTopology_template().getRelationship_templates();
        mapRelationshipTemplatesYaml(graphNodes, relats);

        // Add Nodes and ID to the graph
        graph.setNodes(graphNodes);
        try {
            graph.setId(Long.valueOf(yamlServiceTemplate.getMetadata().get("template_id")));
        } catch (NumberFormatException ex) {
            throw new BadRequestException("If you want to use this service, the TopologyTemplate ID must be a number.");
        } catch (NullPointerException ex) {} //ID is not mandatory for the user since VeriGraph provides its IDs

        return graph;
    }


    private static Node mapNodeTemplateYaml(NodeTemplateYaml yamlNodeTemplate) {
        Node node = new Node();

        String type =  yamlNodeTemplate.getType().replace("verigraph.nodeTypes.", "").toLowerCase();

        try {
            node.setName(yamlNodeTemplate.getName());
            Configuration conf = mapConfigurationYaml(yamlNodeTemplate);
            node.setConfiguration(conf);
            node.setFunctional_type(type);
        } catch(NullPointerException ex) {
            throw new BadRequestException("A NodeTemplate has wrong fields representation.");
        }
        return node;
    }


    private static void mapRelationshipTemplatesYaml(Map<Long, Node> graphNodes, Map<String, RelationshipTemplateYaml> relats) {
        //updated nodes (update = Node + its Neighbours)
        for(Map.Entry<String, RelationshipTemplateYaml> yamlRelationshipTemplate : relats.entrySet()) {
            try {
                // Retrieve relationship information
                String target = yamlRelationshipTemplate.getValue().getProperties().get("target_id");
                String source = yamlRelationshipTemplate.getValue().getProperties().get("source_id");
                String name = graphNodes.get(Long.valueOf(target)).getName();

                Neighbour neigh = new Neighbour();
                neigh.setName(name);
                neigh.setId(Long.valueOf(target));

                //Retrieve the Neighbour map of the source Node and add the Neighbour
                Node sourceNode = graphNodes.get(Long.valueOf(source));
                Map<Long,Neighbour> sourceNodeNeighMap = sourceNode.getNeighbours();
                if(sourceNodeNeighMap.containsKey(neigh.getId()))
                    throw new BadRequestException("The RelationshipTemplate ID must be unique.");
                else
                    sourceNodeNeighMap.put(neigh.getId(), neigh);
                sourceNode.setNeighbours(sourceNodeNeighMap);

                //Update the Node list
                graphNodes.put(Long.valueOf(source), sourceNode);

            } catch(NullPointerException | NumberFormatException ex) {
                throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
            }

        }

    }


    private static Configuration mapConfigurationYaml(NodeTemplateYaml node) {
        Configuration config = new Configuration();
        JsonNode jsonConfiguration = null;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ConfigurationYaml.class, new YamlConfigSerializer());
        mapper.registerModule(module);

        try{
            String stringConfiguration = YamlParsingUtils.obtainConfiguration(node);
            jsonConfiguration = mapper.readTree(stringConfiguration);
            config.setConfiguration(jsonConfiguration);
            config.setDescription("");
            config.setId("");
        } catch (NullPointerException | IOException | BadRequestException e) {
            throw new BadRequestException("Not able to retrieve a valid configuration");
        }

        return config;
    }
}
