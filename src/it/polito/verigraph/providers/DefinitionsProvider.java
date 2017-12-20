package it.polito.verigraph.providers;

import it.polito.verigraph.model.Graph;
import it.polito.verigraph.tosca.classes.Definitions;
import it.polito.verigraph.tosca.converter.xml.XmlToGraph;

import javax.ws.rs.Consumes;
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
public class DefinitionsProvider implements MessageBodyReader<Graph> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Graph.class == type;
    }

    public Graph readFrom(Class<Graph> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Definitions topologyTemplate = (Definitions) jaxbUnmarshaller.unmarshal(entityStream);
            return XmlToGraph.mapTopologyTemplate(topologyTemplate);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return null; // Check that
    }

}