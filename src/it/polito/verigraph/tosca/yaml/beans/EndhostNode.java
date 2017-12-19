package it.polito.verigraph.tosca.yaml.beans;

public class EndhostNode extends NodeTemplateYaml{
	private EndhostConfigurationYaml properties;

	public EndhostConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(EndhostConfigurationYaml properties) {
		this.properties = properties;
	}

}
