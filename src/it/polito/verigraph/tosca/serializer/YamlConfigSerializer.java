/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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

//Custom serializer for YamlToscaConfigurationObject conversion to JSON
public class YamlConfigSerializer extends StdSerializer<ConfigurationYaml> {

    //Automatically generated VersionUID
    private static final long serialVersionUID = 9102508941195129607L;

    public YamlConfigSerializer() {
        this(null);
    }

    public YamlConfigSerializer(Class<ConfigurationYaml> t) {
        super(t);
    }

    @Override
    public void serialize(
            ConfigurationYaml value, JsonGenerator jgen, SerializerProvider provider)
                    throws IOException, JsonProcessingException {

        if(value instanceof AntispamConfigurationYaml) {
            jgen.writeStartArray();
            for(String source : ((AntispamConfigurationYaml) value).getSources()) {
                jgen.writeString(source);
            }
            jgen.writeEndArray();

        }else if(value instanceof CacheConfigurationYaml) {
            jgen.writeStartArray();
            for(String resource : ((CacheConfigurationYaml) value).getResources()) {
                jgen.writeString(resource);
            }
            jgen.writeEndArray();

        }else if(value instanceof DpiConfigurationYaml) {
            jgen.writeStartArray();
            for(String notAllowed : ((DpiConfigurationYaml) value).getNotAllowedList()) {
                jgen.writeString(notAllowed);
            }
            jgen.writeEndArray();

        }else if(value instanceof EndhostConfigurationYaml) {
            jgen.writeStartArray();
            jgen.writeStartObject();

            EndhostConfigurationYaml endhost = (EndhostConfigurationYaml) value;

            if(endhost.getBody() != null)
                jgen.writeObjectField("body", endhost.getBody());
            if(endhost.getSequence() != 0)
                jgen.writeObjectField("sequence", endhost.getSequence());
            if(endhost.getProtocol() != null)
                jgen.writeObjectField("protocol", endhost.getProtocol());
            if(endhost.getEmail_from() != null)
                jgen.writeObjectField("email_from", endhost.getEmail_from());
            if(endhost.getUrl() != null)
                jgen.writeObjectField("url", endhost.getUrl());
            if(endhost.getOptions() != null)
                jgen.writeObjectField("options", endhost.getOptions());
            if(endhost.getDestination() != null)
                jgen.writeObjectField("destination", endhost.getDestination());

            jgen.writeEndObject();
            jgen.writeEndArray();

        }else if(value instanceof EndpointConfigurationYaml) {
            jgen.writeStartArray();
            jgen.writeEndArray();

        }else if(value instanceof FieldModifierConfigurationYaml) {
            jgen.writeStartArray();
            jgen.writeEndArray();

        }else if(value instanceof FirewallConfigurationYaml) {
            jgen.writeStartArray();
            FirewallConfigurationYaml fw = (FirewallConfigurationYaml) value;

            for(Map.Entry<String, String> entry : fw.getElements().entrySet()) {
                if(entry.getKey()!= null && entry.getValue() != null) {
                    jgen.writeStartObject();
                    jgen.writeObjectField(entry.getKey(),  entry.getValue());
                    jgen.writeEndObject();
                }
            }
            jgen.writeEndArray();

        }else if(value instanceof MailClientConfigurationYaml) {
            jgen.writeStartArray();

            if(((MailClientConfigurationYaml) value).getMailserver() != null) {
                jgen.writeStartObject();
                jgen.writeObjectField("mailserver", ((MailClientConfigurationYaml) value).getMailserver());
                jgen.writeEndObject();
            }
            jgen.writeEndArray();

        }else if(value instanceof MailServerConfigurationYaml) {
            jgen.writeStartArray();
            jgen.writeEndArray();

        }else if(value instanceof NatConfigurationYaml) {
            jgen.writeStartArray();

            for(String source : ((NatConfigurationYaml) value).getSources()) {
                jgen.writeString(source);
            }

            jgen.writeEndArray();

        }else if(value instanceof VpnAccessConfigurationYaml) {
            jgen.writeStartArray();

            if(((VpnAccessConfigurationYaml) value).getVpnexit()!= null) {
                jgen.writeStartObject();
                jgen.writeObjectField("vpnexit", ((VpnAccessConfigurationYaml) value).getVpnexit());
                jgen.writeEndObject();
            }

            jgen.writeEndArray();

        }else if(value instanceof VpnExitConfigurationYaml) {
            jgen.writeStartArray();

            if(((VpnExitConfigurationYaml) value).getVpnaccess()!= null) {
                jgen.writeStartObject();
                jgen.writeObjectField("vpnaccess", ((VpnExitConfigurationYaml) value).getVpnaccess());
                jgen.writeEndObject();
            }

            jgen.writeEndArray();

        }else if(value instanceof WebClientConfigurationYaml) {
            jgen.writeStartArray();

            if(((WebClientConfigurationYaml) value).getNameWebServer() != null) {
                jgen.writeStartObject();
                jgen.writeObjectField("webserver", ((WebClientConfigurationYaml) value).getNameWebServer());
                jgen.writeEndObject();
            }

            jgen.writeEndArray();

        }else if(value instanceof WebServerConfigurationYaml) {
            jgen.writeStartArray();
            jgen.writeEndArray();

        }else  {
            jgen.writeStartArray();
            jgen.writeEndArray();
        }

    }
}
