package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class WebClientNode extends NodeTemplateYaml {
	private WebClientConfigurationYaml properties;

	public WebClientConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(WebClientConfigurationYaml properties) {
		this.properties = properties;
	}

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

}
