package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

public class MailClientNode extends NodeTemplateYaml {
	private MailClientConfigurationYaml properties;

	public MailClientConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(MailClientConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class MailClientConfigurationYaml implements ConfigurationYaml{
		private String mailserver;

		public String getMailserver() {
			return mailserver;
		}

		public void setMailserver(String mailserver) {
			this.mailserver = mailserver;
		}
	}

}
