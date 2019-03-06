/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.validation;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Policy;
import it.polito.verigraph.model.Restrictions;
import it.polito.verigraph.validation.exception.ValidationException;

public class RestrictionsValidator {
	private enum ValidTypes { sequence, set, selection, list };
	
    private boolean validateRestrictionsType(String type) {
    	for (ValidTypes vt : ValidTypes.values()) {
            if (vt.name().equals(type)) {
                return true;
            }
        }
    	return false;
    }
    
    private boolean validateFunctionsType(String type) {
    	for (it.polito.neo4j.jaxb.FunctionalTypes ft : it.polito.neo4j.jaxb.FunctionalTypes.values()) {
            if (ft.name().equals(type)) {
                return true;
            }
        }
    	return false;
    }
    
	private void validateFunc(Graph graph, JsonNode func) throws ValidationException {
		// Check that the function's type is valid
        JsonNode type = func.get("funcType");

        if (!type.isTextual())
            throw new ValidationException("FuncType must be a string");
        
        if(!(type.asText().equals("exact") || type.asText().equals("generic"))) 
            throw new ValidationException("FuncType must be a string between: exact, generic");
        
        // Check that the function's name is valid
        JsonNode name = func.get("funcName");

        if (!name.isTextual())
            throw new ValidationException("FuncName must be a string");
        
        // If the function is exact, check if it exists a node with that name
        if(type.asText().equals("exact")) {
        	Node funcTarget = graph.searchNodeByName(name.asText());
        	
        	if(funcTarget == null) {
        		throw new ValidationException("FuncName must be a string describing an existing node");
        	}
        } else { // Otherwise, check if the specified function is a valid functional type
        	if (!validateFunctionsType(name.asText().toUpperCase()))
                throw new ValidationException("FuncName must be a string describing a valid functional type. The input was " + name.asText());
        }
	}

    public void validate(Graph graph, Policy policy, Restrictions restrictions) throws ValidationException {

    	String type = restrictions.getType();
        JsonNode funcs = restrictions.getRestrictions();
        
        // Validate the name
        if(!validateRestrictionsType(type)) 
            throw new ValidationException("Restrictions element must be a string between: sequence, set, selection, list");
        
        // Validate the functions
        if (!funcs.isArray()) {
            throw new ValidationException("Restrictions' functions must be an array");
        }
        
        for (JsonNode func : funcs) {
            if (!func.isObject())
                throw new ValidationException("Restrictions' functions must be an array of objects");
            validateFunc(graph, func);
        }
    }

}