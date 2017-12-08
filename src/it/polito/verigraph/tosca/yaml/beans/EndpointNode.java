package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class EndpointNode extends NodeTemplateYaml {
	private EndpointConfigurationYaml properties;

	public EndpointConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(EndpointConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class EndpointConfigurationYaml {
		private List<String> names;

		public List<String> getNames() {
			return names;
		}

		public void setNames(List<String> names) {
			this.names = names;
		}	
	}

}
