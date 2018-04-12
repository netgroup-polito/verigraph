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
import java.util.List;

import javax.xml.namespace.QName;

import it.polito.tosca.jaxb.Configuration;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TEntityTemplate.Properties;
import it.polito.tosca.jaxb.TNodeTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate.SourceElement;
import it.polito.tosca.jaxb.TRelationshipTemplate.TargetElement;
import it.polito.tosca.jaxb.TServiceTemplate;
import it.polito.tosca.jaxb.TTopologyTemplate;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.tosca.MappingUtils;

public class GrpcToXml {

    public static Definitions mapGraph(TopologyTemplateGrpc topologyGrpc) {
        Definitions definitions = new Definitions();
        TServiceTemplate serviceTemplate = new TServiceTemplate();
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();

        for(NodeTemplateGrpc node : topologyGrpc.getNodeTemplateList()) {
            TNodeTemplate nodeTemplate = mapNode(node);
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);
        }
        for(RelationshipTemplateGrpc relat : topologyGrpc.getRelationshipTemplateList()) {
            TRelationshipTemplate relationshipTemplate = mapRelationship(relat, topologyGrpc.getNodeTemplateList());
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(relationshipTemplate);
        }

        try {
            serviceTemplate.setId(String.valueOf(topologyGrpc.getId()));
        } catch (NullPointerException e) {
            throw new NullPointerException("The TopologyTemplateGrpc must have an ID.");
        }
        serviceTemplate.setTopologyTemplate(topologyTemplate);
        definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);
        return definitions;
    }


    private static TNodeTemplate mapNode(NodeTemplateGrpc node){
        TNodeTemplate nodeTemplate = new TNodeTemplate();

        try {
            nodeTemplate.setId(String.valueOf(node.getId()));
        } catch (NullPointerException e) {
            throw new NullPointerException("The NodeTemplateGrpc must have an ID.");
        }
        try {
            nodeTemplate.setName(node.getName());
        } catch (NullPointerException e) {
            throw new NullPointerException("The NodeTemplateGrpc must have a name.");
        }

        try {
            //QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaVerigraphDefinition")
            QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12",
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1) + "Type");
            nodeTemplate.setType(type);
        } catch (NullPointerException e) {
            throw new NullPointerException("The NodeTemplateGrpc must have a valid type.");
        }

        Configuration config = mapModelConfiguration(node.getConfiguration(), node.getType().toString().toLowerCase());
        nodeTemplate.setProperties(new Properties());
        nodeTemplate.getProperties().setAny(config);
        return nodeTemplate;
    }


    private static TRelationshipTemplate mapRelationship(RelationshipTemplateGrpc relat, List<NodeTemplateGrpc> nodeList) {
        TRelationshipTemplate relationship = new TRelationshipTemplate();
        SourceElement source = new SourceElement();
        TargetElement target = new TargetElement();
        int check = 0;

        TNodeTemplate sourceNode = new TNodeTemplate();
        TNodeTemplate targetNode = new TNodeTemplate();

        try {
            for(NodeTemplateGrpc node : nodeList) {
                if(node.getId().equals(relat.getIdSourceNodeTemplate())) {
                    sourceNode = mapNode(node);
                    check++;
                }
                if(node.getId().equals(relat.getIdTargetNodeTemplate())) {
                    targetNode = mapNode(node);
                    check++;
                }
            }
        } catch (NullPointerException e) {
            throw new BadRequestException("A RelationshipTemplateGrpc must contain both source and target node ID.");
        }
        if(check != 2)
            throw new BadRequestException("A RelationshipTemplateGrpc must contain both source and target node ID.");

        source.setRef(sourceNode);
        target.setRef(targetNode);

        relationship.setId(relat.getId()); //TODO da valutare
        relationship.setSourceElement(source);
        relationship.setTargetElement(target);
        relationship.setName(sourceNode.getName()+"To"+targetNode.getName());

        return relationship;
    }


    private static it.polito.tosca.jaxb.Configuration mapModelConfiguration(ToscaConfigurationGrpc toscaConfigurationGrpc, String type) {
        it.polito.tosca.jaxb.Configuration configuration = new it.polito.tosca.jaxb.Configuration();
        try {
            //We are passing the configuration type to the Deserializer context
            configuration = MappingUtils.obtainToscaConfiguration(toscaConfigurationGrpc, type);

            //In Graph, ID and DESCRIPTION are always empty
            //configuration.setConfID(confGrpc.getId());
            //configuration.setConfDescr(confGrpc.getDescription());

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return configuration;
    }

}

