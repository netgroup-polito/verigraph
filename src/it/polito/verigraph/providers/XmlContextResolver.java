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
import it.polito.tosca.jaxb.Configuration;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TDefinitions;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class XmlContextResolver implements ContextResolver<JAXBContext> {
    private JAXBContext ctx;

    public XmlContextResolver() throws JAXBException {
        this.ctx = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
        // initialize it the way you want
    }

    public JAXBContext getContext(Class<?> type) {
        if (type.equals(Definitions.class)) {
            return ctx;
        } else {
            return null;
        }
    }
}
