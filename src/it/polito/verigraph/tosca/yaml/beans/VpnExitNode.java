package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class VpnExitNode extends NodeTemplateYaml {
	private VpnExitConfigurationYaml properties;

	public VpnExitConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(VpnExitConfigurationYaml properties) {
		this.properties = properties;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class VpnExitConfigurationYaml {
		private String vpnaccess;

		public String getVpnaccess() {
			return vpnaccess;
		}

		public void setVpnaccess(String vpnaccess) {
			this.vpnaccess = vpnaccess;
		}
	}

}
