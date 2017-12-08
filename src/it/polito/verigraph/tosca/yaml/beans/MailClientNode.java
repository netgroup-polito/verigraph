package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class MailClientNode extends NodeTemplateYaml {
	private MailClientConfigurationYaml properties;

	public MailClientConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(MailClientConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class MailClientConfigurationYaml {
		private String mailserver;

		public String getMailserver() {
			return mailserver;
		}

		public void setMailserver(String mailserver) {
			this.mailserver = mailserver;
		}
	}

}
