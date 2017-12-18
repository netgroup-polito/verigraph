package it.polito.verigraph.tosca;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import it.polito.verigraph.tosca.classes.Configuration;

public class JSONConverter {

	public static String ConfigurationToJSON(Configuration toConvert) {
		String jsonConfig = null;
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			
			SimpleModule module = new SimpleModule();
			module.addSerializer(Configuration.class, new ConfigSerializer());
			mapper.registerModule(module);
			
			//Convert object to JSON string and pretty print
			jsonConfig = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toConvert);
			System.out.println(jsonConfig);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonConfig;
	}
	
	

	
}
