package it.polito.verigraph.tosca.yaml.beans;

public class FirewallNode extends NodeTemplateYaml {
	private FirewallConfigurationYaml properties;

	public FirewallConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(FirewallConfigurationYaml properties) {
		this.properties = properties;
	}

}
