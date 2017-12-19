package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class CacheConfigurationYaml implements ConfigurationYaml {
	private List<String> resources;

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

}
