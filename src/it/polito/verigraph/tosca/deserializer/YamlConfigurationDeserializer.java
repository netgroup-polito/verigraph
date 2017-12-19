package it.polito.verigraph.tosca.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.tosca.yaml.beans.AntispamConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.CacheConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.DpiConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndhostConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndpointConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FirewallConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailServerConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.NatConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnExitConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebServerConfigurationYaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class YamlConfigurationDeserializer extends JsonDeserializer<ConfigurationYaml> {
	  
	  @Override
	  public ConfigurationYaml deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    ObjectCodec oc = jp.getCodec();
	    JsonNode node = oc.readTree(jp);
	    ConfigurationYaml deserialized;
	    
		try {
			//Get the content from the array wrapping the JSON configuration
			final Iterator<JsonNode> elements = node.elements();
			
			switch ((String)ctxt.findInjectableValue("type", null, null)) {

			case "antispam":
				AntispamConfigurationYaml antispam = new AntispamConfigurationYaml();
				List<String> sources = new ArrayList<String>();
				while (elements.hasNext()) {
					sources.add(elements.next().asText());
				}
				antispam.setSources(sources);
				deserialized = antispam;
				break;
			
			case "cache":
				CacheConfigurationYaml cache = new CacheConfigurationYaml();
				List<String> resources = new ArrayList<String>();
				while(elements.hasNext()) {
					resources.add(elements.next().asText());
				}
				cache.setResources(resources);
				deserialized = cache;
				break;
				
			case "dpi":
				DpiConfigurationYaml dpi = new DpiConfigurationYaml();
				List<String> notallowed = new ArrayList<String>();
				while(elements.hasNext()) {
					notallowed.add(elements.next().asText());
				}
				dpi.setNotAllowedList(notallowed);
				deserialized = dpi;
				break;
				
			case "endhost":
				EndhostConfigurationYaml endhost = new EndhostConfigurationYaml();
				JsonNode thisnode = elements.next();
				
				endhost.setBody(thisnode.findValue("body").asText());
				endhost.setSequence(Integer.valueOf(thisnode.findValue("sequence").asText()));
				endhost.setProtocol(thisnode.findValue("protocol").asText());
				endhost.setEmail_from(thisnode.findValue("email_from").asText());
				endhost.setOptions(thisnode.findValue("options").asText());
				endhost.setUrl(thisnode.findValue("url").asText());
				endhost.setDestination(thisnode.findValue("destination").asText());
				
				deserialized = endhost;
				break;
			
			case "endpoint":
				EndpointConfigurationYaml endpoint = new EndpointConfigurationYaml();
				deserialized = endpoint;
				break;
			
			case "fieldmodifier":
				FieldModifierConfigurationYaml fieldmodifier = new FieldModifierConfigurationYaml();
				deserialized = fieldmodifier;
				break;
			
			case "firewall":
				FirewallConfigurationYaml firewall = new FirewallConfigurationYaml();
				Map<String, String> fwelements = new HashMap<String, String>();
				JsonNode current = elements.next();
				Iterator<Map.Entry<String, JsonNode>> iter = current.fields();
				while (iter.hasNext()) {
					Map.Entry<String, JsonNode> entry = iter.next();
					fwelements.put(entry.getKey(), entry.getValue().asText());
				}
				deserialized = firewall;
				break;
			
			case "mailclient":
				MailClientConfigurationYaml mailclient = new MailClientConfigurationYaml();
				mailclient.setMailserver(elements.next().findValue("mailserver").asText());
				deserialized = mailclient;
				break;
			
			case "mailserver":
				MailServerConfigurationYaml mailserver = new MailServerConfigurationYaml();
				deserialized = mailserver;
				break;
			
			case "nat":
				NatConfigurationYaml nat = new NatConfigurationYaml();
				List<String> natsource = new ArrayList<String>();
				while(elements.hasNext()) {
					natsource.add(elements.next().asText());
				}
				deserialized = nat;
				break;	
			
			case "vpnaccess":
				VpnAccessConfigurationYaml vpnaccess = new VpnAccessConfigurationYaml();
				vpnaccess.setVpnexit(elements.next().findValue("vpnexit").asText());
				deserialized = vpnaccess;
				break;	
			
			case "vpnexit":
				VpnExitConfigurationYaml vpnexit = new VpnExitConfigurationYaml();
				vpnexit.setVpnaccess(elements.next().findValue("vpnaccess").asText());
				deserialized = vpnexit;
				break;	
			
			case "webclient":
				WebClientConfigurationYaml webclient = new WebClientConfigurationYaml();
				webclient.setNameWebServer(elements.next().findValue("webserver").asText());
				deserialized = webclient;
				break;	
				
			case "webserver":
				WebServerConfigurationYaml webserver = new WebServerConfigurationYaml();
				deserialized = webserver;
				break;	

			default:
				FieldModifierConfigurationYaml defaultForwarder = new FieldModifierConfigurationYaml();
				deserialized = defaultForwarder;
				break;
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Error converting the Json to XmlConfiguration");
			e.printStackTrace();
			return null;
		}
	    
	    return deserialized;
	    
	  }

}