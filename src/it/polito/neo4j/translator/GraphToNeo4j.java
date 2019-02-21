/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.neo4j.translator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;


import it.polito.neo4j.jaxb.AddressType;
import it.polito.neo4j.jaxb.Antispam;
import it.polito.neo4j.jaxb.Cache;
import it.polito.neo4j.jaxb.Dpi;
import it.polito.neo4j.jaxb.Elements;
import it.polito.neo4j.jaxb.Endpoint;
import it.polito.neo4j.jaxb.Firewall;
import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.Node;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.jaxb.PacketType;
import it.polito.neo4j.jaxb.Policy;
import it.polito.neo4j.jaxb.ProtocolTypes;
import it.polito.neo4j.jaxb.TransportProtocolTypes;
import it.polito.neo4j.jaxb.Vpnaccess;
import it.polito.neo4j.jaxb.Vpnexit;
import it.polito.neo4j.jaxb.Webclient;
import it.polito.neo4j.jaxb.Webserver;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.service.VerigraphLogger;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.Mailclient;
import it.polito.neo4j.jaxb.Mailserver;
import it.polito.neo4j.jaxb.Nat;
import it.polito.neo4j.jaxb.Neighbour;

public class GraphToNeo4j {
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();
    public static it.polito.neo4j.jaxb.Graph generateObject(it.polito.verigraph.model.Graph gr) throws JsonParseException, JsonMappingException, IOException {
        it.polito.neo4j.jaxb.Graph graph;
        graph=(new ObjectFactory()).createGraph();
        graph.setId(gr.getId());

        List<it.polito.neo4j.jaxb.Node> nodes=new ArrayList<it.polito.neo4j.jaxb.Node>();
        for(Map.Entry<Long, it.polito.verigraph.model.Node> c : gr.getNodes().entrySet()){
            it.polito.neo4j.jaxb.Node node=(new ObjectFactory()).createNode();
            node.setId(c.getValue().getId());
            node.setName(c.getValue().getName());
            node.setFunctionalType(FunctionalTypes.fromValue(c.getValue().getFunctional_type().toUpperCase()));
            List<Neighbour> neighbours=new ArrayList<Neighbour>();

            /* setNeighbours*/
            for(Map.Entry<Long, it.polito.verigraph.model.Neighbour> a : c.getValue().getNeighbours().entrySet()){
                Neighbour neighbour=(new ObjectFactory()).createNeighbour();
                neighbour.setId(a.getValue().getId());
                neighbour.setName(a.getValue().getName());
                neighbours.add(neighbour);
            }
            node.getNeighbour().addAll(neighbours);

            /* setConfigurations */
            it.polito.neo4j.jaxb.Configuration configuration=(new ObjectFactory()).createConfiguration();
            JsonNode json=c.getValue().getConfiguration().getConfiguration();
            setConfiguration(configuration, c.getValue(), json);
            node.setConfiguration(configuration);
            nodes.add(node);
        }
        graph.getNode().addAll(nodes);
        
        // Add policies to neo4j graph object
        List<it.polito.neo4j.jaxb.Policy> policyList = new ArrayList<it.polito.neo4j.jaxb.Policy>();
        for(Map.Entry<Long, it.polito.verigraph.model.Policy> p : gr.getPolicies().entrySet()){
            it.polito.neo4j.jaxb.Policy policy=(new ObjectFactory()).createPolicy();
            policy.setId(p.getValue().getId());
            policy.setName(p.getValue().getName());
            policy.setSource(p.getValue().getSource());
            policy.setDestination(p.getValue().getDestination());
            
            JsonNode trafficFlow = p.getValue().getTrafficFlow();
            setTrafficFlow(policy, trafficFlow);

            // setRestrictions
            it.polito.neo4j.jaxb.Restrictions restrictions = (new ObjectFactory()).createRestrictions();
            JsonNode json=p.getValue().getRestrictions().getRestrictions();
            setRestrictions(restrictions, p.getValue(), json);
            policy.setRestrictions(restrictions);
            policyList.add(policy);
        }
        
        it.polito.neo4j.jaxb.Policies policies = (new ObjectFactory()).createPolicies();
        policies.getPolicy().addAll(policyList);
        graph.setPolicies(policies);
        
        return graph;
    }

