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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.tosca.deserializer.YamlConfigurationDeserializer;
import it.polito.verigraph.tosca.yaml.beans.AntispamConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.AntispamNode;
import it.polito.verigraph.tosca.yaml.beans.CacheConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.CacheNode;
import it.polito.verigraph.tosca.yaml.beans.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.DpiConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.DpiNode;
import it.polito.verigraph.tosca.yaml.beans.EndhostConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndhostNode;
import it.polito.verigraph.tosca.yaml.beans.EndpointConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndpointNode;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierNode;
import it.polito.verigraph.tosca.yaml.beans.FirewallConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FirewallNode;
import it.polito.verigraph.tosca.yaml.beans.MailClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailClientNode;
import it.polito.verigraph.tosca.yaml.beans.MailServerConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailServerNode;
import it.polito.verigraph.tosca.yaml.beans.NatConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.NatNode;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.TopologyTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessNode;
import it.polito.verigraph.tosca.yaml.beans.VpnExitConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnExitNode;
import it.polito.verigraph.tosca.yaml.beans.WebClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebClientNode;
import it.polito.verigraph.tosca.yaml.beans.WebServerConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebServerNode;

public class GrpcToYaml {
    public static ServiceTemplateYaml mapGraphYaml(TopologyTemplateGrpc topologyGrpc) {
        ServiceTemplateYaml serviceYaml = new ServiceTemplateYaml();
        TopologyTemplateYaml topologyYaml = new TopologyTemplateYaml();

        topologyYaml.setNode_templates(new HashMap<String,NodeTemplateYaml>());
        topologyYaml.setRelationship_templates(new HashMap<String,RelationshipTemplateYaml>());
        serviceYaml.setMetadata(new HashMap<String,String>());

        for(NodeTemplateGrpc node : topologyGrpc.getNodeTemplateList()) {
            NodeTemplateYaml nodeTemplate = new NodeTemplateYaml();
            try {
                nodeTemplate = mapNodeYaml(node);
            } catch (IOException e) {
                throw new BadRequestException("Error while mapping a Node in Yaml object.");
            }
            topologyYaml.getNode_templates().put(String.valueOf(node.getId()), nodeTemplate);
            //shall we catch NumberFormatException?
        }
        for(RelationshipTemplateGrpc relationship : topologyGrpc.getRelationshipTemplateList()) {
            RelationshipTemplateYaml rel = mapRelationshipYaml(relationship, topologyGrpc.getNodeTemplateList());
            topologyYaml.getRelationship_templates().put(String.valueOf(relationship.getId()), rel);
        }

        serviceYaml.getMetadata().put("template_id", String.valueOf(topologyGrpc.getId()));
        serviceYaml.setTopology_template(topologyYaml);
        return serviceYaml;
    }


    private static NodeTemplateYaml mapNodeYaml(NodeTemplateGrpc node) throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SimpleModule module = new SimpleModule();
        String stringConfig = null;
        try {
            stringConfig = node.getConfiguration().getConfiguration();
        } catch (NullPointerException e) {
            throw new NullPointerException("A NodeTemplateGrpc does not contain the configuration.");
        }
        JsonNode nodeConfig = mapper.readTree(stringConfig);
        //Passing the configuration type to the Deserializer context
        module.addDeserializer(ConfigurationYaml.class, new YamlConfigurationDeserializer());
        mapper.registerModule(module);

        if(node.getType() == null)
            throw new NullPointerException("A NodeTemplateGrpc does not contain a type.");
        ConfigurationYaml yamlConfig = mapper
                .reader(new InjectableValues.Std().addValue("type", node.getType().toString().toLowerCase()))
                .forType(ConfigurationYaml.class)
                .readValue(nodeConfig);


