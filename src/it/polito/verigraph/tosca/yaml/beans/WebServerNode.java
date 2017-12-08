package it.polito.verigraph.tosca.yaml.beans;

import java.util.List;

public class WebServerNode extends NodeTemplateYaml {
	private WebServerConfigurationYaml properties;

	public WebServerConfigurationYaml getProperties() {
		return properties;
	}

	public void setProperties(WebServerConfigurationYaml properties) {
		this.properties = properties;
	}

	public class WebServerConfigurationYaml {
		public List<String> names;

		public List<String> getNames() {
			return names;
		}

		public void setNames(List<String> names) {
			this.names = names;
		}
	}

}
