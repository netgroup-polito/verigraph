/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.InvalidServiceTemplateException;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.TopologyTemplateYaml;

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
			Map<String,NodeTemplateYaml> nodes = yamlTopology.getNode_templates();
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


	public static String obtainConfiguration(NodeTemplateYaml node) {
		// TODO Deve ritornare il json della configuration
		ObjectMapper mapper = new ObjectMapper();
		return "[]";
	}	

}