        FunctionalTypes nodeType = FunctionalTypes.valueOf(node.getType().toString().toUpperCase());
        switch(nodeType) {
        case ANTISPAM:
            AntispamNode antispamNode = new AntispamNode();
            antispamNode.setName(node.getName());
            antispamNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            antispamNode.setProperties((AntispamConfigurationYaml) yamlConfig);
            return antispamNode;

        case CACHE:
            CacheNode cacheNode = new CacheNode();
            cacheNode.setName(node.getName());
            cacheNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            cacheNode.setProperties((CacheConfigurationYaml) yamlConfig);
            return cacheNode;

        case DPI:
            DpiNode dpiNode = new DpiNode();
            dpiNode.setName(node.getName());
            dpiNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            dpiNode.setProperties((DpiConfigurationYaml) yamlConfig);
            return dpiNode;

        case ENDHOST:
            EndhostNode endhostNode = new EndhostNode();
            endhostNode.setName(node.getName());
            endhostNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            endhostNode.setProperties((EndhostConfigurationYaml) yamlConfig);
            return endhostNode;

        case ENDPOINT:
            EndpointNode endpointNode = new EndpointNode();
            endpointNode.setName(node.getName());
            endpointNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            endpointNode.setProperties((EndpointConfigurationYaml) yamlConfig);
            return endpointNode;

        case FIELDMODIFIER:
            FieldModifierNode fieldmodifierNode = new FieldModifierNode();
            fieldmodifierNode.setName(node.getName());
            fieldmodifierNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            fieldmodifierNode.setProperties((FieldModifierConfigurationYaml) yamlConfig);
            return fieldmodifierNode;

        case FIREWALL:
            FirewallNode firewallNode = new FirewallNode();
            firewallNode.setName(node.getName());
            firewallNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            firewallNode.setProperties((FirewallConfigurationYaml) yamlConfig);
            return firewallNode;

        case MAILCLIENT:
            MailClientNode mailclientNode = new MailClientNode();
            mailclientNode.setName(node.getName());
            mailclientNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            mailclientNode.setProperties((MailClientConfigurationYaml) yamlConfig);
            return mailclientNode;

        case MAILSERVER:
            MailServerNode mailserverNode = new MailServerNode();
            mailserverNode.setName(node.getName());
            mailserverNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            mailserverNode.setProperties((MailServerConfigurationYaml) yamlConfig);
            return mailserverNode;

        case NAT:
            NatNode natNode = new NatNode();
            natNode.setName(node.getName());
            natNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            natNode.setProperties((NatConfigurationYaml) yamlConfig);
            return natNode;

        case VPNACCESS:
            VpnAccessNode vpnaccessNode = new VpnAccessNode();
            vpnaccessNode.setName(node.getName());
            vpnaccessNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            vpnaccessNode.setProperties((VpnAccessConfigurationYaml) yamlConfig);
            return vpnaccessNode;

        case VPNEXIT:
            VpnExitNode vpnexitNode = new VpnExitNode();
            vpnexitNode.setName(node.getName());
            vpnexitNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            vpnexitNode.setProperties((VpnExitConfigurationYaml) yamlConfig);
            return vpnexitNode;

        case WEBCLIENT:
            WebClientNode webclientNode = new WebClientNode();
            webclientNode.setName(node.getName());
            webclientNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            webclientNode.setProperties((WebClientConfigurationYaml) yamlConfig);
            return webclientNode;

        case WEBSERVER:
            WebServerNode webserverNode = new WebServerNode();
            webserverNode.setName(node.getName());
            webserverNode.setType("verigraph.nodeTypes." +
                    node.getType().toString().substring(0, 1).toUpperCase() +
                    node.getType().toString().substring(1));
            webserverNode.setProperties((WebServerConfigurationYaml) yamlConfig);
            return webserverNode;

        default:
            FieldModifierNode defaultNode = new FieldModifierNode();
            defaultNode.setName(node.getName());
            defaultNode.setType("verigraph.nodeTypes.Fieldmodifier");
            defaultNode.setProperties(new FieldModifierConfigurationYaml());
            return defaultNode;
        }

    }

    private static RelationshipTemplateYaml mapRelationshipYaml(RelationshipTemplateGrpc relat, List<NodeTemplateGrpc> nodeList) {
        RelationshipTemplateYaml relationship = new RelationshipTemplateYaml();
        relationship.setProperties(new HashMap<String,String>());
        String sourceNode = null;
        String targetNode = null;
        int check = 0;

        relationship.setType("verigraph.relationshipType.generic");
        relationship.getProperties().put("source_id", String.valueOf(relat.getIdSourceNodeTemplate())); //to be catched?
        relationship.getProperties().put("target_id", String.valueOf(relat.getIdTargetNodeTemplate()));

        for(NodeTemplateGrpc node : nodeList) {
            if(node.getId().equals(relat.getIdSourceNodeTemplate())) {
                sourceNode = node.getName();
                check++;
            }
            if(node.getId().equals(relat.getIdTargetNodeTemplate())) {
                targetNode = node.getName();
                check++;
            }
        }

        if(check!=2)
            throw new BadRequestException("A RelationshipTemplateGrpc must contain both source and target node ID.");

        relationship.getProperties().put("name", sourceNode+"To"+targetNode);

        return relationship;
    }
}