	public static it.polito.neo4j.jaxb.Configuration ConfToNeo4j(it.polito.verigraph.model.Configuration nodeConfiguration, it.polito.verigraph.model.Node node) throws JsonParseException, JsonMappingException, IOException {
        it.polito.neo4j.jaxb.Configuration configuration=(new ObjectFactory()).createConfiguration();
        JsonNode nodes=nodeConfiguration.getConfiguration();
        setConfiguration(configuration, node, nodes);

        return configuration;

    }

    @SuppressWarnings("unchecked")
    private static void setConfiguration(it.polito.neo4j.jaxb.Configuration configuration, it.polito.verigraph.model.Node node, JsonNode nodes) throws JsonParseException, JsonMappingException, IOException {

        String empty="[]";

        switch(node.getFunctional_type().toUpperCase()){
        case "FIREWALL":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Firewall firewall=new Firewall();
            List<Elements> elements_list=new ArrayList<Elements>();


        	if (nodes.isArray()) {
    			for (JsonNode objNode : nodes) {
                    String src_id = objNode.get("source_id").textValue();
                    String dest_id = objNode.get("destination_id").textValue();
                    BigInteger src_port = objNode.get("source_port").bigIntegerValue();
                    BigInteger dest_port = objNode.get("destination_port").bigIntegerValue();
                    String proto = objNode.get("protocol").textValue();
                    
                    Elements e=new Elements();
                    e.setSource(src_id);
                    e.setDestination(dest_id);
                    e.setSrcPort(src_port);
                    e.setDestPort(dest_port);
                    e.setProtocol(TransportProtocolTypes.fromValue(proto));;
                    elements_list.add(e);
                }
        	}
            else{
                vlogger.logger.info("elements_list of Firewall " +node.getName()+" empty");
            }
        	
            firewall.getElements().addAll(elements_list);
            configuration.setFirewall(firewall);
            break;
        }
        case "ANTISPAM":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Antispam antispam=new Antispam();
            List<AddressType> source=new ArrayList<AddressType>();
            //List<String> source=new ArrayList<String>();

            if(!nodes.toString().equals(empty)){
                ObjectMapper mapper=new ObjectMapper();
                List<String> list=new ArrayList<String>();

                try {

                    list = mapper.readValue(nodes.toString(), ArrayList.class);

                    for(String s : list){
                    	AddressType address = new AddressType();
                    	address.setName(s);
                        source.add(address);
                    }

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                vlogger.logger.info("Antispam " +node.getName()+" empty");

            }
            antispam.getSource().addAll(source);
            configuration.setAntispam(antispam);
            break;
        }
        case "CACHE":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Cache cache=new Cache();
            List<AddressType> resource=new ArrayList<AddressType>();
            //List<String> resource=new ArrayList<String>();

            if(!nodes.toString().equals(empty)){
                ObjectMapper mapper=new ObjectMapper();

                List<String> list=new ArrayList<String>();
                try {

                    list = mapper.readValue(nodes.toString(), ArrayList.class);

                    for(String s : list){
                    	AddressType address = new AddressType();
                    	address.setName(s);
                        resource.add(address);
                    }

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                vlogger.logger.info("Cache " +node.getName()+" empty");

            }
            cache.getResource().addAll(resource);
            configuration.setCache(cache);
            break;

        }
        case "DPI":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Dpi dpi=new Dpi();
            List<String> notAllowed=new ArrayList<String>();

            if(!nodes.toString().equals(empty)){
                ObjectMapper mapper=new ObjectMapper();

                List<String> list=new ArrayList<String>();
                try {

                    list = mapper.readValue(nodes.toString(), ArrayList.class);

                    for(String s : list){
                        notAllowed.add(s);
                    }

                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                vlogger.logger.info("Dpi " +node.getName()+"  empty");

            }
            dpi.getNotAllowed().addAll(notAllowed);
            configuration.setDpi(dpi);
            break;
        }
        case "ENDHOST":
        case "FIELDMODIFIER":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            PacketType endhost=new PacketType();

            if(!nodes.toString().equals(empty)){
                ObjectMapper mapper=new ObjectMapper();
                java.util.Map<String, String> map=new LinkedHashMap();
                String input;
                Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(nodes.toString());
                if(matcher.find()){
                    input=matcher.group(1);
                }else
                    input=nodes.toString();

                try{
                    map=mapper.readValue(input, java.util.LinkedHashMap.class);
                    for(java.util.Map.Entry<String, String> m : map.entrySet()){
                        switch(m.getKey()){
                        case "body":{
                            endhost.setBody(m.getValue());
                            break;
                        }
                        case"sequence":{
                            endhost.setSequence(new BigInteger(m.getValue()));
                            break;
                        }
                        case "protocol":{
                            endhost.setProtocol(ProtocolTypes.fromValue(m.getValue()));
                            break;
                        }
                        case "email_from":{
                            endhost.setEmailFrom(m.getValue());
                            break;
                        }
                        case "url":{
                            endhost.setUrl(m.getValue());
                            break;
                        }
                        case "option":{
                            endhost.setOptions(m.getValue());
                            break;
                        }
                        case "destination":{
                            endhost.setDestination(m.getValue());
                            break;
                        }
                        }
                    }

                }catch(JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                vlogger.logger.info("Endhost " +node.getName()+" empty");

            }
            configuration.setEndhost(endhost);
            break;
        }
        case "ENDPOINT":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Endpoint endpoint=new Endpoint();
            configuration.setEndpoint(endpoint);
            break;
        }
        /*case "FIELDMODIFIER":{

            configuration.setName(node.getFunctional_type());
            PacketType fieldmodifier=new  PacketType();
            configuration.setFieldmodifier(fieldmodifier);
            break;
        }
		*/
        case "MAILCLIENT":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Mailclient mailclient=new Mailclient();

