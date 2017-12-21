package it.polito.verigraph.providers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Provider
@Produces("application/x-yaml")
public class YamlWriterProvider implements MessageBodyWriter<List<ServiceTemplateYaml>> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        // TODO: Check that return
        return new MediaType("application", "x-yaml").equals(mediaType)
                && List.class.isAssignableFrom(type)
                && (((ParameterizedType)genericType).getActualTypeArguments()[0]).equals(ServiceTemplateYaml.class);
    }

    @Override
    public long getSize(List<ServiceTemplateYaml> serviceTemplatesYaml , Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<ServiceTemplateYaml> serviceTemplatesYaml, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        for (final ServiceTemplateYaml st: serviceTemplatesYaml) {
            mapper.writeValue(entityStream, st);
        }
    }
}
