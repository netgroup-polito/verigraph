/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.exception.InternalServerErrorException;
import it.polito.verigraph.model.Restrictions;

public class RestrictionCustomDeserializer extends JsonDeserializer<Restrictions> {

    @Override
    public Restrictions deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
    JsonProcessingException {
        try {
            JsonNode root = jp.getCodec().readTree(jp);
            return new Restrictions("", root);
        }
        catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Error parsing restrictions: " + e.getMessage());
        }
        catch (IOException e) {
            throw new InternalServerErrorException("I/O error parsing restrictions: " + e.getMessage());
        }
    }
}