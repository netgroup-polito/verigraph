package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class DpiNode {
	private DpiConfigurationYaml properties;

	public DpiConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(DpiConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true) 
	public class DpiConfigurationYaml {
		private List<String> notAllowedList;

		public List<String> getNotAllowedList() {
			return notAllowedList;
		}

		public void setNotAllowedList(List<String> notAllowedList) {
			this.notAllowedList = notAllowedList;
		}
	}

}
