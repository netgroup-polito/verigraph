package it.polito.verigraph.tosca.yaml.beans;

public class NatNode extends NodeTemplateYaml {
	private NatConfigurationYaml properties;

	public NatConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(NatConfigurationYaml properties) {
		this.properties = properties;
	}
}
