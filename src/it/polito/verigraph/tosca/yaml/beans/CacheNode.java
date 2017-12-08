package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class CacheNode extends NodeTemplateYaml {
	private CacheConfigurationYaml properties;

	public CacheConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(CacheConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) 
	public class CacheConfigurationYaml {
		private List<String> resources;

		public List<String> getResources() {
			return resources;
		}

		public void setResources(List<String> resources) {
			this.resources = resources;
		}
	}

}
