package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class VpnAccessNode extends NodeTemplateYaml{
	private VpnAccessConfigurationYaml properties;

	public VpnAccessConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(VpnAccessConfigurationYaml properties) {
		this.properties = properties;
	}

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

}
