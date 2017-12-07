package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VpnAccessConfigurationYaml {
	private String vpnexit;

	public String getVpnexit() {
		return vpnexit;
	}

	public void setVpnexit(String vpnexit) {
		this.vpnexit = vpnexit;
	}

}
