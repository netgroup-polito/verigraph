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
import it.polito.verigraph.validation.exception.ValidationException;


public class FirewallValidator implements ValidationInterface {
    
    @Override
    public void validate(Graph graph, Node node, Configuration configuration) throws ValidationException {
    	JsonNode configurationNode = configuration.getConfiguration();

        if (!configurationNode.isArray())
            throw new ValidationException("configuration must be an array");

        for (JsonNode object : configurationNode) {
            JsonNode source = object.get("source_id");
            if (!source.isTextual())
                throw new ValidationException("value corresponding to the key 'source_id' must be a string");
            validateSourceNode(graph, node, source.asText());
            
            JsonNode dest = object.get("destination_id");
            if (!dest.isTextual())
                throw new ValidationException("value corresponding to the key 'destination_id' must be a string");
            validateDestinationNode(graph, node, dest.asText());
            
            JsonNode srcPort = object.get("source_port");
            if (!srcPort.isInt())
                throw new ValidationException("value corresponding to the key 'source_port' must be an integer");
           
            JsonNode destPort = object.get("destination_port");
            if (!destPort.isInt())
                throw new ValidationException("value corresponding to the key 'destination_port' must be an integer");
        
            JsonNode proto = object.get("protocol");
            if (!proto.isTextual())
                throw new ValidationException("value corresponding to the key 'protocol' must be a string");
            
            if (!((proto.asText().equals("TCP")) || (proto.asText().equals("UDP"))))
                throw new ValidationException("value corresponding to the key 'protocol' must be either 'TCP' or 'UDP'");
        
        }
    }

    private void validateSourceNode(Graph g, Node node, String field) throws ValidationException {
        Node n = g.searchNodeByName(field);

        if (n == null)
            throw new ValidationException("'"+ field + "' is not a valid value for the configuration of a type '"
                    + node.getFunctional_type()
                    + "'. Please use the name of an existing node of type 'source_id'.");
    }
    
    private void validateDestinationNode(Graph g, Node node, String field) throws ValidationException {
        Node n = g.searchNodeByName(field);

        if (n == null)
            throw new ValidationException("'"+ field + "' is not a valid value for the configuration of a type '"
                    + node.getFunctional_type()
                    + "'. Please use the name of an existing node of type 'destination_id'.");
    }
}