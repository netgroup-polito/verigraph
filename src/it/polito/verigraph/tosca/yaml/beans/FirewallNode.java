package it.polito.verigraph.tosca.yaml.beans;

import java.util.Map;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

public class FirewallNode extends NodeTemplateYaml {
	private FirewallConfigurationYaml properties;

	public FirewallConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(FirewallConfigurationYaml properties) {
		this.properties = properties;
	}

	public class FirewallConfigurationYaml implements ConfigurationYaml{
		private Map<String, String> elements;

		public Map<String, String> getElements() {
			return elements;
		}

		public void setElements(Map<String, String> elements) {
			this.elements = elements;
		}
	}

}
