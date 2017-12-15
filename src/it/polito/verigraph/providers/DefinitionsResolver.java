package it.polito.verigraph.providers;

import it.polito.verigraph.tosca.classes.Definitions;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class DefinitionsResolver
        implements ContextResolver<JAXBContext> {
    private JAXBContext ctx;

    public DefinitionsResolver() throws JAXBException {
        this.ctx = JAXBContext.newInstance(Definitions.class);
    }

    public JAXBContext getContext(Class<?> type) {
        if (type.equals(Definitions.class)) {
            return ctx;
        } else {
            return null;
        }
    }
}