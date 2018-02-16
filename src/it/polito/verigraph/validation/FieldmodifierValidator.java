/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.validation;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.service.JsonValidationService;
import it.polito.verigraph.validation.exception.ValidationException;


public class FieldmodifierValidator implements ValidationInterface {

    public FieldmodifierValidator(){}

    @Override
    public void validate(Graph graph, Node node, Configuration configuration) throws ValidationException {
        JsonNode conf = configuration.getConfiguration();
        Iterator<String> fields =  conf.fieldNames();
        while(fields.hasNext()){
            String field =  fields.next();
            Object value = conf.get(field);
            JsonValidationService validator = new JsonValidationService(graph,  node);
            switch (field){
                case "dest" :
                    if(!validator.validateFieldAgainstNodeNames((String) value))
                        throw new ValidationException("Packet field 'dest' is not a valid graph node");
                    break;
                case "body" :
                    if(!(value instanceof String))
                        throw new ValidationException("Packet field 'body' has not a valid value. please use integer");
                    break;
                case "emailFrom" :
                    if (!((String)value).matches("\\w+[@]\\w+[.]\\w+"))
                        throw new ValidationException("'" + (String) value +
                                "' is not a valid configuration string for an 'antispam'."+
                                "Please use a string that matches this regular expression : \\w+[@]\\w+[.]\\w+");
                    break;
                case "options" :
                    if(!(value instanceof Integer))
                        throw new ValidationException("Packet field 'options' has not a valid value. please use integer");
                    break;
                case "proto" :
                    if(!(value instanceof Integer))
                        throw new ValidationException("Packet field 'proto' has not a valid value. please use integer");
                    break;
                case "seq" :
                    if(!(value instanceof Integer))
                        throw new ValidationException("Packet field 'seq' has not a valid value. please use integer");
                    break;
                case "url" :
                    if (!((String)value).matches("www[.]\\w+[.]\\w+"))
                        throw new ValidationException("'" + (String) value +
                                "' is not a valid configuration string for an 'antispam'."+
                                "Please use a string that matches this regular expression : www[.]\\w+[.]\\w+");
                    break;
                default:
                    throw new ValidationException("'" + field + "' is not a valid configuration field for a 'fieldmodifier'");
            }
        }

    }
}