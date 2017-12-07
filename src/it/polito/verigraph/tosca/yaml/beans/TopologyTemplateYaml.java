package it.polito.verigraph.tosca.yaml.beans;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopologyTemplateYaml {
	private Map<String, NodeTemplateYaml> node_templates;
	private Map<String, RelationshipTemplate> relationship_templates;
	
	public Map<String, RelationshipTemplate> getRelationship_templates() {
		return relationship_templates;
	}

	public void setRelationship_templates(Map<String, RelationshipTemplate> relationship_templates) {
		this.relationship_templates = relationship_templates;
	}

	public Map<String, NodeTemplateYaml> getNode_templates() {
		return node_templates;
	}

	public void setNode_templates(Map<String, NodeTemplateYaml> node_templates) {
		this.node_templates = node_templates;
	}

}
