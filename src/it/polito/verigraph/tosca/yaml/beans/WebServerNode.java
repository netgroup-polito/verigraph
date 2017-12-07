package it.polito.verigraph.tosca.yaml.beans;

public class WebServerNode extends NodeTemplateYaml {
	private WebServerConfigurationYaml properties;

	public WebServerConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(WebServerConfigurationYaml properties) {
		this.properties = properties;
	}
}
