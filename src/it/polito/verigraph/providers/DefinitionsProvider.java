package it.polito.verigraph.providers;

import it.polito.verigraph.model.Graph;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.classes.Definitions;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
@Produces(MediaType.APPLICATION_XML)
public class DefinitionsProvider implements MessageBodyReader<Object> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Object.class == type;
    }

    public Graph readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Definitions topologyTemplate = (Definitions) jaxbUnmarshaller.unmarshal(entityStream);
            return MappingUtils.mapTopologyTemplate(topologyTemplate);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null; // Check that
    }

}