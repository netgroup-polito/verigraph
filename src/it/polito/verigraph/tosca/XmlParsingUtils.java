/*******************************************************************************
 * Copyright (c) 2017/18 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import com.sun.research.ws.wadl.ObjectFactory;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.tosca.jaxb.Configuration;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TDefinitions;
import it.polito.tosca.jaxb.TEntityTemplate;
import it.polito.tosca.jaxb.TExtensibleElements;
import it.polito.tosca.jaxb.TNodeTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate;
import it.polito.tosca.jaxb.TServiceTemplate;
import it.polito.tosca.jaxb.TTopologyTemplate;
import it.polito.verigraph.tosca.converter.grpc.ToscaGrpcUtils;


public class XmlParsingUtils {

    /** Returns a List of TServiceTemplate JAXB-generated objects, parsed from a TOSCA-compliant XML. */
    public static Definitions obtainDefinitions(String file) throws JAXBException, IOException, ClassCastException, DataNotFoundException {
        // Create a JAXBContext capable of handling the generated classes
        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
        Unmarshaller u = jc.createUnmarshaller();

        //Retrieve the TDefinitions object
        Source source = new StreamSource(new FileInputStream(file));

        JAXBElement<Definitions> rootElement = (JAXBElement<Definitions>)u.unmarshal(source, Definitions.class);
        Definitions definitions = rootElement.getValue();
        return definitions;
    }

    /** Returns a List of TServiceTemplate JAXB-generated objects, parsed from a TOSCA-compliant XML. */
    public static List<TServiceTemplate> obtainServiceTemplates(String file) throws JAXBException, IOException, ClassCastException, DataNotFoundException {
        // Create a JAXBContext capable of handling the generated classes
        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class, TDefinitions.class, Configuration.class);
        Unmarshaller u = jc.createUnmarshaller();

        //Retrieve the TDefinitions object
        Source source = new StreamSource(new FileInputStream(file));

        JAXBElement<TDefinitions> rootElement = (JAXBElement<TDefinitions>)u.unmarshal(source, TDefinitions.class);
        TDefinitions definitions = rootElement.getValue();
        List<TExtensibleElements> elements = definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();

        //Retrieve the list of ServiceTemplate in Definitions
        List<TServiceTemplate> serviceTemplates = elements.stream()
                .filter(p -> p instanceof TServiceTemplate)
                .map(obj -> (TServiceTemplate) obj).collect(Collectors.toList());

        if (serviceTemplates.isEmpty())
            throw new DataNotFoundException("There is no ServiceTemplate into the TOSCA XML file");
        return serviceTemplates; // Could be an empty list if there are no TServiceTemplate objects
    }


    /** Returns a List of TNodeTemplate JAXB-generated TOSCA objects. */
    public static List<TNodeTemplate> obtainNodeTemplates(TServiceTemplate serviceTemplate) throws DataNotFoundException {
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        // Retrieving a list of TNodeTemplate and TRelationshipTemplate JAXB objects
        List<TEntityTemplate> entities = topologyTemplate.getNodeTemplateOrRelationshipTemplate();

        // Retrieving a List containing only TNodeTemplates objects
        List<TNodeTemplate> nodeTemplates = entities.stream()
                .filter(p -> p instanceof TNodeTemplate)
                .map(obj -> (TNodeTemplate) obj).collect(Collectors.toList());

        if (nodeTemplates.isEmpty())
            throw new DataNotFoundException("There is no NodeTemplate into ServiceTemplate " + serviceTemplate.toString() + " and TopologyTemplate " + topologyTemplate.toString());
        return nodeTemplates; // Could be an empty list if there are no TNodeTemplate objects
    }


    /** Returns a List of TRelationshipTemplate JAXB-generated TOSCA objects. */
    public static List<TRelationshipTemplate> obtainRelationshipTemplates(TServiceTemplate serviceTemplate) throws DataNotFoundException {
        TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();

        // Retrieving a List of TNodeTemplate and TRelationshipTemplate JAXB objects
        List<TEntityTemplate> entities = topologyTemplate.getNodeTemplateOrRelationshipTemplate();

        // Retrieving a List containing only TRelationshipTemplate objects
        List<TRelationshipTemplate> relationshipTemplates = entities.stream()
                .filter(p -> p instanceof TRelationshipTemplate)
                .map(obj -> (TRelationshipTemplate) obj).collect(Collectors.toList());

        if (relationshipTemplates.isEmpty())
            throw new DataNotFoundException("There is no RelationshipTemplate into ServiceTemplate " + serviceTemplate.toString() + " and TopologyTemplate " + topologyTemplate.toString());
        return relationshipTemplates; // Could be an empty list if there are no TRelationshipTemplate objects
    }


    /** Returns the it.polito.tosca.jaxb.Configuration JAXB-generated TOSCA object of a TOSCA NodeTemplate. */
    public static Configuration obtainConfiguration(TNodeTemplate nodeTemplate) {
        try {
            Configuration configuration = (Configuration)nodeTemplate.getProperties().getAny();

            //This could be eventually used to cross check node type and configuration type
            //String typename = nodeTemplate.getType().getLocalPart().toLowerCase();
            return configuration;


        } catch (NullPointerException | ClassCastException ex) {
            //To be eventually defined a mechanism to distinguish hostnode from forwarder
            System.out.println("[Warning] Node " + nodeTemplate.getId().toString()
                    + ": missing or invalid configuration, the node will be configured as a forwarder!" );
            Configuration defConf = new Configuration();
            defConf.setConfDescr(ToscaGrpcUtils.defaultDescr);
            defConf.setConfID(ToscaGrpcUtils.defaultConfID);

            Configuration.FieldmodifierConfiguration defaultForward = new Configuration.FieldmodifierConfiguration();
            defaultForward.setName("DefaultForwarder");

            defConf.setFieldmodifierConfiguration(defaultForward);
            return defConf;
        }
    }

}
