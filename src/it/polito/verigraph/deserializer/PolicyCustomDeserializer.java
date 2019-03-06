/*******************************************************************************
 * Copyright (c) 2019 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.deserializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.InternalServerErrorException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Policy;
import it.polito.verigraph.model.Restrictions;

public class PolicyCustomDeserializer extends JsonDeserializer<Policy> {

    @Override
    public Policy deserialize(JsonParser jp, DeserializationContext context) {

        try {
            JsonNode root = jp.getCodec().readTree(jp);
            JsonNode trafficFlowJson = root.get("trafficFlow");
            JsonNode restrictionJson = root.get("restrictions");
            String policyName = root.get("policyName").asText();
            String source = root.get("source").asText();
            String destination = root.get("target").asText();
            String restrictionsType = restrictionJson.get("type").asText();
            JsonNode restrictionFunctionsJson = restrictionJson.get("functions");
            
            Policy policy = new Policy();

            if(root.get("id") != null){
                long policyId = root.get("id").asLong();
                policy.setId(policyId);
            }
            policy.setName(policyName);
            policy.setSource(source);
            policy.setDestination(destination);
            policy.setTrafficFlow(trafficFlowJson);

            Restrictions restr = policy.getRestrictions();
            restr.setType(restrictionsType);
            restr.setRestrictions(restrictionFunctionsJson);
            
            return policy;
        }
        catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Error parsing a policy: " + e.getMessage());
        }
        catch (IOException e) {
            throw new InternalServerErrorException("I/O error parsing a policy: " + e.getMessage());
        }

    }

}