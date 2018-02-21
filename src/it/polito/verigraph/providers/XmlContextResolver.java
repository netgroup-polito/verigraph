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
        this.ctx = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class); // initialize it the way you want
    }

    public JAXBContext getContext(Class<?> type) {
        if (type.equals(Definitions.class)) {
            return ctx;
        } else {
            return null;
        }
    }
}
