/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.providers;

import com.sun.research.ws.wadl.ObjectFactory;
import it.polito.verigraph.model.Graph;
import it.polito.tosca.jaxb.Configuration;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TDefinitions;
import it.polito.verigraph.tosca.converter.xml.XmlToGraph;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class XmlReaderProvider implements MessageBodyReader<Graph> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Graph.class == type;
    }

    public Graph readFrom(Class<Graph> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Definitions topologyTemplate = (Definitions) jaxbUnmarshaller.unmarshal(entityStream);
            return XmlToGraph.mapTopologyTemplate(topologyTemplate);
        } catch (JAXBException e) {
            throw new WebApplicationException(e.getCause(), Response.status(Response.Status.BAD_REQUEST).build());
        }
    }

}