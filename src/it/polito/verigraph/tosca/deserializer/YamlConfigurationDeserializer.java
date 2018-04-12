/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.deserializer;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.tosca.yaml.beans.AntispamConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.CacheConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.ConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.DpiConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndhostConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.EndpointConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.FirewallConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.MailServerConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.NatConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnExitConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebClientConfigurationYaml;
import it.polito.verigraph.tosca.yaml.beans.WebServerConfigurationYaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class YamlConfigurationDeserializer extends JsonDeserializer<ConfigurationYaml> {

    @Override
    public ConfigurationYaml deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        ConfigurationYaml deserialized;
        boolean emptyConfig = false;

        try {
            //Get the content from the array wrapping the JSON configuration
            final Iterator<JsonNode> elements = node.elements();

            if(!elements.hasNext()) {
                //System.out.println("The provided configuration is empty.");
                emptyConfig = true;
            }

            switch ((String)ctxt.findInjectableValue("type", null, null)) {

            case "antispam":
                if(emptyConfig) {
                    deserialized = new AntispamConfigurationYaml();
                    break;
                }
                AntispamConfigurationYaml antispam = new AntispamConfigurationYaml();
                antispam.setSources(new ArrayList<String>());
                while (elements.hasNext()) {
                    antispam.getSources().add(elements.next().asText());
                }
                deserialized = antispam;
                break;

            case "cache":
                if(emptyConfig) {
                    deserialized = new CacheConfigurationYaml();
                    break;
                }
                CacheConfigurationYaml cache = new CacheConfigurationYaml();
                cache.setResources(new ArrayList<String>());
                while(elements.hasNext()) {
                    cache.getResources().add(elements.next().asText());
                }
                deserialized = cache;
                break;

            case "dpi":
                if(emptyConfig) {
                    deserialized = new DpiConfigurationYaml();
                    break;
                }
                DpiConfigurationYaml dpi = new DpiConfigurationYaml();
                dpi.setNotAllowedList(new ArrayList<String>());
                while(elements.hasNext()) {
                    dpi.getNotAllowedList().add(elements.next().asText());
                }
                deserialized = dpi;
                break;

            case "endhost":
                if(emptyConfig) {
                    deserialized = new EndhostConfigurationYaml();
                    break;
                }
                EndhostConfigurationYaml endhost = new EndhostConfigurationYaml();
                JsonNode thisnode = elements.next();
                if(thisnode.has("body"))
                    endhost.setBody(thisnode.findValue("body").asText());
                if(thisnode.has("sequence"))
                    endhost.setSequence(Integer.valueOf(thisnode.findValue("sequence").asText()));
                if(thisnode.has("protocol"))
                    endhost.setProtocol(thisnode.findValue("protocol").asText());
                if(thisnode.has("email_from"))
                    endhost.setEmail_from(thisnode.findValue("email_from").asText());
                if(thisnode.has("options"))
                    endhost.setOptions(thisnode.findValue("options").asText());
                if(thisnode.has("url"))
                    endhost.setUrl(thisnode.findValue("url").asText());
                if(thisnode.has("destination"))
                    endhost.setDestination(thisnode.findValue("destination").asText());

                deserialized = endhost;
                break;

            case "endpoint":
                if(emptyConfig) {
                    deserialized = new EndpointConfigurationYaml();
                    break;
                }
                EndpointConfigurationYaml endpoint = new EndpointConfigurationYaml();
                deserialized = endpoint;
                break;

            case "fieldmodifier":
                if(emptyConfig) {
                    deserialized = new FieldModifierConfigurationYaml();
                    break;
                }
                FieldModifierConfigurationYaml fieldmodifier = new FieldModifierConfigurationYaml();
                deserialized = fieldmodifier;
                break;

            case "firewall":
                if(emptyConfig) {
                    deserialized = new FirewallConfigurationYaml();
                    break;
                }
                FirewallConfigurationYaml firewall = new FirewallConfigurationYaml();
                firewall.setElements(new HashMap<String,String>());
                JsonNode current = elements.next();
                Iterator<Map.Entry<String, JsonNode>> iter = current.fields();

                while (iter.hasNext()) {
                    Map.Entry<String, JsonNode> entry = iter.next();
                    firewall.getElements().put(entry.getKey(), entry.getValue().asText());
                }

                deserialized = firewall;
                break;

            case "mailclient":
                if(emptyConfig) {
                    deserialized = new MailClientConfigurationYaml();
                    break;
                }
                MailClientConfigurationYaml mailclient = new MailClientConfigurationYaml();
                mailclient.setMailserver(elements.next().findValue("mailserver").asText());
                deserialized = mailclient;
                break;

            case "mailserver":
                if(emptyConfig) {
                    deserialized = new MailServerConfigurationYaml();
                    break;
                }
                MailServerConfigurationYaml mailserver = new MailServerConfigurationYaml();
                deserialized = mailserver;
                break;

            case "nat":
                if(emptyConfig) {
                    deserialized = new NatConfigurationYaml();
                    break;
                }
                NatConfigurationYaml nat = new NatConfigurationYaml();
                nat.setSources(new ArrayList<String>());
                while(elements.hasNext()) {
                    nat.getSources().add(elements.next().asText());
                }
                deserialized = nat;
                break;

            case "vpnaccess":
                if(emptyConfig) {
                    deserialized = new VpnAccessConfigurationYaml();
                    break;
                }
                VpnAccessConfigurationYaml vpnaccess = new VpnAccessConfigurationYaml();
                vpnaccess.setVpnexit(elements.next().findValue("vpnexit").asText());
                deserialized = vpnaccess;
                break;

            case "vpnexit":
                if(emptyConfig) {
                    deserialized = new VpnExitConfigurationYaml();
                    break;
                }
                VpnExitConfigurationYaml vpnexit = new VpnExitConfigurationYaml();
                vpnexit.setVpnaccess(elements.next().findValue("vpnaccess").asText());
                deserialized = vpnexit;
                break;

            case "webclient":
                if(emptyConfig) {
                    deserialized = new WebClientConfigurationYaml();
                    break;
                }
                WebClientConfigurationYaml webclient = new WebClientConfigurationYaml();
                webclient.setNameWebServer(elements.next().findValue("webserver").asText());
                deserialized = webclient;
                break;

            case "webserver":
                if(emptyConfig) {
                    deserialized = new WebServerConfigurationYaml();
                    break;
                }
                WebServerConfigurationYaml webserver = new WebServerConfigurationYaml();
                deserialized = webserver;
                break;

            default:
                deserialized = new FieldModifierConfigurationYaml();
                break;
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            //System.err.println("Error converting the Json to YamlConfiguration");
            e.printStackTrace();
            return new FieldModifierConfigurationYaml();
        }

        return deserialized;

    }

}