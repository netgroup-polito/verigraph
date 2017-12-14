package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

public class EndhostNode extends NodeTemplateYaml{
	private EndhostConfigurationYaml properties;

	public EndhostConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(EndhostConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) 
	public class EndhostConfigurationYaml implements ConfigurationYaml {
		private String body;
		private int sequence;
		private String protocol;
		private String email_from;
		private String url;
		private String options;
		private String destination;

		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
		public int getSequence() {
			return sequence;
		}
		public void setSequence(int sequence) {
			this.sequence = sequence;
		}
		public String getProtocol() {
			return protocol;
		}
		public void setProtocol(String protocol) {
			this.protocol = protocol;
		}
		public String getEmail_from() {
			return email_from;
		}
		public void setEmail_from(String email_from) {
			this.email_from = email_from;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getOptions() {
			return options;
		}
		public void setOptions(String options) {
			this.options = options;
		}
		public String getDestination() {
			return destination;
		}
		public void setDestination(String destination) {
			this.destination = destination;
		}
	}
}
