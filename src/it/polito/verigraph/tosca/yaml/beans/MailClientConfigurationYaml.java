package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