            ObjectMapper mapper=new ObjectMapper();
            java.util.Map<String, String> map=new LinkedHashMap();
            String input;
            Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(nodes.toString());
            if(matcher.find()){
                input=matcher.group(1);
            }else
                input=nodes.toString();

            Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
            List<String> list = new ArrayList<String>();
            Matcher match= pattern.matcher(input);
            while (match.find()) {
                list.add(match.group());
            }

            try{
                for(String string : list){
                    map.putAll(mapper.readValue(string, LinkedHashMap.class));
                    if(map!=null || !map.isEmpty()){
                        for(java.util.Map.Entry<String, String> m : map.entrySet()){
                            switch(m.getKey()){
                            case "mailserver":{
                                mailclient.setMailserver(m.getValue());
                                break;
                            }
                            default:
                                vlogger.logger.info("\"mailserver\" object is required");
                                break;
                            }
                        }
                    }
                }
            }catch(JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setMailclient(mailclient);
            break;
        }
        case "MAILSERVER":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Mailserver mailserver=new Mailserver();
            configuration.setMailserver(mailserver);
            break;
        }
        case "NAT":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Nat nat=new Nat();
            List<AddressType> source=new ArrayList<AddressType>();
            //List<String> source=new ArrayList<String>();

            if(!nodes.toString().equals(empty)){
                ObjectMapper mapper=new ObjectMapper();
                List<String> list=new ArrayList<String>();
                try {

                    list = mapper.readValue(nodes.toString(), ArrayList.class);

                    for(String s : list){
                    	AddressType address = new AddressType();
                    	address.setName(s);
                        source.add(address);
                    }
                } catch (JsonGenerationException e) {
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                vlogger.logger.info("Nat " +node.getName()+" empty");
            }
            nat.getSource().addAll(source);
            configuration.setNat(nat);
            break;
        }
        case "VPNACCESS":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Vpnaccess vpnaccess=new Vpnaccess();
            ObjectMapper mapper=new ObjectMapper();
            java.util.Map<String, String> map=new LinkedHashMap();
            String input;
            Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(nodes.toString());
            if(matcher.find()){
                input=matcher.group(1);
            }else
                input=nodes.toString();

            Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
            List<String> list = new ArrayList<String>();
            Matcher match= pattern.matcher(input);
            while (match.find()) {
                list.add(match.group());
            }
            try{
                for(String string : list){
                    map.putAll(mapper.readValue(string, LinkedHashMap.class));
                    if(map!=null || !map.isEmpty()){
                        for(java.util.Map.Entry<String, String> m : map.entrySet()){
                            switch(m.getKey()){
                            case "vpnexit":{
                                vpnaccess.setVpnexit(m.getValue());
                                break;
                            }
                            default:
                                vlogger.logger.info("\"vpnexit\" is required");
                                break;
                            }
                        }
                    }
                }
            }catch(JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setVpnaccess(vpnaccess);
            break;
        }
        case "VPNEXIT":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Vpnexit vpnexit=new Vpnexit();

            ObjectMapper mapper=new ObjectMapper();
            java.util.Map<String, String> map=new LinkedHashMap();
            String input;
            Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(nodes.toString());
            if(matcher.find()){
                input=matcher.group(1);
            }else
                input=nodes.toString();

            Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
            List<String> list = new ArrayList<String>();
            Matcher match= pattern.matcher(input);
            while (match.find()) {
                list.add(match.group());
            }

            try{
                for(String string : list){
                    /* there is only 1 string */
                    map.putAll(mapper.readValue(string, LinkedHashMap.class));
                    if(map!=null || !map.isEmpty()){
                        for(java.util.Map.Entry<String, String> m : map.entrySet()){
                            switch(m.getKey()){
                            case "vpnaccess":{
                                vpnexit.setVpnaccess(m.getValue());
                                break;
                            }
                            default:{
                                vlogger.logger.info("\"vpnaccess\" is required");
                                break;
                            }
                            }
                        }
                    }
                }
            }catch(JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setVpnexit(vpnexit);
            break;
        }
        case "WEBCLIENT":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Webclient webclient=new Webclient();
            ObjectMapper mapper=new ObjectMapper();
            java.util.Map<String, String> map=new LinkedHashMap();
            String input;
            Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(nodes.toString());
            if(matcher.find()){
                input=matcher.group(1);
            }else
                input=nodes.toString();

            Pattern pattern=Pattern.compile("\\{([^\\}]*)\\}");
            List<String> list = new ArrayList<String>();
            Matcher match= pattern.matcher(input);
            while (match.find()) {
                list.add(match.group());
            }
            try{
                for(String string : list){
                    map.putAll(mapper.readValue(string, LinkedHashMap.class));
                    if(map!=null || !map.isEmpty()){
                        for(java.util.Map.Entry<String, String> m : map.entrySet()){
                            switch(m.getKey()){
                            case "webserver":{
                                webclient.setNameWebServer(m.getValue());
                                break;
                            }
                            default:{
                                vlogger.logger.info("\"webserver\" object is required");
                                break;
                            }
                            }
                        }
                    }
                }
            }catch(JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            configuration.setWebclient(webclient);
            break;
        }
        case "WEBSERVER":{
            configuration.setName(node.getFunctional_type().toLowerCase());
            Webserver webserver=new Webserver();
            configuration.setWebserver(webserver);
            break;
        }
        }
    }

    public static Neighbour NeighbourToNeo4j(it.polito.verigraph.model.Neighbour n){
        Neighbour neighbourRoot;
        neighbourRoot=(new ObjectFactory()).createNeighbour();
        neighbourRoot.setId(n.getId());
        neighbourRoot.setName(n.getName());
        return neighbourRoot;
    }

    public static Node NodeToNeo4j(it.polito.verigraph.model.Node n) throws JsonParseException, JsonMappingException, IOException{
        Node nodeRoot;
        it.polito.neo4j.jaxb.Configuration configuration=(new ObjectFactory()).createConfiguration();
        nodeRoot=(new ObjectFactory()).createNode();
        nodeRoot.setId(n.getId());
        nodeRoot.setName(n.getName());
        nodeRoot.setFunctionalType(FunctionalTypes.fromValue(n.getFunctional_type().toUpperCase()));
        for(Map.Entry<Long, it.polito.verigraph.model.Neighbour> neighbour : n.getNeighbours().entrySet()){
            it.polito.verigraph.model.Neighbour neighb=neighbour.getValue();
            nodeRoot.getNeighbour().add(NeighbourToNeo4j(neighb));
        }
        JsonNode json=n.getConfiguration().getConfiguration();
        setConfiguration(configuration, n, json);
        nodeRoot.setConfiguration(configuration);
        return nodeRoot;
    }
    
    public static Policy PolicyToNeo4j(it.polito.verigraph.model.Policy p) throws JsonParseException, JsonMappingException, IOException{
        Policy policyRoot;
        it.polito.neo4j.jaxb.Restrictions restrictions=(new ObjectFactory()).createRestrictions();
        policyRoot=(new ObjectFactory()).createPolicy();
        
        policyRoot.setId(p.getId());
        policyRoot.setName(p.getName());
        policyRoot.setSource(p.getSource());
        policyRoot.setDestination(p.getDestination());
        
        JsonNode trafficFlow = p.getTrafficFlow();
        setTrafficFlow(policyRoot, trafficFlow);

        // setRestrictions
        JsonNode json=p.getRestrictions().getRestrictions();
        setRestrictions(restrictions, p, json);
        policyRoot.setRestrictions(restrictions);
        
        return policyRoot;
    }
    
    @SuppressWarnings("unchecked")
    private static void setRestrictions(it.polito.neo4j.jaxb.Restrictions restrictions, it.polito.verigraph.model.Policy policy, JsonNode nodes) throws JsonParseException, JsonMappingException, IOException {
        
        String empty="[]";
    	
        restrictions.setType(it.polito.neo4j.jaxb.RestrictionTypes.fromValue(policy.getRestrictions().getType().toUpperCase()));
        
        List<Object> functions_list=new ArrayList<Object>();
        if(!nodes.toString().equals(empty)){
        	if (nodes.isArray()) {
                for (JsonNode objNode : nodes) {
                    String type = objNode.get("funcType").textValue();
                    
                    if(type.equals("exact")) {
                    	it.polito.neo4j.jaxb.ExactFunction f = new it.polito.neo4j.jaxb.ExactFunction();
                		f.setName(objNode.get("funcName").textValue());
                        functions_list.add(f);
                    } else if (type.equals("generic")) {
                    	it.polito.neo4j.jaxb.GenericFunction f = new it.polito.neo4j.jaxb.GenericFunction();
                		f.setType(it.polito.neo4j.jaxb.FunctionalTypes.fromValue(objNode.get("funcName").textValue().toUpperCase()));
                        functions_list.add(f);
                        
                    	vlogger.logger.info("Added funcname: \n" + objNode.get("funcName").textValue());

                    }
                }
        	}
        }
        else{
            vlogger.logger.info("Function_list of the policy " + policy.getName() + " empty");
        }
        
        restrictions.getGenericFunctionOrExactFunction().addAll(functions_list);
    }

    @SuppressWarnings("unchecked")
    private static void setTrafficFlow(Policy policy, JsonNode tf) {
        String empty="[]";

        PacketType trafficFlow = new PacketType();

        if(!tf.toString().equals(empty)){
            ObjectMapper mapper=new ObjectMapper();
            java.util.Map<String, String> map=new LinkedHashMap();
            String input;
            Matcher matcher = Pattern.compile("\\[([^\\]]*)\\]").matcher(tf.toString());
            if(matcher.find()){
                input=matcher.group(1);
            }else
                input=tf.toString();

            try{
                map = mapper.readValue(input, java.util.LinkedHashMap.class);
                for(java.util.Map.Entry<String, String> m : map.entrySet()){
                    switch(m.getKey()){
                    case "body":{
                        trafficFlow.setBody(m.getValue());
                        break;
                    }
                    case"sequence":{
                        trafficFlow.setSequence(new BigInteger(m.getValue()));
                        break;
                    }
                    case "protocol":{
                        trafficFlow.setProtocol(ProtocolTypes.fromValue(m.getValue()));
                        break;
                    }
                    case "email_from":{
                        trafficFlow.setEmailFrom(m.getValue());
                        break;
                    }
                    case "url":{
                        trafficFlow.setUrl(m.getValue());
                        break;
                    }
                    case "option":{
                        trafficFlow.setOptions(m.getValue());
                        break;
                    }
                    case "destination":{
                        trafficFlow.setDestination(m.getValue());
                        break;
                    }
                    }
                }

            }catch(JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            vlogger.logger.info("Traffic flow for policy " + policy.getName() + " empty");
        }
        
		policy.setTrafficflow(trafficFlow);
	}
    
    public static it.polito.neo4j.jaxb.Restrictions RestrictionsToNeo4j(it.polito.verigraph.model.Restrictions policyRestrictions, it.polito.verigraph.model.Policy policy) throws JsonParseException, JsonMappingException, IOException {
        it.polito.neo4j.jaxb.Restrictions restrictions = (new ObjectFactory()).createRestrictions();
        JsonNode nodes = policyRestrictions.getRestrictions();
        setRestrictions(restrictions, policy, nodes);

        return restrictions;
    }
}