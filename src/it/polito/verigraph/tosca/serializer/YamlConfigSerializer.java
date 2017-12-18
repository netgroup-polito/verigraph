package it.polito.verigraph.tosca.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.polito.verigraph.tosca.yaml.beans.AntispamNode;
import it.polito.verigraph.tosca.yaml.beans.CacheNode;
import it.polito.verigraph.tosca.yaml.beans.DpiNode;
import it.polito.verigraph.tosca.yaml.beans.EndhostNode;
import it.polito.verigraph.tosca.yaml.beans.EndpointNode;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierNode;
import it.polito.verigraph.tosca.yaml.beans.FirewallNode;
import it.polito.verigraph.tosca.yaml.beans.MailClientNode;
import it.polito.verigraph.tosca.yaml.beans.MailServerNode;
import it.polito.verigraph.tosca.yaml.beans.NatNode;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessNode;
import it.polito.verigraph.tosca.yaml.beans.VpnExitNode;
import it.polito.verigraph.tosca.yaml.beans.WebClientNode;
import it.polito.verigraph.tosca.yaml.beans.WebServerNode;

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

			EndhostNode.EndhostConfigurationYaml endhost = (EndhostNode.EndhostConfigurationYaml) value;

			if(endhost.getBody() != null) 
				jgen.writeObjectField("body", endhost.getBody());
			if(endhost.getSequence() != 0) 
				jgen.writeObjectField("sequence", endhost.getSequence());
			if(endhost.getProtocol() != null) 
				jgen.writeObjectField("protocol", endhost.getProtocol());
			if(endhost.getEmail_from() != null) 
				jgen.writeObjectField("email_from", endhost.getEmail_from());
			if(endhost.getUrl() != null) 
				jgen.writeObjectField("url", endhost.getUrl());
			if(endhost.getOptions() != null) 
				jgen.writeObjectField("options", endhost.getOptions());
			if(endhost.getDestination() != null) 
				jgen.writeObjectField("destination", endhost.getDestination());

			jgen.writeEndObject();
			jgen.writeEndArray();

		}else if(value instanceof EndpointNode.EndpointConfigurationYaml) {
			jgen.writeStartArray();
			jgen.writeEndArray();

		}else if(value instanceof FieldModifierNode.FieldModifierConfigurationYaml) {
			jgen.writeStartArray();
			jgen.writeEndArray();

		}else if(value instanceof FirewallNode.FirewallConfigurationYaml) {
			jgen.writeStartArray();
			FirewallNode.FirewallConfigurationYaml fw = (FirewallNode.FirewallConfigurationYaml) value;  

			for(Map.Entry<String, String> entry : fw.getElements().entrySet()) {
				jgen.writeStartObject();
				jgen.writeObjectField("source", entry.getKey());
				jgen.writeObjectField("destination", entry.getValue());
				jgen.writeEndObject();
			}  		
			jgen.writeEndArray();

		}else if(value instanceof MailClientNode.MailClientConfigurationYaml) {
			jgen.writeStartArray();

			if(((MailClientNode.MailClientConfigurationYaml) value).getMailserver() != null) {
				jgen.writeStartObject();
				jgen.writeObjectField("mailserver", ((MailClientNode.MailClientConfigurationYaml) value).getMailserver());
				jgen.writeEndObject();
			}
			jgen.writeEndArray();

		}else if(value instanceof MailServerNode.MailServerConfigurationYaml) {
			jgen.writeStartArray();
			jgen.writeEndArray();

		}else if(value instanceof NatNode.NatConfigurationYaml) {
			jgen.writeStartArray();

			for(String source : ((NatNode.NatConfigurationYaml) value).getSources()) {
				jgen.writeString(source);
			}

			jgen.writeEndArray();

		}else if(value instanceof VpnAccessNode.VpnAccessConfigurationYaml) {
			jgen.writeStartArray();

			if(((VpnAccessNode.VpnAccessConfigurationYaml) value).getVpnexit()!= null) {
				jgen.writeStartObject();
				jgen.writeObjectField("vpnexit", ((VpnAccessNode.VpnAccessConfigurationYaml) value).getVpnexit());
				jgen.writeEndObject();
			}

			jgen.writeEndArray();

		}else if(value instanceof VpnExitNode.VpnExitConfigurationYaml) {
			jgen.writeStartArray();

			if(((VpnExitNode.VpnExitConfigurationYaml) value).getVpnaccess()!= null) {
				jgen.writeStartObject();
				jgen.writeObjectField("vpnaccess", ((VpnExitNode.VpnExitConfigurationYaml) value).getVpnaccess());
				jgen.writeEndObject();
			}

			jgen.writeEndArray();

		}else if(value instanceof WebClientNode.WebClientConfigurationYaml) {
			jgen.writeStartArray();

			if(((WebClientNode.WebClientConfigurationYaml) value).getNameWebServer() != null) {
				jgen.writeStartObject();
				jgen.writeObjectField("webserver", ((WebClientNode.WebClientConfigurationYaml) value).getNameWebServer());
				jgen.writeEndObject();
			}

			jgen.writeEndArray();

		}else if(value instanceof WebServerNode.WebServerConfigurationYaml) {
			jgen.writeStartArray();
			jgen.writeEndArray();

		}else  {
			jgen.writeStartArray();
			jgen.writeEndArray();
			System.out.println("[DA RIMUOVERE]: Yaml configuration settata a default."); //TODO
		}

	}
}
