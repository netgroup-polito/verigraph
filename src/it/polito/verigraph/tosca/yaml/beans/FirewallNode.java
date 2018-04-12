/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.yaml.beans;

public class FirewallNode extends NodeTemplateYaml {
    private FirewallConfigurationYaml properties;

    public FirewallConfigurationYaml getProperties() {
        return properties;
    }

    public void setProperties(FirewallConfigurationYaml properties) {
        this.properties = properties;
    }

}
