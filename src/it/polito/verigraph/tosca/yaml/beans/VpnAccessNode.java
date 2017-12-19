package it.polito.verigraph.tosca.yaml.beans;

public class VpnAccessNode extends NodeTemplateYaml{
	private VpnAccessConfigurationYaml properties;

	public VpnAccessConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(VpnAccessConfigurationYaml properties) {
		this.properties = properties;
	}
}
