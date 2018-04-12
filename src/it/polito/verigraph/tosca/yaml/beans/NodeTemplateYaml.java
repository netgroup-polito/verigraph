/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.yaml.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.*;

//These annotations allow polimorphic deserialization of yaml text into beans by using the type field of each node
//In case a specified type is unknown the default implementation will be FieldModifierNode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type", defaultImpl=FieldModifierNode.class, visible= true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AntispamNode.class, name="verigraph.nodeTypes.Antispam"),
    @JsonSubTypes.Type(value = CacheNode.class, name="verigraph.nodeTypes.Cache"),
    @JsonSubTypes.Type(value = DpiNode.class, name="verigraph.nodeTypes.Dpi"),
    @JsonSubTypes.Type(value = EndhostNode.class, name="verigraph.nodeTypes.Endhost"),
    @JsonSubTypes.Type(value = EndpointNode.class, name="verigraph.nodeTypes.Endpoint"),
    @JsonSubTypes.Type(value = FieldModifierNode.class, name="verigraph.nodeTypes.FieldModifier"),
    @JsonSubTypes.Type(value = FirewallNode.class, name="verigraph.nodeTypes.Firewall"),
    @JsonSubTypes.Type(value = MailClientNode.class, name="verigraph.nodeTypes.MailClient"),
    @JsonSubTypes.Type(value = MailServerNode.class, name="verigraph.nodeTypes.MailServer"),
    @JsonSubTypes.Type(value = NatNode.class, name="verigraph.nodeTypes.Nat"),
    @JsonSubTypes.Type(value = VpnAccessNode.class, name="verigraph.nodeTypes.VpnAccess"),
    @JsonSubTypes.Type(value = VpnExitNode.class, name="verigraph.nodeTypes.VpnExit"),
    @JsonSubTypes.Type(value = WebClientNode.class, name="verigraph.nodeTypes.WebClient"),
    @JsonSubTypes.Type(value = WebServerNode.class, name="verigraph.nodeTypes.WebServer")
})
public class NodeTemplateYaml {
    private String name;
    private String type;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    //	//Generic YamlNode configuration to be extended in single nodes
    //	public interface ConfigurationYaml {
    //	}

}
