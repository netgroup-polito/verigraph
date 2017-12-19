package it.polito.verigraph.tosca.yaml.beans;

public class MailClientNode extends NodeTemplateYaml {
	private MailClientConfigurationYaml properties;

	public MailClientConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(MailClientConfigurationYaml properties) {
		this.properties = properties;
	}
}
