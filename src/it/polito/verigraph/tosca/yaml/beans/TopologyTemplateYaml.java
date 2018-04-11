/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.yaml.beans;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TopologyTemplateYaml {
    private Map<String, NodeTemplateYaml> node_templates;
    private Map<String, RelationshipTemplateYaml> relationship_templates;

    public Map<String, RelationshipTemplateYaml> getRelationship_templates() {
        return relationship_templates;
    }

    public void setRelationship_templates(Map<String, RelationshipTemplateYaml> relationship_templates) {
        this.relationship_templates = relationship_templates;
    }

    public Map<String, NodeTemplateYaml> getNode_templates() {
        return node_templates;
    }

    public void setNode_templates(Map<String, NodeTemplateYaml> node_templates) {
        this.node_templates = node_templates;
    }

}
