package it.polito.verigraph.tosca.yaml.beans;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.polito.verigraph.exception.InvalidServiceTemplateException;
import it.polito.verigraph.exception.BadRequestException;

public class YamlDeserializer {
	
	public static ServiceTemplateYaml deserialize(String yamlFile) throws BadRequestException{
		ServiceTemplateYaml parsedServiceTemplate = null;
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		
		try {
			parsedServiceTemplate = mapper.readValue(new File(yamlFile), ServiceTemplateYaml.class);
			validate(parsedServiceTemplate);
			
        } catch (JsonMappingException e) {
        	System.err.println("[YamlDeserializer] The provided file does not match the expected structure.");
        	e.printStackTrace();
        	throw new BadRequestException("BAD_REQUEST Yaml deserialization failed");
        } catch (InvalidServiceTemplateException e) {
        	System.err.println("[YamlDeserializer] The provided template contains errors or missing informations.");
        	e.printStackTrace();
        	throw new BadRequestException("BAD_REQUEST Yaml deserialization failed");
        } catch (IOException e) {
            System.err.println("[YamlDeserializer] Unexpected I/O error reading the provided file.");
            e.printStackTrace();
            throw new BadRequestException("BAD_REQUEST Yaml deserialization failed");
        }
		
		return parsedServiceTemplate;
	}
	
	public static void validate(ServiceTemplateYaml toBeChecked) throws InvalidServiceTemplateException {
		//to be eventually defined (we could simply use the server side validator)
		return ;
	}

}
