package it.polito.verigraph.tosca.yaml.beans;

public class WebClientNode extends NodeTemplateYaml {
	private WebClientConfigurationYaml properties;

	public WebClientConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(WebClientConfigurationYaml properties) {
		this.properties = properties;
	}
}
