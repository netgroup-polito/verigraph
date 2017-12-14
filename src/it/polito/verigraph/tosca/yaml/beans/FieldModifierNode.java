package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

public class FieldModifierNode extends NodeTemplateYaml {
	private FieldModifierConfigurationYaml properties;

	public FieldModifierConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(FieldModifierConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class FieldModifierConfigurationYaml implements ConfigurationYaml {
		private List<String> names;

		public List<String> getNames() {
			return names;
		}

		public void setNames(List<String> names) {
			this.names = names;
		}
	}

}
