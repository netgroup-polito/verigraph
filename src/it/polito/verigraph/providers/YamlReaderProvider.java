package it.polito.verigraph.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.model.Graph;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.converter.yaml.YamlToGraph;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;

@Provider
@Consumes("application/x-yaml")
public class YamlReaderProvider implements MessageBodyReader<Graph> {


   @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Graph.class == type;
    }


    @Override
    public Graph readFrom(Class<Graph> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {
        try {
            ServiceTemplateYaml yamlServiceTemplate = new ServiceTemplateYaml();
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
            yamlServiceTemplate = mapper.readValue(entityStream, ServiceTemplateYaml.class);
            return YamlToGraph.mapTopologyTemplateYaml(yamlServiceTemplate);
        } catch (IOException e) {
            throw new WebApplicationException(e.getCause(), Response.status(Response.Status.BAD_REQUEST).build());
        }
    }
}