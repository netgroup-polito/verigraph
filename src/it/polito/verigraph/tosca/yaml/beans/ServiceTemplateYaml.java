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
import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceTemplateYaml {
    private Map<String, String> metadata;
    private String description;
    private TopologyTemplateYaml topology_template;

    public Map<String, String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public TopologyTemplateYaml getTopology_template() {
        return topology_template;
    }
    public void setTopology_template(TopologyTemplateYaml topology_template) {
        this.topology_template = topology_template;
    }

}
