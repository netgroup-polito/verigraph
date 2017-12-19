package it.polito.verigraph.tosca.yaml.beans;

import java.util.Map;

public class FirewallConfigurationYaml implements ConfigurationYaml{
	private Map<String, String> elements;

	public Map<String, String> getElements() {
		return elements;
	}

	public void setElements(Map<String, String> elements) {
		this.elements = elements;
	}

}
