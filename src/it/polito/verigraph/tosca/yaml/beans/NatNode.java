package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

public class NatNode extends NodeTemplateYaml {
	private NatConfigurationYaml properties;

	public NatConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(NatConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class NatConfigurationYaml implements ConfigurationYaml{
		private List<String> sources;

		public List<String> getSources() {
			return sources;
		}

		public void setSources(List<String> sources) {
			this.sources = sources;
		}
	}

}