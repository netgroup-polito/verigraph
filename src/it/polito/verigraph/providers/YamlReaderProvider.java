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
public class YamlReaderProvider implements MessageBodyReader<Object> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Object.class == type;
    }

    public Graph readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws WebApplicationException {

        ServiceTemplateYaml yamlServiceTemplate = new ServiceTemplateYaml();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);

        try {
            yamlServiceTemplate = mapper.readValue(entityStream, ServiceTemplateYaml.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return YamlToGraph.mapTopologyTemplateYaml(yamlServiceTemplate);
    }

}