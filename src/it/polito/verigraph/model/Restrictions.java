/*******************************************************************************
 * Copyright (c) 2019 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.verigraph.deserializer.RestrictionCustomDeserializer;
import it.polito.verigraph.serializer.CustomRestrictionSerializer;
import it.polito.verigraph.validation.exception.ValidationException;

@ApiModel(value = "Restrictions")
@JsonSerialize(using = CustomRestrictionSerializer.class)
@JsonDeserialize(using = RestrictionCustomDeserializer.class)
public class Restrictions {
	@ApiModelProperty(required = true,
            example = "sequence",
            value = "The restriction types that are currently supported are: selection, set, sequence, list")
    private String type;

    @ApiModelProperty(required = true, name = "functions")
    	
    private JsonNode restrictions;

    public Restrictions() {

    }

    public Restrictions(String type, JsonNode restrictions) {
        this.type = type;
        this.restrictions = restrictions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(JsonNode restrictions) {
        this.restrictions = restrictions;
    }

    /*
     * This functions return a list of pair of strings. Each pair describes a specific function contained in the description. 
     * Each pair contains the following elements: {funcType, funcName}
     */
	public List<List<String>> getFunctions() {
		List<List<String>> listOfFunctions = new ArrayList<List<String>>();;
		
		for (JsonNode func : restrictions) {
			List<String> funcToAdd = new ArrayList<String>();;
			
	        funcToAdd.add(func.get("funcType").asText());
	        funcToAdd.add(func.get("funcName").asText());
	        
	        listOfFunctions.add(funcToAdd);
        }
		
		return listOfFunctions;
	}
}
