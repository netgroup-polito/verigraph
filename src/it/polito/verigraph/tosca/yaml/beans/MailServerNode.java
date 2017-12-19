package it.polito.verigraph.tosca.yaml.beans;

public class MailServerNode extends NodeTemplateYaml {
	private MailServerConfigurationYaml properties;

	public MailServerConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(MailServerConfigurationYaml properties) {
		this.properties = properties;
	}
}
