package it.polito.verigraph.tosca.yaml.beans;

public class FieldModifierNode extends NodeTemplateYaml {
	private FieldModifierConfigurationYaml properties;

	public FieldModifierConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(FieldModifierConfigurationYaml properties) {
		this.properties = properties;
	}
}
