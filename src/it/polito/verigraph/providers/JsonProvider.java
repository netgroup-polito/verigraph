package it.polito.verigraph.providers;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.polito.verigraph.model.Graph;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class JsonProvider implements MessageBodyReader<Graph> {


    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Graph.class == type;
    }


    public Graph readFrom(Class<Graph> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(entityStream, Graph.class);
        } catch (IOException e) {
            throw new WebApplicationException(e.getCause(), Response.status(Response.Status.BAD_REQUEST).build());
        }
    }
}