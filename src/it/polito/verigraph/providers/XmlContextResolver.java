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

/*
@Provider
@Produces(MediaType.APPLICATION_XML)
public class XmlWriterProvider implements MessageBodyWriter<List<Definitions>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // TODO: Check that return
        return MediaType.APPLICATION_XML.equals(mediaType)
                && List.class.isAssignableFrom(type)
                && (((ParameterizedType)genericType).getActualTypeArguments()[0]).equals(Definitions.class);
    }

    @Override
    public long getSize(List<Definitions> definitionsList , Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<Definitions> definitionsList, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            for (final Definitions definitions: definitionsList) {
                marshaller.marshal(definitions, entityStream);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
*/

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
