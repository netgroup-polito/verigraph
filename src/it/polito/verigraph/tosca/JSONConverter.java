package it.polito.verigraph.tosca;

import java.util.List;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.polito.verigraph.tosca.classes.Configuration;

public class JSONConverter {

	public static String ConfigurationToJSON(Configuration toConvert) {
		String jsonConfig = null;
		
		
		return jsonConfig;
	}
	
	
	public class configSerializer extends StdSerializer<Configuration> {
		
	    public configSerializer(Class<Configuration> t) {
	        super(t);
	    }
	 
	    @Override
	    public void serialize(
	      Configuration value, JsonGenerator jgen, SerializerProvider provider) 
	      throws IOException, JsonProcessingException {
	    	
	    	if(value.getAntispamConfiguration() != null) {
	    		jgen.writeStartArray();
	    		for(String source : value.getAntispamConfiguration().getSource()) {
	    			jgen.writeString(source);
	    		}
	    		jgen.writeEndArray();
	    		
	    	}else if(value.getCacheConfiguration() != null) {
	    		jgen.writeStartArray();
	    		for(String resource : value.getCacheConfiguration().getResource()) {
	    			jgen.writeString(resource);
	    		}
	    		jgen.writeEndArray();
	    		
	    	}else if(value.getDpiConfiguration() != null) {
	    		jgen.writeStartArray();
	    		for(String notAllowed : value.getDpiConfiguration().getNotAllowed()) {
	    			jgen.writeString(notAllowed);
	    		}
	    		jgen.writeEndArray();
	    		
	    	}else if(value.getEndhostConfiguration() != null) {
	    		jgen.writeStartArray();
	    		jgen.writeStartObject();
	    		
	    		Configuration.EndhostConfiguration endhost = value.getEndhostConfiguration();
	    		if(endhost.getBody() != null) jgen.writeObjectField("body", endhost.getBody());
	    		if(endhost.getSequence() != null) jgen.writeObjectField("sequence", endhost.getSequence());
	    		if(endhost.getProtocol() != null) jgen.writeObjectField("protocol", endhost.getProtocol());
	    		if(endhost.getEmailFrom() != null) jgen.writeObjectField("email_from", endhost.getEmailFrom());
	    		if(endhost.getUrl() != null) jgen.writeObjectField("url", endhost.getUrl());
	    		if(endhost.getOptions() != null) jgen.writeObjectField("options", endhost.getOptions());
	    		if(endhost.getDestination() != null) jgen.writeObjectField("destination", endhost.getDestination());
	    		
	    		jgen.writeEndObject();
	    		jgen.writeEndArray();
	    		
	    	}else if(value.getEndpointConfiguration() != null) {
	    		jgen.writeStartArray();
	    		jgen.writeEndArray();
	    		
	    	}else if(value.getFieldmodifierConfiguration() != null) {
	    		
	    	}else if(value.getFirewallConfiguration() != null) {
	    		
	    	}else if(value.getMailclientConfiguration() != null) {
	    		
	    	}else if(value.getMailserverConfiguration() != null) {
	    		
	    	}else if(value.getNatConfiguration() != null) {
	    		
	    	}else if(value.getVpnaccessConfiguration() != null) {
	    		
	    	}else if(value.getVpnexitConfiguration() != null) {
	    		
	    	}else if(value.getWebclientConfiguration() != null) {
	    		
	    	}else if(value.getWebserverConfiguration() != null) {
	    		
	    	}else  {
	    		//Behaviour to be defined
	    	}
	    	
//	        jgen.writeStartObject();
//	        
//	        jgen.writeNumberField("id", value.id);
//	        jgen.writeStringField("itemName", value.itemName);
//	        jgen.writeNumberField("owner", value.owner.id);
//	        jgen.writeEndObject();
	    }
	}
	
}
