/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Policy;

public class JsonValidationService {

    private Graph graph= new Graph();
    private Node node= new Node();
    private Policy policy= new Policy();
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

    public JsonValidationService() {}

    public JsonValidationService(Graph graph, Node node) {
        this.graph = graph;
        this.node = node;
    }
    
    public JsonValidationService(Graph graph, Policy policy) {
        this.graph = graph;
        this.policy = policy;
    }

    public boolean validateFieldAgainstNodeNames(String value) {
        for (Node node : this.graph.getNodes().values()) {
            if (node.getName().equals(value))
                return true;
        }
        return false;
    }

    public void validateFieldsAgainstNodeNames(JsonNode node) {
        if (node.isTextual()) {
            boolean isValid = validateFieldAgainstNodeNames(node.asText());
            if (!isValid) {
                vlogger.logger.info(node.asText() + " is not a valid string!");
                //System.out.println(node.asText() + " is not a valid string!");
                throw new BadRequestException("String '"+ node.asText()
                + "' is not valid for the configuration of node '" + this.node.getName()
                + "'");
            }
        }
        if (node.isArray()) {
            for (JsonNode object : node) {
                validateFieldsAgainstNodeNames(object);
            }
        }
        if (node.isObject()) {
            Iterator<Entry<String, JsonNode>> iter = node.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> item = iter.next();
                validateFieldsAgainstNodeNames(item.getValue());
            }
        }

    }

	public boolean validateNodeConfiguration() {
		String className;
		
		if( node.getFunctional_type().equals("endhost") || node.getFunctional_type().equals("fieldmodifier"))
    		className = "PacketTypeValidator";
    	else	
    		className = WordUtils.capitalize(node.getFunctional_type()) + "Validator";

        Class<?> validator;
        try {
            validator = Class.forName("it.polito.verigraph.validation." + className);
        }
        catch (ClassNotFoundException e) {
            vlogger.logger.info(className+ " not found, configuration properties of node '" + node.getName()
            + "' will be validated against node names");
            //System.out.println(className+ " not found, configuration properties of node '" + node.getName()
            //+ "' will be validated against node names");
            return false;
        }

        Class<?> graphClass;
        Class<?> nodeClass;
        Class<?> configurationClass;
        try {
            graphClass = Class.forName("it.polito.verigraph.model.Graph");
            nodeClass = Class.forName("it.polito.verigraph.model.Node");
            configurationClass = Class.forName("it.polito.verigraph.model.Configuration");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Model classes not found");
        }

        Class<?>[] paramTypes = new Class[3];
        paramTypes[0] = graphClass;
        paramTypes[1] = nodeClass;
        paramTypes[2] = configurationClass;
        String methodName = "validate";
        Object instance;
        try {
            instance = validator.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("'" + className + "' cannot be instantiated");
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access to '" + className + "' instantiation");
        }
        Method myMethod;
        try {
            myMethod = validator.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("'" + methodName + "' method has to be implemented in " + className + " class");
        }
        try {
            myMethod.invoke(instance, graph, node, node.getConfiguration());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access to '" + methodName + "' method in " + className + " instance");
        }
        catch (InvocationTargetException e) {
            throw new BadRequestException("Validation failed for node '"+ node.getName() + "': "
                    + e.getTargetException().getMessage());
        }
        return true;
    }
    
    public boolean validateFieldAgainstPolicyNames(String value) {
        for (Policy policy : this.graph.getPolicies().values()) {
            if (policy.getName().equals(value))
                return true;
        }
        return false;
    }

    public void validateFieldsAgainstPolicyNames(JsonNode policy) {
        if (policy.isTextual()) {
            boolean isValid = validateFieldAgainstPolicyNames(policy.asText());
            if (!isValid) {
                vlogger.logger.info(policy.asText() + " is not a valid string!");

                throw new BadRequestException("String '"+ policy.asText()
                + "' is not valid for the restrictions of policy '" + this.policy.getName()
                + "'");
            }
        }
        if (policy.isArray()) {
            for (JsonNode object : policy) {
                validateFieldsAgainstPolicyNames(object);
            }
        }
        if (policy.isObject()) {
            Iterator<Entry<String, JsonNode>> iter = policy.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> item = iter.next();
                validateFieldsAgainstPolicyNames(item.getValue());
            }
        }

    }

    public boolean validatePolicyRestrictions() {
        String className = "RestrictionsValidator";

        Class<?> validator;
        try {
            validator = Class.forName("it.polito.verigraph.validation." + className);
        }
        catch (ClassNotFoundException e) {
            vlogger.logger.info(className + " not found, restrictions properties of policy '" + policy.getName()
            + "' will be validated against policy names");
            return false;
        }

        Class<?> graphClass;
        Class<?> policyClass;
        Class<?> restrictionsClass;
        try {
            graphClass = Class.forName("it.polito.verigraph.model.Graph");
            policyClass = Class.forName("it.polito.verigraph.model.Policy");
            restrictionsClass = Class.forName("it.polito.verigraph.model.Restrictions");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Model classes not found");
        }

        Class<?>[] paramTypes = new Class[3];
        paramTypes[0] = graphClass;
        paramTypes[1] = policyClass;
        paramTypes[2] = restrictionsClass;
        String methodName = "validate";
        Object instance;
        try {
            instance = validator.newInstance();
        }
        catch (InstantiationException e) {
            throw new RuntimeException("'" + className + "' cannot be instantiated");
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access to '" + className + "' instantiation");
        }
        Method myMethod;
        try {
            myMethod = validator.getDeclaredMethod(methodName, paramTypes);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("'" + methodName + "' method has to be implemented in " + className + " class");
        }
        try {
            myMethod.invoke(instance, graph, policy, policy.getRestrictions());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal access to '" + methodName + "' method in " + className + " instance");
        }
        catch (InvocationTargetException e) {
            throw new BadRequestException("Validation failed for policy '"+ policy.getName() + "': "
                    + e.getTargetException().getMessage());
        }
        return true;
    }
}