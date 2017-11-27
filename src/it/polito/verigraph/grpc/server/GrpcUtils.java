/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.grpc.server;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import it.polito.verigraph.grpc.ConfigurationGrpc;
import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.grpc.TestGrpc;
import it.polito.verigraph.grpc.VerificationGrpc;
import it.polito.verigraph.grpc.NodeGrpc.FunctionalType;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Test;
import it.polito.verigraph.model.Verification;
//new import
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.tosca.ToscaTestGrpc;
import it.polito.verigraph.grpc.tosca.ToscaConfigurationGrpc;
import it.polito.verigraph.grpc.tosca.ToscaVerificationGrpc;


public class GrpcUtils {
    private static final Logger logger = Logger.getLogger(GrpcUtils.class.getName());

    public static NeighbourGrpc obtainNeighbour(Neighbour ne){
        return NeighbourGrpc.newBuilder()
                .setId(ne.getId())
                .setName(ne.getName())
                .build();
    }

    public static Neighbour deriveNeighbour(NeighbourGrpc request) {
        //id is not present
        Neighbour ne = new Neighbour();
        ne.setName(request.getName());
        return ne;
    }

    public static ConfigurationGrpc obtainConfiguration(Configuration conf){
        return ConfigurationGrpc.newBuilder()
                .setId(conf.getId())
                .setDescription(conf.getDescription())
                .setConfiguration(conf.getConfiguration().toString())
                .build();
    }

    public static Configuration deriveConfiguration(ConfigurationGrpc request) {
        Configuration conf = new Configuration();
        conf.setId(request.getId());
        conf.setDescription(request.getDescription());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            if ("".equals(request.getConfiguration()))
                rootNode=mapper.readTree("[]");
            else
                rootNode = mapper.readTree(request.getConfiguration());
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        conf.setConfiguration(rootNode);
        return conf;
    }

    public static NodeGrpc obtainNode(Node node) {
        NodeGrpc.Builder nr = NodeGrpc.newBuilder();
        nr.setId(node.getId());
        nr.setName(node.getName());
        nr.setFunctionalType(FunctionalType.valueOf(node.getFunctional_type()));
        for(Neighbour neighbour:node.getNeighbours().values()){
            NeighbourGrpc ng = obtainNeighbour(neighbour);
            nr.addNeighbour(ng);
        }
        nr.setConfiguration(obtainConfiguration(node.getConfiguration()));
        return nr.build();
    }

    public static Node deriveNode(NodeGrpc request) {
        //id is not present
        Node node = new Node();
        node.setName(request.getName());
        node.setFunctional_type(request.getFunctionalType().toString());
        Configuration conf = deriveConfiguration(request.getConfiguration());
        node.setConfiguration(conf);

        Map<Long,Neighbour> neighours = node.getNeighbours();
        long i = 1;
        for(NeighbourGrpc neighbour:request.getNeighbourList()){
            Neighbour ng = deriveNeighbour(neighbour);
            neighours.put(i++, ng);
        }

        return node;
    }

    public static GraphGrpc obtainGraph(Graph graph){
        GraphGrpc.Builder gr = GraphGrpc.newBuilder();
        gr.setId(graph.getId());
        for(Node node : graph.getNodes().values()){
            NodeGrpc ng = obtainNode(node);
            gr.addNode(ng);
        }
        return gr.build();
    }
    
    public static Graph deriveGraph(GraphGrpc request) {
        //id is not present
        Graph graph = new Graph();

        long i=1;
        Map<Long, Node> nodes= graph.getNodes();
        for(NodeGrpc node:request.getNodeList()){
            Node ng = deriveNode(node);
            nodes.put(i++, ng);  
        }
        return graph;
    }

    public static VerificationGrpc obtainVerification(Verification verify){
        VerificationGrpc.Builder ver = VerificationGrpc.newBuilder();
        ver.setComment(verify.getComment());
        ver.setResult(verify.getResult());
        for(Test test:verify.getTests()){
            TestGrpc.Builder tst = TestGrpc.newBuilder().setResult(test.getResult());
            for(Node node:test.getPath()){
                NodeGrpc ng = obtainNode(node);
                tst.addNode(ng);
            }
            ver.addTest(tst);
        }
        return ver.build();
    }
    
    /** Mapping method --> from Graph to TopologyTemplate */
    public static ToscaVerificationGrpc obtainToscaVerification(Verification verify){
        ToscaVerificationGrpc.Builder ver = ToscaVerificationGrpc.newBuilder();
        ver.setComment(verify.getComment());
        ver.setResult(verify.getResult());
        for(Test test:verify.getTests()){
            ToscaTestGrpc.Builder tst = ToscaTestGrpc.newBuilder().setResult(test.getResult());
            for(Node node:test.getPath()){
                NodeTemplateGrpc nodetempl = obtainNodeTemplate(node);
                tst.addNodeTemplate(nodetempl);
            }
            ver.addTest(tst);
        }
        return ver.build();
    }
    
