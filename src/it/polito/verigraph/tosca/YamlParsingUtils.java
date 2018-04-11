/*******************************************************************************
 * Copyright (c) 2017/18 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.InvalidServiceTemplateException;
import it.polito.verigraph.tosca.serializer.YamlConfigSerializer;
import it.polito.verigraph.tosca.yaml.beans.AntispamNode;
import it.polito.verigraph.tosca.yaml.beans.CacheNode;
import it.polito.verigraph.tosca.yaml.beans.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.DpiNode;
import it.polito.verigraph.tosca.yaml.beans.EndhostNode;
import it.polito.verigraph.tosca.yaml.beans.EndpointNode;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierNode;
import it.polito.verigraph.tosca.yaml.beans.FirewallNode;
import it.polito.verigraph.tosca.yaml.beans.MailClientNode;
import it.polito.verigraph.tosca.yaml.beans.MailServerNode;
import it.polito.verigraph.tosca.yaml.beans.NatNode;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.TopologyTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessNode;
import it.polito.verigraph.tosca.yaml.beans.VpnExitNode;
import it.polito.verigraph.tosca.yaml.beans.WebClientNode;
import it.polito.verigraph.tosca.yaml.beans.WebServerNode;

public class YamlParsingUtils {

    public static ServiceTemplateYaml obtainServiceTemplate(String filePath) throws InvalidServiceTemplateException {
        ServiceTemplateYaml yamlServiceTemplate = new ServiceTemplateYaml();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        try {
            yamlServiceTemplate = mapper.readValue(new File(filePath), ServiceTemplateYaml.class);
            return yamlServiceTemplate;

        } catch (JsonParseException e) {
            throw new InvalidServiceTemplateException("The NodeTemplate IDs and the RelationshipTemplate IDs must be unique.");
        } catch (JsonMappingException e) {
            throw new InvalidServiceTemplateException("The provided file does not match the expected structure.");
        } catch (InvalidServiceTemplateException e) {
            throw new InvalidServiceTemplateException("The provided template contains errors or missing informations.");
        } catch (IOException e) {
            throw new InvalidServiceTemplateException("I/O error.");
        }

    }


    public static Map<String, NodeTemplateYaml> obtainNodeTemplates(ServiceTemplateYaml yamlService) throws DataNotFoundException {
        TopologyTemplateYaml yamlTopology;
        try {
            yamlTopology = yamlService.getTopology_template();
        } catch(NullPointerException ex) {
            throw new DataNotFoundException("The ServiceTemplate provided does not contain a TopologyTemplate.");
        }
        try {
            Map<String, NodeTemplateYaml> nodes = yamlTopology.getNode_templates();
            return nodes;
        } catch(NullPointerException ex) {
            throw new DataNotFoundException("The ServiceTemplate provided does not contain any NodeTemplates.");
        }

    }


    public static Map<String, RelationshipTemplateYaml> obtainRelationshipTemplates(ServiceTemplateYaml yamlService) throws DataNotFoundException {
        TopologyTemplateYaml yamlTopology;
        try {
            yamlTopology = yamlService.getTopology_template();
        } catch(NullPointerException ex) {
            throw new DataNotFoundException("The ServiceTemplate provided does not contain a TopologyTemplate.");
        }
        try {
            Map<String,RelationshipTemplateYaml> relats = yamlTopology.getRelationship_templates();
            return relats;
        } catch(NullPointerException ex) {
            throw new DataNotFoundException("The ServiceTemplate provided does not contain any RelationshipTemplates.");
        }

    }


    public static String obtainConfiguration(NodeTemplateYaml node) throws BadRequestException {
        ConfigurationYaml yamlConfiguration = null;
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ConfigurationYaml.class, new YamlConfigSerializer());
        mapper.registerModule(module);

        // Find out node type, retrieve the corresponding configuration and convert it properly
        try {
            if(node instanceof AntispamNode) {
                yamlConfiguration = ((AntispamNode)node).getProperties();
            }else if(node instanceof CacheNode) {
                yamlConfiguration = ((CacheNode)node).getProperties();
            }else if(node instanceof DpiNode) {
                yamlConfiguration = ((DpiNode)node).getProperties();
            }else if(node instanceof EndhostNode) {
                yamlConfiguration = ((EndhostNode)node).getProperties();
            }else if(node instanceof EndpointNode) {
                yamlConfiguration = ((EndpointNode)node).getProperties();
            }else if(node instanceof FieldModifierNode) {
                yamlConfiguration = ((FieldModifierNode)node).getProperties();
            }else if(node instanceof FirewallNode) {
                yamlConfiguration = ((FirewallNode)node).getProperties();
            }else if(node instanceof MailClientNode) {
                yamlConfiguration = ((MailClientNode)node).getProperties();
            }else if(node instanceof MailServerNode) {
                yamlConfiguration = ((MailServerNode)node).getProperties();
            }else if(node instanceof NatNode) {
                yamlConfiguration = ((NatNode)node).getProperties();
            }else if(node instanceof VpnAccessNode) {
                yamlConfiguration = ((VpnAccessNode)node).getProperties();
            }else if(node instanceof VpnExitNode) {
                yamlConfiguration = ((VpnExitNode)node).getProperties();
            }else if(node instanceof WebClientNode) {
                yamlConfiguration = ((WebClientNode)node).getProperties();
            }else if(node instanceof WebServerNode) {
                yamlConfiguration = ((WebServerNode)node).getProperties();
            }else {
                throw new BadRequestException("The provided node is of unknown type, unable to retrieve the node configuration");
            }

            String stringConfiguration = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlConfiguration);
            if (!stringConfiguration.equals("null"))
                return stringConfiguration;
            else
                return "[]";

        } catch (JsonProcessingException | NullPointerException e) {
            throw new BadRequestException("Not able to retrieve a valid configuration");
        }
    }

}