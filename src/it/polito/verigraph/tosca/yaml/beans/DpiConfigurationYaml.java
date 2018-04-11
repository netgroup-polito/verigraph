/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
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
