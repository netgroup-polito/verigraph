/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.converter.xml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.XmlParsingUtils;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TExtensibleElements;
import it.polito.tosca.jaxb.TNodeTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate;
import it.polito.tosca.jaxb.TServiceTemplate;

public class XmlToGraph {
    public static Graph mapTopologyTemplate(Definitions definitions) throws DataNotFoundException, BadRequestException {
        Graph graph = new Graph();
        Map<Long, Node> nodes = new HashMap<>();

        List<TExtensibleElements> elements = definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();

        // Retrieve the list of ServiceTemplate in Definitions
        List<TServiceTemplate> serviceTemplates = elements.stream().filter(p -> p instanceof TServiceTemplate)
                .map(obj -> (TServiceTemplate) obj).collect(Collectors.toList());
        if (serviceTemplates.isEmpty())
            throw new DataNotFoundException("There is no ServiceTemplate into the Definitions object");

        List<TNodeTemplate> nodeTemplates = XmlParsingUtils.obtainNodeTemplates(serviceTemplates.get(0));

        for (TNodeTemplate nodeTemplate : nodeTemplates) {
            Node node = mapNodeTemplate(nodeTemplate);
            nodes.put(Long.valueOf(nodeTemplate.getId()), node);
        }

        // Add Neighbour to the Node of the list
        List<TRelationshipTemplate> relationshipTemplates = XmlParsingUtils.obtainRelationshipTemplates(serviceTemplates.get(0));
        mapRelationshipTemplates(nodes, relationshipTemplates);

        // Add Nodes and ID to the graph
        graph.setNodes(nodes);

        try {
            graph.setId(Long.valueOf(serviceTemplates.get(0).getId()));
        } catch (NumberFormatException ex) {
            throw new BadRequestException("If you want to store your TopologyTemplate on this server, "
                    + "the TopologyTemplate ID must be a number.");
        }

        return graph;
    }


    private static Node mapNodeTemplate(TNodeTemplate nodeTemplate) {
        Node node = new Node();

        String toscaType =  nodeTemplate.getType().toString();
        toscaType = toscaType.replace("Type", "").replace("{http://docs.oasis-open.org/tosca/ns/2011/12}", "");
        toscaType = toscaType.toLowerCase();

        try {
            node.setId(Long.valueOf(nodeTemplate.getId()));
        } catch(NumberFormatException ex) {
            throw new BadRequestException("The NodeTemplate ID must be a number.");
        }
        try {
            node.setName(nodeTemplate.getName());
            Configuration conf = mapToscaConfiguration(XmlParsingUtils.obtainConfiguration(nodeTemplate));
            node.setConfiguration(conf);
            node.setFunctional_type(toscaType);
        } catch(NullPointerException | IOException ex) {
            throw new BadRequestException("The NodeTemplate id:"+node.getId()+" has wrong fields representation.");
        }
        return node;
    }


    private static void mapRelationshipTemplates(Map<Long, Node> nodes, List<TRelationshipTemplate> relationshipTemplates) {
        //update of nodes... (update = Node + its Neighbours)
        for(TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
            if (relationshipTemplate != null) {
                try {
                    if(relationshipTemplate.getSourceElement().getRef() == relationshipTemplate.getTargetElement().getRef())
                        throw new BadRequestException("Source and Target cannot be equal in a Relationship.");

                    // Retrieve the target Node name and generate a new Neighbour
                    TNodeTemplate targetNodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
                    String neighName = nodes.get(Long.valueOf(targetNodeTemplate.getId())).getName();
                    //this manages invalid/inexistent node ID for target node
                    Neighbour neigh = new Neighbour();
                    neigh.setName(neighName);
                    neigh.setId(Long.valueOf(relationshipTemplate.getId()));

                    //Retrieve the Neighbour map of the source Node and add the Neighbour
                    TNodeTemplate sourceNodeTemplate = (TNodeTemplate) relationshipTemplate.getSourceElement().getRef();
                    Node source = nodes.get(Long.valueOf(sourceNodeTemplate.getId()));
                    //this manages invalid/inexistent node ID for source node
                    Map<Long,Neighbour> sourceNodeNeighMap = source.getNeighbours();
                    if(sourceNodeNeighMap.containsKey(neigh.getId()))
                        throw new BadRequestException("The RelationshipTemplate ID must be unique.");
                    else
                        sourceNodeNeighMap.put(neigh.getId(), neigh);
                    source.setNeighbours(sourceNodeNeighMap);

                    //Update the Node list
                    nodes.put(Long.valueOf(sourceNodeTemplate.getId()), source);
                } catch(NullPointerException | NumberFormatException ex) {
                    throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
                }
            }
        }
    }

    private static Configuration mapToscaConfiguration(it.polito.tosca.jaxb.Configuration configuration)
            throws JsonProcessingException, IOException {
        Configuration conf = new Configuration();
        ObjectMapper mapper = new ObjectMapper();
        String stringConfiguration;

        //Retrieve configuration ID (optional)
        if (configuration.getConfID() != null)
            conf.setId(configuration.getConfID());
        else
            conf.setId("");

        //Retrieve description (optional)
        if (configuration.getConfDescr() != null)
            conf.setDescription(configuration.getConfDescr());
        else
            conf.setDescription("");

        //Retrieve string of configuration
        try {
            stringConfiguration = MappingUtils.obtainStringConfiguration(configuration);
        } catch(IOException ex) {
            conf.setConfiguration(mapper.readTree("[]"));
            System.out.println("[WARNING] Provided default configuration.");
            return conf;
        }

        //Retrieve JsonNode from the string of configuration
        try {
            conf.setConfiguration(mapper.readTree(stringConfiguration));
            return conf;
        } catch (IOException e) {
            throw new BadRequestException("NodeTemplate configuration is invalid.");
        }
    }
}
