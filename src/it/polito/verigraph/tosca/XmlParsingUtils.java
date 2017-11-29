/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.tosca.classes.TConfiguration;
import it.polito.verigraph.tosca.classes.TDefinitions;
import it.polito.verigraph.tosca.classes.TEntityTemplate;
import it.polito.verigraph.tosca.classes.TExtensibleElements;
import it.polito.verigraph.tosca.classes.TNodeTemplate;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate;
import it.polito.verigraph.tosca.classes.TServiceTemplate;
import it.polito.verigraph.tosca.classes.TTopologyTemplate;

public class XmlParsingUtils {
	
	/** Returns a List of TServiceTemplate JAXB-generated objects, parsed from a TOSCA-compliant XML. */
    public static List<TServiceTemplate> obtainServiceTemplates(String file) throws JAXBException, IOException, ClassCastException, DataNotFoundException {
    	// Create a JAXBContext capable of handling the generated classes
		JAXBContext jc = JAXBContext.newInstance("it.polito.verigraph.tosca.classes");
        Unmarshaller u = jc.createUnmarshaller();
        
        // Unmarshal a document into a tree of Java content objects
        /*Object jaxbElement = u.unmarshal(new FileInputStream(file));
        JAXBElement<TDefinitions> jaxbRoot = (JAXBElement<TDefinitions>) jaxbElement;
		TDefinitions definitions = (TDefinitions) jaxbRoot.getValue();*/
        Source source = new StreamSource(new FileInputStream(file));
        JAXBElement<TDefinitions> rootElement = u.unmarshal(source, TDefinitions.class);
        TDefinitions definitions = rootElement.getValue();
        
        List<TExtensibleElements> elements = definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
        
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
	
	
	/** Returns the TConfiguration JAXB-generated TOSCA object of a TOSCA NodeTemplate. */
	public static TConfiguration obtainConfiguration(TNodeTemplate nodeTemplate) {
		try {
			Object configObject = nodeTemplate.getProperties().getAny();
			
			//Retrieving element text content (JSON)
			Element propertyNode = (Element) configObject;
			NodeList jsonNodeList = propertyNode.getElementsByTagName("JSON");
			Element jsonNode = (Element) jsonNodeList.item(0);
			
			//Retrieving TConfiguration object without its text content
			JAXBContext context = JAXBContext.newInstance(TConfiguration.class);
			Unmarshaller um = context.createUnmarshaller();			
			JAXBElement<TConfiguration> configNode = um.unmarshal((Node)configObject, TConfiguration.class);
			TConfiguration configuration = configNode.getValue();
			
			configuration.setJSON(jsonNode.getTextContent());
			return configuration;
			
		} catch (JAXBException ex) {
        	System.err.println("[obtainConfiguration] error: empty Configuration returned");
    		TConfiguration emptyConf = new TConfiguration();
    		emptyConf.setConfDescr("");
    		emptyConf.setConfID("");
    		emptyConf.setJSON("");
    		return emptyConf;    		
		}
	}
}
