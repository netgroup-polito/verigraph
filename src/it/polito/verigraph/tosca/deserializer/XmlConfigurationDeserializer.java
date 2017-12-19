package it.polito.verigraph.tosca.deserializer;

import it.polito.verigraph.tosca.classes.Configuration;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XmlConfigurationDeserializer extends JsonDeserializer<Configuration> {
	  
	  @Override
	  public Configuration deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    ObjectCodec oc = jp.getCodec();
	    JsonNode node = oc.readTree(jp);
	    Configuration deserialized;
	    
		try {
			//Get the content from the array wrapping the JSON configuration
			final Iterator<JsonNode> elements = node.elements();
			deserialized = new Configuration();
			
			switch ((String) ctxt.findInjectableValue("type", null, null)) {

			case "antispam":
				Configuration.AntispamConfiguration antispam = new Configuration.AntispamConfiguration();
				List<String> sources = antispam.getSource();
				while (elements.hasNext()) {
					sources.add(elements.next().asText());
				}
				deserialized.setAntispamConfiguration(antispam);
				break;
			
			case "cache":
				Configuration.CacheConfiguration cache = new Configuration.CacheConfiguration();
				List<String> resources = cache.getResource();
				while(elements.hasNext()) {
					resources.add(elements.next().asText());
				}
				deserialized.setCacheConfiguration(cache);
				break;
				
			case "endhost":
				Configuration.EndhostConfiguration endhost = new Configuration.EndhostConfiguration();
				JsonNode thisnode = elements.next();
				
				String body = endhost.getBody();
				body = thisnode.findValue("body").asText();
				BigInteger sequence = endhost.getSequence();
				sequence = new BigInteger(thisnode.findValue("sequence").asText());
				String protocol = endhost.getProtocol();
				protocol = thisnode.findValue("protocol").asText();
				String email_from = endhost.getEmailFrom();
				email_from = thisnode.findValue("email_from").asText();
				String url = endhost.getUrl();
				url = thisnode.findValue("url").asText();
				String options = endhost.getOptions();
				options = thisnode.findValue("options").asText();
				String destination = endhost.getDestination();
				destination = thisnode.findValue("destination").asText();
			
				deserialized.setEndhostConfiguration(endhost);
				break;
			
			case "endpoint":
				Configuration.EndpointConfiguration endpoint = new Configuration.EndpointConfiguration();
				deserialized.setEndpointConfiguration(endpoint);
				break;
			
			case "fieldmodifier":
				Configuration.FieldmodifierConfiguration fieldmodifier = new Configuration.FieldmodifierConfiguration();
				deserialized.setFieldmodifierConfiguration(fieldmodifier);
				break;
			
			case "firewall":
				Configuration.FirewallConfiguration firewall = new Configuration.FirewallConfiguration();
				List<Configuration.FirewallConfiguration.Elements> fwelements = firewall.getElements();
				Configuration.FirewallConfiguration.Elements element;
				String src, dest;
				Map.Entry<String, JsonNode> entry;
				Iterator<Map.Entry<String, JsonNode>> current = elements.next().fields();
				while(current.hasNext()) {
					entry = current.next();
					element = new Configuration.FirewallConfiguration.Elements();
					src = element.getSource();
					dest = element.getDestination();
					src = entry.getKey();
					dest = entry.getValue().asText();
					fwelements.add(element);
				}
				deserialized.setFirewallConfiguration(firewall);
				break;
			
			case "mailclient":
				Configuration.MailclientConfiguration mailclient = new Configuration.MailclientConfiguration();
				String mailserv = mailclient.getMailserver();
				mailserv = elements.next().findValue("mailserver").asText();
				deserialized.setMailclientConfiguration(mailclient);
				break;
			
			case "mailserver":
				Configuration.MailserverConfiguration mailserver = new Configuration.MailserverConfiguration();
				deserialized.setMailserverConfiguration(mailserver);
				break;
			
			case "nat":
				Configuration.NatConfiguration nat = new Configuration.NatConfiguration();
				List<String> natsource = nat.getSource();
				while(elements.hasNext()) {
					natsource.add(elements.next().asText());
				}
				deserialized.setNatConfiguration(nat);
				break;	
			
			case "vpnaccess":
				Configuration.VpnaccessConfiguration vpnaccess = new Configuration.VpnaccessConfiguration();
				String vpnex = vpnaccess.getVpnexit();
				vpnex = elements.next().findValue("vpnexit").asText();
				deserialized.setVpnaccessConfiguration(vpnaccess);
				break;	
			
			case "vpnexit":
				Configuration.VpnexitConfiguration vpnexit = new Configuration.VpnexitConfiguration();
				String vpnacc = vpnexit.getVpnaccess();
				vpnacc = elements.next().findValue("vpnaccess").asText();
				deserialized.setVpnexitConfiguration(vpnexit);
				break;	
			
			case "webclient":
				Configuration.WebclientConfiguration webclient = new Configuration.WebclientConfiguration();
				String webserv = webclient.getNameWebServer();
				webserv = elements.next().findValue("webserver").asText();
				deserialized.setWebclientConfiguration(webclient);
				break;	
				
			case "webserver":
				Configuration.WebserverConfiguration webserver = new Configuration.WebserverConfiguration();
				deserialized.setWebserverConfiguration(webserver);
				break;	

			default:
				Configuration.FieldmodifierConfiguration defaultForwarder = new Configuration.FieldmodifierConfiguration();
				deserialized.setFieldmodifierConfiguration(defaultForwarder);
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
