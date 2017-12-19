package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public class DpiConfigurationYaml implements ConfigurationYaml{
	private List<String> notAllowedList;

	public List<String> getNotAllowedList() {
		return notAllowedList;
	}

	public void setNotAllowedList(List<String> notAllowedList) {
		this.notAllowedList = notAllowedList;
	}

}
