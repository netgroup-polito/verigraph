package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VpnExitConfigurationYaml implements ConfigurationYaml {
	private String vpnaccess;

	public String getVpnaccess() {
		return vpnaccess;
	}

	public void setVpnaccess(String vpnaccess) {
		this.vpnaccess = vpnaccess;
	}
}
