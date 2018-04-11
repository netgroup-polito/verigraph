/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/

package it.polito.verigraph.tosca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TDocumentation;
import it.polito.tosca.jaxb.TServiceTemplate;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Test;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.tosca.converter.xml.GraphToXml;
import it.polito.verigraph.tosca.converter.yaml.GraphToYaml;
import it.polito.verigraph.tosca.deserializer.XmlConfigurationDeserializer;
import it.polito.verigraph.tosca.serializer.XmlConfigSerializer;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.VerificationYaml;

public class MappingUtils {

    public static String prettyPrintJsonString(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object json = mapper.readValue(jsonNode.toString(), Object.class);
            return System.getProperty("line.separator") + mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(json) + System.getProperty("line.separator");
        } catch (Exception e) {
            return "Sorry, pretty print didn't work";
        }
    }

    // From a list of nodes (path) returns a Definitions object that contains all the paths as different service templates
    public static Definitions mapPathsToXml(List<List<Node>> paths) {
        Definitions definitions = new Definitions();
        List<Graph> tempGraphs = new ArrayList<Graph>();

        int i = 0;
        for (List<Node> path: paths) {
            Graph tempGraph = new Graph();
            tempGraph.setId(i++);
            for (Node node : path)
                tempGraph.getNodes().put(node.getId(), node);
            tempGraphs.add(tempGraph);
        }

        for (Graph g: tempGraphs) {
            definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(GraphToXml.mapPathToXml(g));
        }

        return definitions;
    }


    // From a list of nodes (path) returns a list of ServiceTemplateYaml object that represent the paths
    public static List<ServiceTemplateYaml> mapPathsToYaml(List<List<Node>> paths) {
        List<ServiceTemplateYaml> serviceTemplates = new ArrayList<ServiceTemplateYaml>();
        List<Graph> tempGraphs = new ArrayList<Graph>();

        int i = 0;
        for (List<Node> path: paths) {
            Graph tempGraph = new Graph();
            tempGraph.setId(i++);
            for (Node node : path)
                tempGraph.getNodes().put(node.getId(), node);
            tempGraphs.add(tempGraph);
        }

        for (Graph g: tempGraphs) {
            serviceTemplates.add(GraphToYaml.mapGraphYaml(g));
        }

        return serviceTemplates;
    }


    public static Definitions mapVerificationToXml(Verification verification) {
        Definitions toscaVerification = new Definitions();
        TDocumentation toscaVerificationResult = new TDocumentation();
        toscaVerificationResult.setSource(verification.getResult() + ": " + verification.getComment());
        toscaVerification.getDocumentation().add(toscaVerificationResult);

        List<TServiceTemplate> toscaPaths = new ArrayList<TServiceTemplate>();

        int i = 0;
        for (Test test: verification.getTests()) {
            Graph tempGraph = new Graph();
            tempGraph.setId(i++);
            for (Node node : test.getPath())
                tempGraph.getNodes().put(node.getId(), node);

            TServiceTemplate toscaPath = GraphToXml.mapPathToXml(tempGraph);
            TDocumentation toscaTestResult = new TDocumentation();
            toscaTestResult.setSource(test.getResult());
            toscaPath.getDocumentation().add(toscaTestResult);
            toscaPaths.add(toscaPath);
        }

        toscaVerification.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().addAll(0, toscaPaths);
        return toscaVerification;
    }


    public static VerificationYaml mapVerificationToYaml(Verification verification) {
        VerificationYaml verificationYaml = new VerificationYaml();
        verificationYaml.setResult(verification.getResult());
        verificationYaml.setComment(verification.getComment());

        List<ServiceTemplateYaml> toscaPaths = new ArrayList<ServiceTemplateYaml>();

        int i = 0;
        for (Test test: verification.getTests()) {
            Graph tempGraph = new Graph();
            tempGraph.setId(i++);
            for (Node node : test.getPath())
                tempGraph.getNodes().put(node.getId(), node);

            ServiceTemplateYaml toscaPath = GraphToYaml.mapGraphYaml(tempGraph);
            toscaPath.getMetadata().put("result", test.getResult());
            toscaPaths.add(toscaPath);
        }

        verificationYaml.setPaths(toscaPaths);
        return verificationYaml;
    }


    /** Return a string that represent the Tosca Configuration in json string.
     *
     * The string can be converted in JsonNode to be inserted in Model Configuration.
     *
     * Used for: xml-->model
     * @throws JsonProcessingException*/
    public static String obtainStringConfiguration(it.polito.tosca.jaxb.Configuration nodeConfig) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(it.polito.tosca.jaxb.Configuration.class, new XmlConfigSerializer());
        mapper.registerModule(module);

        String stringConfiguration = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nodeConfig);
        if (stringConfiguration.equals("") || stringConfiguration == null)
            return "[]";
        else
            return stringConfiguration;
    }


    /** Return a Tosca Configuration with inside the representation of a model Configuration (only its JsonNode)
     *
     * Used for: model-->xml
     * @throws JsonProcessingException */
    public static it.polito.tosca.jaxb.Configuration obtainToscaConfiguration(it.polito.verigraph.model.Configuration modelConfig, String type) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        //Passing the configuration type to the Deserializer context
        module.addDeserializer(it.polito.tosca.jaxb.Configuration.class, new XmlConfigurationDeserializer());
        mapper.registerModule(module);

        it.polito.tosca.jaxb.Configuration toscaConfig = new it.polito.tosca.jaxb.Configuration();
        try {
            toscaConfig = mapper.reader(new InjectableValues.Std().addValue("type", type))
                    .forType(it.polito.tosca.jaxb.Configuration.class)
                    .readValue(modelConfig.getConfiguration());
        } catch (IOException e) {
            //TODO shall we suppose that configuration stored on DB are always correct?
        }

        return toscaConfig;
    }

    /** Return a Tosca Configuration from a ConfigurationGrpc
     *
     * Used for: grpc-->xml
     * @throws JsonProcessingException */
    public static it.polito.tosca.jaxb.Configuration obtainToscaConfiguration(ToscaConfigurationGrpc grpcConfig, String type) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        //Passing the configuration type to the Deserializer context
        module.addDeserializer(it.polito.tosca.jaxb.Configuration.class, new XmlConfigurationDeserializer());
        mapper.registerModule(module);

        it.polito.tosca.jaxb.Configuration toscaConfig = new it.polito.tosca.jaxb.Configuration();
        try {
            toscaConfig = mapper.reader(new InjectableValues.Std().addValue("type", type))
                    .forType(it.polito.tosca.jaxb.Configuration.class)
                    .readValue(grpcConfig.getConfiguration());
        } catch (IOException e) {
            //TODO shall we suppose that configuration stored on DB are always correct?
        }

        return toscaConfig;
    }

}
