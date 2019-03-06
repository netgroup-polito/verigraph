/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.verigraph.deserializer.PolicyCustomDeserializer;
import it.polito.verigraph.serializer.CustomMapSerializer;
import it.polito.verigraph.validation.exception.ValidationException;

@ApiModel(value = "Policy")
@XmlRootElement
@JsonDeserialize(using = PolicyCustomDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Policy {

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private long id;

    @ApiModelProperty(name = "policyName", required = true, example = "pEx", value = "The name of the policy can be any string")
    @JsonProperty("policyName")
    private String name;
    
    @ApiModelProperty(required = true, example = "src", value = "The name of the source node can be any string")
    private String source;
    
    @ApiModelProperty(name = "target",required = true, example = "dest", value = "The name of the destination node can be any string")
    @JsonProperty("target")
    private String destination;
    
    @ApiModelProperty(required = true)
    private JsonNode trafficFlow;
    
    @ApiModelProperty(name = "restrictions",
            notes = "Restrictions",
            dataType = "it.polito.verigraph.model.Restrictions")
    private Restrictions restrictions = new Restrictions();
    
    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private Set<Link>links= new HashSet<>();

    public Policy() {
    
    }

    public Policy(long id, String name, String source, String destination, JsonNode trafficFlow) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.trafficFlow = trafficFlow;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
    
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public JsonNode getTrafficFlow() {
        return trafficFlow;
    }

    public void setTrafficFlow(JsonNode trafficFlow) {
        this.trafficFlow = trafficFlow;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }
    
    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public void addLink(String url, String rel) {
        Link link = new Link();
        link.setLink(url);
        link.setRel(rel);
        links.add(link);
    }

	public List<List<String>> getFunctions() {
		return restrictions.getFunctions();
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else
            return false;
    }
}
