/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.converter.grpc;

import java.util.Map;

import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.grpc.ToscaTestGrpc;
import it.polito.verigraph.grpc.ToscaVerificationGrpc;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Test;
import it.polito.verigraph.model.Verification;

public class GraphToGrpc {

    /** Mapping method --> from model Graph to grpc TopologyTemplate */
    public static TopologyTemplateGrpc obtainTopologyTemplate(Graph graph) {
        TopologyTemplateGrpc.Builder topol = TopologyTemplateGrpc.newBuilder();
        topol.setId(String.valueOf(graph.getId()));

        //NodeTemplate
        for(Node node : graph.getNodes().values()) {
            NodeTemplateGrpc nt = obtainNodeTemplate(node);
            topol.addNodeTemplate(nt);
            //RelationshipTemplate
            Map<Long,Neighbour> neighMap = node.getNeighbours();
            for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
                Neighbour neigh = myentry.getValue();
                RelationshipTemplateGrpc relat = obtainRelationshipTemplate(neigh, node);
                topol.addRelationshipTemplate(relat);
            }
        }
        return topol.build();
    }


    /** Mapping method --> from model Node to grpc NodeTemplate */
    private static NodeTemplateGrpc obtainNodeTemplate(Node node){
        NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder();

        nodegrpc.setId(String.valueOf(node.getId()));
        nodegrpc.setName(node.getName());
        nodegrpc.setType(NodeTemplateGrpc.Type.valueOf(node.getFunctional_type().toLowerCase()));

        ToscaConfigurationGrpc config = obtainToscaConfiguration(node.getConfiguration());
        nodegrpc.setConfiguration(config);

        return nodegrpc.build();
    }


    /** Mapping method --> from model Neighbour to grpc RelationshipTemplate */
    private static RelationshipTemplateGrpc obtainRelationshipTemplate(Neighbour neigh, Node sourceNode) {
        RelationshipTemplateGrpc.Builder relat = RelationshipTemplateGrpc.newBuilder();
        relat.setId(String.valueOf(sourceNode.getId()));
        //Neighbour does not have a neighbourID! RelationshipTemplate does, so it is set to sourceNodeID
        relat.setIdSourceNodeTemplate(String.valueOf(sourceNode.getId()));
        relat.setIdTargetNodeTemplate(String.valueOf(neigh.getId()));
        relat.setName(sourceNode.getName()+"to"+neigh.getName());
        return relat.build();
    }


    /** Mapping method --> from model Configuration to grpc ToscaConfigurationGrpc */
    private static ToscaConfigurationGrpc obtainToscaConfiguration(Configuration conf) {
        return ToscaConfigurationGrpc.newBuilder()
                .setId(conf.getId())
                .setDescription(conf.getDescription())
                .setConfiguration(conf.getConfiguration().toString())
                .build();
    }

    /** Mapping method --> from model Verification to grpc ToscaVerificationGrpc */
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

}
