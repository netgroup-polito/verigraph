/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.serializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.polito.verigraph.exception.InternalServerErrorException;
import it.polito.verigraph.model.Restrictions;

public class CustomRestrictionSerializer extends JsonSerializer<Restrictions> {

    @Override
    public void serialize(Restrictions res, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        try {
        	jgen.writeStartObject();
        	jgen.writeStringField("type", res.getType());
        	jgen.writeObjectField("functions", res.getRestrictions());
            jgen.writeEndObject();
        } catch (IOException e) {
            throw new InternalServerErrorException("I/O error serializing a restrictions object: " + e.getMessage());
        }
    }

}