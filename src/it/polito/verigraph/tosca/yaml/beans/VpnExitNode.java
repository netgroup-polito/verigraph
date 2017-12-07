package it.polito.verigraph.tosca.yaml.beans;

public class VpnExitNode extends NodeTemplateYaml {
	private VpnExitConfigurationYaml properties;

	public VpnExitConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(VpnExitConfigurationYaml properties) {
		this.properties = properties;
	}

}
