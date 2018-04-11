/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.converter.grpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class GrpcToGraph {

    /** Mapping method --> from grpc TopologyTemplateGrpc to model Graph */
    public static Graph deriveGraph(TopologyTemplateGrpc request) throws BadRequestException, JsonProcessingException, IOException {
        Graph graph = new Graph();
        Map<Long, Node> nodes = new HashMap<>();

        try {
            //Create a list of Node without Neighbour
            for(NodeTemplateGrpc nodetempl : request.getNodeTemplateList()){
                Node node = deriveNode(nodetempl);
              //It necessary to check uniqueness here otherwise a .put with the same key will overwrite the old node
                if(nodes.containsKey(node.getId()))
                    throw new BadRequestException("The NodeTemplate ID must be unique.");
                else
                    nodes.put(node.getId(), node);
            }

            //Add Neighbour to the Node of the list
            List<RelationshipTemplateGrpc> relatList = request.getRelationshipTemplateList();
            nodes = deriveNeighboursNode(nodes, relatList);

            //Add Node and ID to the graph
            graph.setNodes(nodes);
            try {
                graph.setId(Long.valueOf(request.getId()));
            } catch(NumberFormatException ex) {
                throw new BadRequestException("If you want to store your TopologyTemplate on this server,"
                        + "the TopologyTemplate ID must be a number.");
            }

            return graph;

        } catch (NullPointerException e) {
            throw new BadRequestException("The TopologyTemplate received has invalid fields.");
        }

    }


    /** Mapping method --> from grpc NodeTemplate to model Node (with no Neighbour) */
    private static Node deriveNode(NodeTemplateGrpc nodegrpc) throws BadRequestException, JsonProcessingException, IOException {
        Node node = new Node();
        try {
            try {
                node.setId(Long.valueOf(nodegrpc.getId()));
            } catch(NumberFormatException ex) {
                throw new BadRequestException("The NodeTemplate ID must be a number.");
            }

            node.setName(nodegrpc.getName());
            Configuration conf = deriveConfiguration(nodegrpc.getConfiguration());
            node.setConfiguration(conf);
            node.setFunctional_type(nodegrpc.getType().toString());

        } catch(NullPointerException ex) {
            throw new BadRequestException("A NodeTemplate has wrong fields representation.");
        }

        return node;
    }



    /** Mapping method --> from a list of model Node to a list of model Node with their Neighbour */
    private static Map<Long,Node> deriveNeighboursNode(Map<Long,Node> nodes, List<RelationshipTemplateGrpc> relatList)
            throws BadRequestException{
        Map<Long,Node> updNodes = nodes; //new list to be filled with updated Node (update = Node + its Neighbour)
        for(RelationshipTemplateGrpc relat : relatList) {
            try {
                //Retrieve the target Node name and generate a new Neighbour
                String neighName = updNodes.get(Long.valueOf(relat.getIdTargetNodeTemplate())).getName();
                Neighbour neigh = new Neighbour();
                neigh.setName(neighName);
                neigh.setId(Long.valueOf(relat.getId()));

                //Retrieve the Neighbour map of the source Node and add the Neighbour
                Node source = updNodes.get(Long.valueOf(relat.getIdSourceNodeTemplate()));
                Map<Long,Neighbour> sourceNodeNeighMap = source.getNeighbours();
                if(sourceNodeNeighMap.containsKey(neigh.getId()))
                    throw new BadRequestException("The RelationshipTemplate ID must be unique.");
                else
                    sourceNodeNeighMap.put(neigh.getId(), neigh);
                source.setNeighbours(sourceNodeNeighMap);

                //Update the Node list
                updNodes.put(Long.valueOf(relat.getIdSourceNodeTemplate()), source);
            } catch(NullPointerException | NumberFormatException ex) {
                throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
            }
        }
        return updNodes;
    }

    /** Mapping method --> from ToscaConfiguration to model Configuration */
    private static Configuration deriveConfiguration(ToscaConfigurationGrpc request)
            throws BadRequestException, JsonProcessingException, IOException {
        Configuration conf = new Configuration();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            conf.setId(request.getId());
        } catch (NullPointerException e) {}

        try {
            conf.setDescription(request.getDescription());
        } catch (NullPointerException e) {}

        try {
            if ("".equals(request.getConfiguration()))
                rootNode=mapper.readTree("[]");
            else
                rootNode = mapper.readTree(request.getConfiguration());
        } catch (NullPointerException e) {
            rootNode=mapper.readTree("[]");
        }
        conf.setConfiguration(rootNode);
        return conf;
    }
}
