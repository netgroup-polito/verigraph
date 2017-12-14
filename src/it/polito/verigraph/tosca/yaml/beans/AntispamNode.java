package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.*;

import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml.ConfigurationYaml;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AntispamNode extends NodeTemplateYaml {
	private AntispamConfigurationYaml properties;

	public AntispamConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(AntispamConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) 
	public class AntispamConfigurationYaml implements ConfigurationYaml {
		private List<String> sources;

		public List<String> getSources() {
			return sources;
		}

		public void setSources(List<String> sources) {
			this.sources = sources;
		}
	}

}
