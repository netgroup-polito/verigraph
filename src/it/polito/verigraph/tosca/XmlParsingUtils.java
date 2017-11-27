package it.polito.verigraph.tosca;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import it.polito.verigraph.grpc.tosca.ConfigurationGrpc;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;
import it.polito.verigraph.tosca.classes.TConfiguration;
import it.polito.verigraph.tosca.classes.TDefinitions;
import it.polito.verigraph.tosca.classes.TEntityTemplate;
import it.polito.verigraph.tosca.classes.TExtensibleElements;
import it.polito.verigraph.tosca.classes.TNodeTemplate;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate;
import it.polito.verigraph.tosca.classes.TServiceTemplate;
import it.polito.verigraph.tosca.classes.TTopologyTemplate;

public class XmlParsingUtils {
	
	/** Method for parsing a Tosca xml file into Tosca objects.
     *  Additional functionalities for yaml support must be defined*/
    public static TServiceTemplate obtainServiceTemplate(String file) {
        TServiceTemplate parsed = null;
        
        try {
            // Create a JAXBContext capable of handling the generated classes
            JAXBContext jc = JAXBContext.newInstance( "it.polito.verigraph.tosca.classes" );
            
            // Create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            
            // Unmarshal a document into a tree of Java content objects
			Object jaxbElement = u.unmarshal(new FileInputStream(file));
            JAXBElement<TDefinitions> jaxbRoot = (JAXBElement<TDefinitions>) jaxbElement;
			TDefinitions root = (TDefinitions) jaxbRoot.getValue();
            
            List<TExtensibleElements> elements = root.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();
            Object toRet = null;
            
			for (int i = 0; i < elements.size(); i++) {
				toRet = elements.get(i);
				if (toRet instanceof TServiceTemplate)
					break;
				toRet = null;
			}

			if (toRet == null) {
				// Exception to throw
				System.out.println("[toscaClient] No Service Template in the provided file...");
			} else {
				parsed = (TServiceTemplate) toRet;
			}

		} catch (JAXBException je) {
			System.err.println("[toscaClient] Error while unmarshalling...");
			je.printStackTrace();
			System.exit(1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		} catch (ClassCastException cce) {
			System.out.println("[toscaClient] Wrong data type found in XML document");
			cce.printStackTrace();
			System.exit(1);
		}
		return parsed;
	}
    
	/** Method for parsing a TOSCA Node into a gRPC Node */
	public static List<TNodeTemplate> obtainNodeTemplates(TServiceTemplate serviceTemplate) {
	    	TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
	    	
	    	// Retrieving a list of TNodeTemplate and TRelationshipTemplate JAXB objects
	    	List<TEntityTemplate> entities = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
	    	
	    	// Retrieving a List containing only TNodeTemplates objects
	    	List<TNodeTemplate> nodeTemplates = entities.stream()
	    			.filter(p -> p instanceof TNodeTemplate)
	    			.map(obj -> (TNodeTemplate) obj).collect(Collectors.toList());
	    	return nodeTemplates;
	}

	/** Returns a List of TRelationshipTemplate JAXB-generated TOSCA objects */
	public static List<TRelationshipTemplate> obtainRelationshipTemplates(TServiceTemplate serviceTemplate) {
	    	TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
	    	
	    	// Retrieving a List of TNodeTemplate and TRelationshipTemplate JAXB objects
	    	List<TEntityTemplate> entities = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
	    	
	    	// Retrieving a List containing only TRelationshipTemplate objects
	    	List<TRelationshipTemplate> relationshipTemplates = entities.stream()
	    			.filter(p -> p instanceof TRelationshipTemplate)
	    			.map(obj -> (TRelationshipTemplate) obj).collect(Collectors.toList());
	    	return relationshipTemplates;
	}
	
	
	public static TConfiguration obtainConfiguration(TNodeTemplate nodeTemplate) {
		return null;
	}
}
