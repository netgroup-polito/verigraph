package it.polito.verigraph.tosca;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.*;

//Custom serializer for XmlToscaConfigurationObject conversion to JSON
public class YamlConfigSerializer extends StdSerializer<ConfigurationYaml> {
	
	//Automatically generated VersionUID
	private static final long serialVersionUID = 9102508941195129607L;
	
	public YamlConfigSerializer() {
		this(null);
	}

	public YamlConfigSerializer(Class<ConfigurationYaml> t) {
        super(t);
    }
 
    @Override
    public void serialize(
      ConfigurationYaml value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
    	
    	if(value instanceof AntispamNode.AntispamConfigurationYaml) {
    		jgen.writeStartArray();
    		for(String source : ((AntispamNode.AntispamConfigurationYaml) value).getSources()) {
    			jgen.writeString(source);
    		}
    		jgen.writeEndArray();
    		
    	}else if(value instanceof CacheNode.CacheConfigurationYaml) {
    		jgen.writeStartArray();
    		for(String resource : ((CacheNode.CacheConfigurationYaml) value).getResources()) {
    			jgen.writeString(resource);
    		}
    		jgen.writeEndArray();
    		
    	}else if(value instanceof DpiNode.DpiConfigurationYaml) {
    		jgen.writeStartArray();
    		for(String notAllowed : ((DpiNode.DpiConfigurationYaml) value).getNotAllowedList()) {
    			jgen.writeString(notAllowed);
    		}
    		jgen.writeEndArray();
    		
    	}else if(value instanceof EndhostNode.EndhostConfigurationYaml) {
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
    		jgen.writeStartArray();
    		jgen.writeEndArray();
    		
    	}else if(value.getFirewallConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		for(Configuration.FirewallConfiguration.Elements elem : value.getFirewallConfiguration().getElements()) {
    			jgen.writeStartObject();
    			jgen.writeObjectField("source", elem.getSource());
    			jgen.writeObjectField("destination", elem.getDestination());
    			jgen.writeEndObject();
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getMailclientConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		if(value.getMailclientConfiguration().getMailserver() != null) {
    			jgen.writeStartObject();
    			jgen.writeObjectField("mailserver", value.getMailclientConfiguration().getMailserver());
    			jgen.writeEndObject();
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getMailserverConfiguration() != null) {
    		jgen.writeStartArray();
    		jgen.writeEndArray();
    		
    	}else if(value.getNatConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		for(String source : value.getNatConfiguration().getSource()) {
    			jgen.writeString(source);
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getVpnaccessConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		if(value.getVpnaccessConfiguration().getVpnexit()!= null) {
    			jgen.writeStartObject();
    			jgen.writeObjectField("vpnexit", value.getVpnaccessConfiguration().getVpnexit());
    			jgen.writeEndObject();
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getVpnexitConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		if(value.getVpnexitConfiguration().getVpnaccess()!= null) {
    			jgen.writeStartObject();
    			jgen.writeObjectField("vpnaccess", value.getVpnexitConfiguration().getVpnaccess());
    			jgen.writeEndObject();
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getWebclientConfiguration() != null) {
    		jgen.writeStartArray();
    		
    		if(value.getWebclientConfiguration().getNameWebServer() != null) {
    			jgen.writeStartObject();
    			jgen.writeObjectField("webserver", value.getWebclientConfiguration().getNameWebServer());
    			jgen.writeEndObject();
    		}
    		
    		jgen.writeEndArray();
    		
    	}else if(value.getWebserverConfiguration() != null) {
    		jgen.writeStartArray();
    		jgen.writeEndArray();
    		
    	}else  {
    		//Behaviour to be defined
    		System.err.println("Error converting configuration to json, unrecognized configuration");
    	}
    	
    }
}
