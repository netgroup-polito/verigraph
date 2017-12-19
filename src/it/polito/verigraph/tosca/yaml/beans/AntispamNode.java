package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AntispamNode extends NodeTemplateYaml {
	private AntispamConfigurationYaml properties;

	public AntispamConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(AntispamConfigurationYaml properties) {
		this.properties = properties;
	}
}
