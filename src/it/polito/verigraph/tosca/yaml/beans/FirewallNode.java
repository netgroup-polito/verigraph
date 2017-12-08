package it.polito.verigraph.tosca.yaml.beans;

import java.util.Map;

public class FirewallNode extends NodeTemplateYaml {
	private FirewallConfigurationYaml properties;

	public FirewallConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(FirewallConfigurationYaml properties) {
		this.properties = properties;
	}

	public class FirewallConfigurationYaml {
		private Map<String, String> elements;

		public Map<String, String> getElements() {
			return elements;
		}

		public void setElements(Map<String, String> elements) {
			this.elements = elements;
		}
	}

}
