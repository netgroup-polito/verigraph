package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WebClientConfigurationYaml {
	private String nameWebServer;

	public String getNameWebServer() {
		return nameWebServer;
	}

	public void setNameWebServer(String nameWebServer) {
		this.nameWebServer = nameWebServer;
	}

}
