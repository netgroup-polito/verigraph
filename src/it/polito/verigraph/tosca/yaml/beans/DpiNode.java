package it.polito.verigraph.tosca.yaml.beans;

public class DpiNode extends NodeTemplateYaml {
	private DpiConfigurationYaml properties;

	public DpiConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(DpiConfigurationYaml properties) {
		this.properties = properties;
	}
	
}