    /** Mapping method --> from Graph to TopologyTemplate */
    public static TopologyTemplateGrpc obtainTopologyTemplate(Graph graph) {
    	TopologyTemplateGrpc.Builder topol = TopologyTemplateGrpc.newBuilder();
    	topol.setId(graph.getId());
    	
    	//NodeTemplate 
    	for(Node node : graph.getNodes().values()) {
    		NodeTemplateGrpc nt = obtainNodeTemplate(node);
    		topol.addNodeTemplate(nt);
    		//RelationshipTemplate
    		Map<Long,Neighbour> neighMap = node.getNeighbours();
    		for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
    		    Neighbour neigh = myentry.getValue();
    		    RelationshipTemplateGrpc relat = obtainRelationshipTemplate(neigh, nt.getId());
    		    topol.addRelationshipTemplate(relat);
    		}
    	}
    	return topol.build();
    }
    
    /** Mapping method --> from TopologyTemplate to Graph */
    public static Graph deriveGraph(TopologyTemplateGrpc request) {
        Graph graph = new Graph();
        Map<Long, Node> nodes = graph.getNodes();
        for(NodeTemplateGrpc nodetempl : request.getNodeTemplateList()){
        	Node node = deriveNode(nodetempl, request); //Topology is necessary to obtain Neighbour
        	nodes.put(node.getId(), node);
        }
        return graph;
    }
    
    /** Mapping method --> from Node to NodeTemplate */
    public static NodeTemplateGrpc obtainNodeTemplate(Node node){
    	NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder();
    	
    	nodegrpc.setId(node.getId());
    	nodegrpc.setName(node.getName());
    	nodegrpc.setType(NodeTemplateGrpc.Type.valueOf(node.getFunctional_type().toLowerCase()));
    	
    	ToscaConfigurationGrpc config = obtainToscaConfiguration(node.getConfiguration());
    	nodegrpc.setConfiguration(config); 
    	
    	return nodegrpc.build();
    }
    
    /** Mapping method --> from NodeTemplate to Node */
    public static Node deriveNode(NodeTemplateGrpc nodegrpc, TopologyTemplateGrpc request) {
    	Node node = new Node();
    	Map<Long,Neighbour> neighbours = node.getNeighbours();
    	
    	node.setId(nodegrpc.getId());
    	node.setName(nodegrpc.getName());
    	Configuration conf = deriveConfiguration(nodegrpc.getConfiguration());
    	node.setConfiguration(conf);
    	for(RelationshipTemplateGrpc relat : request.getRelationshipTemplateList()){
    		if(relat.getIdSourceNodeTemplate() == node.getId()) {
    			Neighbour neigh = deriveNeighbour(relat);
    			neighbours.put(neigh.getId(), neigh);
    		}
    	}
    	node.setNeighbours(neighbours);
    	node.setFunctional_type(nodegrpc.getType().toString());
    	return node;
    }
    
    /** Mapping method --> from Neighbour to RelationshipTemplate */
    public static RelationshipTemplateGrpc obtainRelationshipTemplate(Neighbour neigh, long sourceID) {
    	RelationshipTemplateGrpc.Builder relat = RelationshipTemplateGrpc.newBuilder();
    	relat.setId(neigh.getId());
    	relat.setIdSourceNodeTemplate(sourceID);
    	relat.setIdTargetNodeTemplate(neigh.getId());
    	relat.setName(neigh.getName());
    	return relat.build();
    }
    
    /** Mapping method --> from RelationshipTemplate to Neighbour */
    public static Neighbour deriveNeighbour(RelationshipTemplateGrpc relat) {
    	Neighbour neigh = new Neighbour();
    	neigh.setName(relat.getName());
    	neigh.setId(relat.getId());
    	return neigh;
    }
    
    /** Mapping method --> from model Configuration to ToscaConfigurationGrpc */
    public static ToscaConfigurationGrpc obtainToscaConfiguration(Configuration conf) {
    	return ToscaConfigurationGrpc.newBuilder()
                .setId(conf.getId())
                .setDescription(conf.getDescription())
                .setConfiguration(conf.getConfiguration().toString())
                .build();
    }
    
    /** Mapping method --> from ToscaConfiguration to model Configuration */
    public static Configuration deriveConfiguration(ToscaConfigurationGrpc request) {
    	Configuration conf = new Configuration();
        conf.setId(request.getId());
        conf.setDescription(request.getDescription());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            if ("".equals(request.getConfiguration()))
                rootNode=mapper.readTree("[]");
            else
                rootNode = mapper.readTree(request.getConfiguration());
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
        conf.setConfiguration(rootNode);
        return conf;
    }
    
    /** Intended for string that begins with "?" */
    public static Map<String,String> getParamGivenString(String str){
        String string = str.substring(1);
        final Map<String, String> map = Splitter.on('&').trimResults().withKeyValueSeparator("=").
                split(string);
        return map;
    }
 
}
