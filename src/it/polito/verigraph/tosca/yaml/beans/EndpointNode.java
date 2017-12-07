package it.polito.verigraph.tosca.yaml.beans;

public class EndpointNode extends NodeTemplateYaml {
	private EndpointConfigurationYaml properties;

	public EndpointConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(EndpointConfigurationYaml properties) {
		this.properties = properties;
	}

}
