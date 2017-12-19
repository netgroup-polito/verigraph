package it.polito.verigraph.tosca.yaml.beans;

public class CacheNode extends NodeTemplateYaml {
	private CacheConfigurationYaml properties;

	public CacheConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(CacheConfigurationYaml properties) {
		this.properties = properties;
	}

}
