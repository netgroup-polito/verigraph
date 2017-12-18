/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.classes.Definitions;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;

public class TopologyTemplateService {

	private Neo4jDBManager manager = new Neo4jDBManager();
	public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

	public TopologyTemplateService() {}

		//XML methods

	public List<Definitions> getAllTopologyTemplates() throws JsonProcessingException, MyNotFoundException {
		List<Graph> verigraphResult;
		List<Definitions> result = new ArrayList<Definitions>();

		verigraphResult = manager.getGraphs();
		for(Graph g : verigraphResult){
			validateGraph(g);
			result.add(MappingUtils.mapGraph(g));
		}
		return result;
	}


	public Definitions addTopologyTemplate(Definitions topologyTemplate) throws JAXBException, IOException, MyInvalidIdException {
		Graph graph = MappingUtils.mapTopologyTemplate(topologyTemplate);
		validateGraph(graph);

		Graph newGraph = manager.addGraph(graph);
		validateGraph(newGraph);
		Definitions newTopologyTemplate = MappingUtils.mapGraph(newGraph);

		return newTopologyTemplate;
	}


	public Definitions getTopologyTemplate(long id) throws JsonParseException, JsonMappingException, JAXBException, IOException {
		if (id < 0) {
			throw new ForbiddenException("Illegal topology template id: " + id);
		}
		Graph localGraph = manager.getGraph(id);
		validateGraph(localGraph);
		return MappingUtils.mapGraph(localGraph);
	}

	//    public TopologyTemplate updateTopologyTemplate(TTopologyTemplate graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {
	//        if (graph.getId() < 0) {
	//            throw new ForbiddenException("Illegal graph id: " + graph.getId());
	//        }
	//        validateTopologyTemplate(graph);
	//        TTopologyTemplate localTopologyTemplate=new TTopologyTemplate();
	//        localTopologyTemplate=manager.updateTopologyTemplate(graph);
	//        vlogger.logger.info("TopologyTemplate updated");
	//        validateTopologyTemplate(localTopologyTemplate);
	//        return localTopologyTemplate;
	//    }
	//
	//
	//    public void removeTopologyTemplate(long id) {
	//
	//        if (id < 0) {
	//            throw new ForbiddenException("Illegal graph id: " + id);
	//        }
	//        manager.deleteTopologyTemplate(id);
	//    }
	//
	//    public TTopologyTemplate addTopologyTemplate(TTopologyTemplate graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {
	//        validateTopologyTemplate(graph);
	//        TTopologyTemplate g=manager.addTopologyTemplate(graph);
	//        validateTopologyTemplate(g);
	//        return g;
	//    }

	public static void validateGraph(Graph graph) throws JsonProcessingException {
		for (Node node : graph.getNodes().values()) {
			NodeService.validateNode(graph, node);
		}
	}

		//YAML methods

	public List<ServiceTemplateYaml> getAllTopologyTemplatesYaml() throws JsonProcessingException, MyNotFoundException {
		List<Graph> verigraphResult;
		List<ServiceTemplateYaml> result = new ArrayList<ServiceTemplateYaml>();

		verigraphResult = manager.getGraphs();
		for(Graph g : verigraphResult){
			validateGraph(g);
			result.add(MappingUtils.mapGraphYaml(g));
		}
		return result;
	}

	public ServiceTemplateYaml getTopologyTemplateYaml(long graphId) throws JAXBException, JsonProcessingException, JsonMappingException, IOException {
		if (graphId < 0) {
			throw new ForbiddenException("Illegal topology template id: " + graphId);
		}
		Graph localGraph = manager.getGraph(graphId);
		validateGraph(localGraph);
		return MappingUtils.mapGraphYaml(localGraph);
	}
}