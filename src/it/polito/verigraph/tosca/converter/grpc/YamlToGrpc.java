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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.NodeTemplateGrpc.Type;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.tosca.YamlParsingUtils;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;

public class YamlToGrpc {

    /** Returns the (first) TopologyTemplate found in the TOSCA-compliant yaml file */
    public static TopologyTemplateGrpc obtainTopologyTemplateGrpc (String filepath)
            throws IOException, JAXBException, DataNotFoundException, ClassCastException, BadRequestException{
        ServiceTemplateYaml serviceTemplate = YamlParsingUtils.obtainServiceTemplate(filepath);

        //Retrieving of list of NodeTemplate and RelationshipTemplate
        List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
        List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();

        try {
            for(Map.Entry<String, NodeTemplateYaml> node : YamlParsingUtils.obtainNodeTemplates(serviceTemplate).entrySet()) {
                for(NodeTemplateGrpc alreadyAddedNode : nodes)
                    if(alreadyAddedNode.getId().equals(node.getKey()))
                        throw new BadRequestException("The NodeTemplate ID must be unique.");
                nodes.add(parseNodeTemplate(node));
            }
        } catch (NullPointerException e) {
            throw new BadRequestException("There is not any NodeTemplate in the ServiceTemplate provided.");
        }

        try {
            for(Map.Entry<String, RelationshipTemplateYaml> rel : YamlParsingUtils.obtainRelationshipTemplates(serviceTemplate).entrySet()) {
                relats.add(parseRelationshipTemplate(rel, nodes));
            }
        } catch (NullPointerException e) {
            throw new BadRequestException("There is not any RelationshipTemplate in the ServiceTemplate provided.");
        }

        //Creating TopologyTemplateGrpc object to be sent to server
        return TopologyTemplateGrpc.newBuilder()
                .setId("0") //useless value since the server chooses the actual value for the GraphID
                .addAllNodeTemplate(nodes)
                .addAllRelationshipTemplate(relats)
                .build();
    }

    /** Parsing method: RelationshipTemplateYaml(tosca) --> RelationshipTemplateGrpc */
    private static RelationshipTemplateGrpc parseRelationshipTemplate(Entry<String, RelationshipTemplateYaml> rel, List<NodeTemplateGrpc> nodes) throws BadRequestException{
        String source, target;
        boolean valid_source = false;
        boolean valid_target = false;

        //RelationshipTemplateGrpc building
        RelationshipTemplateGrpc.Builder relatgrpc = RelationshipTemplateGrpc.newBuilder();  

        //ID can be null
        try {
            relatgrpc.setId(rel.getKey());
            relatgrpc.setName(rel.getValue().getProperties().get("name"));
            source = rel.getValue().getProperties().get("source_id");
            target = rel.getValue().getProperties().get("target_id");
        } catch (NullPointerException ex) {
            throw new BadRequestException("Incorrect fields in RelationshipTemplate:" + rel.getKey());
        }

        //Source and Target values must correctly refer to a NodeTemplate
        if(source.equals(target))
            throw new BadRequestException("Source and Target cannot be the same value");
        for(NodeTemplateGrpc node : nodes) {
            if(node.getId().equals(source))
                valid_source = true;
            if(node.getId().equals(target))
                valid_target = true;
        }
        if(!(valid_source && valid_target))
            throw new BadRequestException("Invalid NodeTemplate reference in RelationshipTemplate:" + rel.getKey());

        return relatgrpc.setIdSourceNodeTemplate(source).setIdTargetNodeTemplate(target).build();

    }

    /** Parsing method: NodeTemplateYaml(tosca) --> NodeTemplateGrpc */
    private static NodeTemplateGrpc parseNodeTemplate(Entry<String, NodeTemplateYaml> node)
            throws ClassCastException, NullPointerException, BadRequestException {   
        Boolean isVerigraphCompl = true;
        Type type;

        //NodeTemplateGrpc building
        NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder()
                .setId(node.getKey());

        try {
            nodegrpc.setName(node.getValue().getName());
        } catch (NullPointerException ex) {
            throw new BadRequestException("Invalid name in a NodeTemplate.");
        }

        //Type cannot be null but it can be invalid
        try {
            type = Type.valueOf(node.getValue().getType().replace("verigraph.nodeTypes.", "").toLowerCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            //in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be a fieldmodifier node
            type = Type.fieldmodifier;
            isVerigraphCompl = false;
        }
        nodegrpc.setType(type);
        ToscaConfigurationGrpc.Builder grpcConfig;   
        if(isVerigraphCompl) {
            String jsonConfig = YamlParsingUtils.obtainConfiguration(node.getValue());
            grpcConfig = ToscaConfigurationGrpc.newBuilder()
                    .setId("")
                    .setDescription("")
                    .setConfiguration(jsonConfig);
        }
        else {
            grpcConfig = ToscaConfigurationGrpc.newBuilder()
                    .setId(ToscaGrpcUtils.defaultConfID)
                    .setDescription(ToscaGrpcUtils.defaultDescr)
                    .setConfiguration(ToscaGrpcUtils.defaultConfig);
        }   
        nodegrpc.setConfiguration(grpcConfig.build());
        return nodegrpc.build();
    }

}