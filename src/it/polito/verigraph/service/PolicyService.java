/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Policy;
import it.polito.verigraph.model.Restrictions;

public class PolicyService {

    private Neo4jDBManager manager=new Neo4jDBManager();

    public PolicyService() {}

    public List<Policy> getAllPolicies(long graphId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }

        // The check for the graph existence is done within getNodes 

        Map<Long, Policy> policies = manager.getPolicies(graphId);
        return new ArrayList<Policy>(policies.values());
    }

    public Policy getPolicy(long graphId, long policyId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (policyId < 0) {
            throw new ForbiddenException("Illegal policy id: " + policyId);
        }

        Policy policy = manager.getPolicyById(policyId, graphId);
        if (policy == null) {
            throw new DataNotFoundException("Policy with id " + policyId + " not found in graph with id " + graphId);
        }
        return policy;
    }

    public Policy updatePolicy(long graphId, Policy policy) throws JAXBException, IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (policy.getId() < 0) {
            throw new ForbiddenException("Illegal policy id: " + policy.getId());
        }

        Graph graph=manager.getGraph(graphId);
        validatePolicy(graph, policy);

        Policy p = manager.updatePolicy(graphId, policy, policy.getId());
        validatePolicy(graph, p);

        return p;
    }

    public Policy removePolicy(long graphId, long policyId) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (policyId < 0) {
            throw new ForbiddenException("Illegal policy id: " + policyId);
        }

        Graph graph=manager.getGraph(graphId);
        if (graph == null)
            throw new DataNotFoundException("Graph with id " + graphId + " not found");
        Policy p = manager.deletePolicy(graphId, policyId);
        return p;
    }

    public Policy addPolicy(long graphId, Policy policy) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }

        Graph graph=manager.getGraph(graphId);
        validatePolicy(graph, policy);
        Policy p = manager.addPolicy(graphId, policy);
        validatePolicy(graph, p);
        return p;
    }

    public Policy searchByName(long graphId, String policyName) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        Graph graph = manager.getGraph(graphId);
        if (graph == null)
            throw new DataNotFoundException("Graph with id " + graphId + " not found");
        Map<Long, Policy> policies = graph.getPolicies();
        for (Policy policy : policies.values()) {
            if (policy.getName().equals(policyName))
                return policy;
        }
        return null;
    }

    public static void validatePolicy(Graph graph, Policy policy) throws JsonProcessingException {
    	// Validate graph and policy objects
        if (graph == null)
            throw new BadRequestException("Policy validation failed: cannot validate null graph");
        if (policy == null)
            throw new BadRequestException("Policy validation failed: cannot validate null policy");

        // Validate policy's name, source and destination
        if (policy.getName() == null)
            throw new BadRequestException("Policy validation failed: policy's 'name' field cannot be null");
        if (policy.getSource() == null)
            throw new BadRequestException("Policy validation failed: policy's 'source' field cannot be null");
        if (policy.getDestination() == null)
            throw new BadRequestException("Policy validation failed: policy's 'destination' field cannot be null");

        if (policy.getName().equals(""))
            throw new BadRequestException("Policy validation failed: policy's 'name' field cannot be an empty string");
        if (policy.getSource().equals(""))
            throw new BadRequestException("Policy validation failed: policy's 'source' field cannot be an empty string");
        if (policy.getDestination().equals(""))
            throw new BadRequestException("Policy validation failed: policy's 'destination' field cannot be an empty string");

        Policy policyFound =graph.searchPolicyByName(policy.getName());
        if ((policyFound != null) && (policyFound.equals(policy) == false))
            throw new BadRequestException("Policy validation failed: graph already has a policy named "+ policy.getName());
        
        Node sourceFound = graph.searchNodeByName(policy.getSource());
        if (sourceFound == null)
            throw new BadRequestException("Source node validation failed: graph has no node named '"+ policy.getSource()
            + "'");
        
        Node targetFound = graph.searchNodeByName(policy.getDestination());
        if (targetFound == null)
            throw new BadRequestException("Target node validation failed: graph has no node named '"+ policy.getDestination()
            + "'");
        
        // Validate traffic flow
        validateTrafficFlowAgainstSchemaFile(policy);
        
        // Validate policy's resctrictions
        Restrictions restrictions = policy.getRestrictions();
        if (restrictions != null) {
        	// Validate type
        	String restrictionsType = restrictions.getType();
        	try {
                validatePolicyRestrictionsType(restrictionsType);
            }
            catch(ForbiddenException e) {
            	throw new BadRequestException("Something went wrong trying to validate policy '"
                + e.getMessage());
            }
        	
            JsonNode restrictionsJsonNode = restrictions.getRestrictions();
            
            // validate function list against schema file
            try {
                validatePolicyRestrictionsAgainstSchemaFile(policy, restrictionsJsonNode);
            }
            catch(ForbiddenException e) {
            	throw new BadRequestException("Something went wrong trying to validate policy '"
                + e.getMessage() + "'");
            }
            
            JsonValidationService jsonValidator = new JsonValidationService(graph, policy);
            boolean hasCustomValidator = jsonValidator.validatePolicyRestrictions();
            if (!hasCustomValidator) {
                jsonValidator.validateFieldsAgainstPolicyNames(restrictionsJsonNode);
            }
        }
    }
    
    private static void validatePolicyRestrictionsType(String restrictionsType) {
    	for (it.polito.neo4j.jaxb.RestrictionTypes rt : it.polito.neo4j.jaxb.RestrictionTypes.values()) {
            if (rt.name().toLowerCase().equals(restrictionsType)) {
                return;
            }
        }
    	throw new ForbiddenException("Forbidden restrictions type: " + restrictionsType);
	}

	public static void validateTrafficFlowAgainstSchemaFile(Policy policy) {
        String schemaFileName = "packettype.json";

        File schemaFile = new File(System.getProperty("catalina.base") + "/webapps/verigraph/jsonschema/" + schemaFileName);

        if (!schemaFile.exists()) {
            //if no REST client, try gRPC application
            schemaFile = new File(System.getProperty("user.dir") + "/jsonschema/" + schemaFileName);

            if (!schemaFile.exists()) {
                throw new ForbiddenException("Packet type is not supported! Please edit 'trafficFlow' field of policy '"
                + policy.getName() + "'");
            }
        }

        JsonSchema schemaNode = null;
        try {
            schemaNode = ValidationUtils.getSchemaNode(schemaFile);
        }
        catch (IOException e) {
            throw new InternalServerErrorException("Unable to load '" + schemaFileName + "' schema file");
        }
        catch (ProcessingException e) {
            throw new InternalServerErrorException("Unable to resolve '"+ schemaFileName
                    + "' schema file as a schema node");
        }

        //VerigraphLogger.getVerigraphlogger().logger.info("Trying to validate policy "+ policy.getName() + " with the following traffic flow: " + policy.getTrafficFlow().toString());
        
        try {
            ValidationUtils.validateJson(schemaNode, policy.getTrafficFlow());
        }
        catch (ProcessingException e) {
            throw new BadRequestException("Something went wrong trying to validate policy '"+ policy.getName()
            + "' with the following traffic flow: '" + policy.getTrafficFlow().toString()
            + "' against the json schema '" + schemaFile.getName() + "': "
            + e.getMessage());
        }

    }

    public static void validatePolicyRestrictionsAgainstSchemaFile(Policy policy, JsonNode restrictionsJson) {
        String schemaFileName = "restrictions.json";

        File schemaFile = new File(System.getProperty("catalina.base") + "/webapps/verigraph/jsonschema/" + schemaFileName);

        if (!schemaFile.exists()) {
            //if no REST client, try gRPC application
            schemaFile = new File(System.getProperty("user.dir") + "/jsonschema/" + schemaFileName);

            if (!schemaFile.exists()) {
                throw new ForbiddenException("Wrong schema file provided! Please check that " + schemaFileName +
                		" exists and is a valid json file.");
            }
        }

        JsonSchema schemaNode = null;
        try {
            schemaNode = ValidationUtils.getSchemaNode(schemaFile);
        }
        catch (IOException e) {
            throw new InternalServerErrorException("Unable to load '" + schemaFileName + "' schema file");
        }
        catch (ProcessingException e) {
            throw new InternalServerErrorException("Unable to resolve '"+ schemaFileName
                    + "' schema file as a schema node");
        }

        try {
            ValidationUtils.validateJson(schemaNode, restrictionsJson);
        }
        catch (ProcessingException e) {
            throw new BadRequestException("Something went wrong trying to validate policy '"+ policy.getName()
            + "' with the following restrictions: '" + restrictionsJson.toString()
            + "' against the json schema '" + schemaFile.getName() + "': "
            + e.getMessage());
        }

    }

    public Restrictions addPolicyRestrictions(long graphId, long policyId, Restrictions policyRestrictions) throws IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (policyId < 0) {
            throw new ForbiddenException("Illegal node id: " + policyId);
        }

        Policy policy = manager.getPolicyById(policyId, graphId);
        validatePolicyRestrictionsAgainstSchemaFile(policy, policyRestrictions.getRestrictions());
        Restrictions newRestr = manager.updateRestrictions(policyId, graphId, policyRestrictions, policy);
        return newRestr;
    }
}