package it.polito.verigraph.tosca.converter.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.ToscaConfigurationGrpc;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.XmlParsingUtils;
import it.polito.verigraph.tosca.classes.TNodeTemplate;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate;
import it.polito.verigraph.tosca.classes.TServiceTemplate;

public class XmlToGrpc {

	/** Returns the (first) TopologyTemplate found in the TOSCA-compliant XML file */
	public static TopologyTemplateGrpc obtainTopologyTemplateGrpc (String filepath) throws IOException, JAXBException, DataNotFoundException, ClassCastException, BadRequestException{
		List<TServiceTemplate> serviceTList = XmlParsingUtils.obtainServiceTemplates(filepath);
		TServiceTemplate serviceTemplate = serviceTList.get(0); //obtain only the first ServiceTemplate of the TOSCA compliance file

		//Retrieving of list of NodeTemplate and RelationshipTemplate
		List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
		List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();	    
		for(TNodeTemplate nt : XmlParsingUtils.obtainNodeTemplates(serviceTemplate)) {
			for(NodeTemplateGrpc alreadyAddedNode : nodes)
				if(alreadyAddedNode.getId().equals(nt.getId()))
					throw new BadRequestException("The NodeTemplate ID must be unique.");
			nodes.add(parseNodeTemplate(nt));
		}
		for(TRelationshipTemplate rt : XmlParsingUtils.obtainRelationshipTemplates(serviceTemplate))
			relats.add(parseRelationshipTemplate(rt));

		//Creating TopologyTemplateGrpc object to be sent to server
		return TopologyTemplateGrpc.newBuilder()
				.setId("0") //useless value since the server chooses the actual value for the GraphID
				.addAllNodeTemplate(nodes)
				.addAllRelationshipTemplate(relats)
				.build();
	}


	/** Parsing method: TNodeTemplate(tosca) --> NodeTemplateGrpc */
	private static NodeTemplateGrpc parseNodeTemplate(TNodeTemplate nodeTempl) throws ClassCastException, NullPointerException {   	
		Boolean isVerigraphCompl = true;
		Type type;

		//NodeTemplateGrpc building
		NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder();

		//ID cannot be null
		try {
			nodegrpc.setId(nodeTempl.getId());
		} catch (NullPointerException ex) {
			throw new NullPointerException("An ID must be specified for each Node");
		}	
		//Name can be null
		try {
			nodegrpc.setName(nodeTempl.getName());
		} catch (NullPointerException ex) {
			nodegrpc.setName("");
		}

		//Type cannot be null but it can be invalid
		try {
			String typestring = nodeTempl.getType().getLocalPart().toLowerCase();
			type = Type.valueOf(nodeTempl.getType().getLocalPart().toLowerCase().substring(0,typestring.length()-4));
		} catch (IllegalArgumentException | NullPointerException ex) {
			//in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be an endhost node
			type = Type.endhost;
			isVerigraphCompl = false;
		}
		nodegrpc.setType(type);
		ToscaConfigurationGrpc.Builder grpcConfig;   	
		if(isVerigraphCompl) {
			it.polito.verigraph.tosca.classes.Configuration nodeConfig = XmlParsingUtils.obtainConfiguration(nodeTempl);
			grpcConfig = ToscaConfigurationGrpc.newBuilder();
			//These fields are optional in TOSCA xml
			try {
				grpcConfig.setId(nodeConfig.getConfID());        		
			} catch(NullPointerException ex) {
				grpcConfig.setId(ToscaGrpcUtils.defaultConfID);
			}
			try {
				grpcConfig.setDescription(nodeConfig.getConfDescr());
			} catch(NullPointerException ex) {
				grpcConfig.setDescription(ToscaGrpcUtils.defaultDescr);
			}
			try {;
			grpcConfig.setConfiguration(MappingUtils.obtainStringConfiguration(nodeConfig)); 
			} catch(NullPointerException | JsonProcessingException ex) {
				grpcConfig.setConfiguration(ToscaGrpcUtils.defaultConfig);
			} 
		}
		else {
			grpcConfig = ToscaConfigurationGrpc.newBuilder()
					.setId(ToscaGrpcUtils.defaultConfID)
					.setDescription(ToscaGrpcUtils.defaultDescr)
					.setConfiguration(ToscaGrpcUtils.defaultConfig);
		}   			
		nodegrpc.setConfiguration(grpcConfig.build());
		return nodegrpc.build();   
	}  


	/** Parsing method: TRelationshipTemplate(tosca) --> RelationshipTemplateGrpc */
	private static RelationshipTemplateGrpc parseRelationshipTemplate(TRelationshipTemplate relatTempl) throws ClassCastException{   	
		String source, target;
		//RelationshipTemplateGrpc building
		RelationshipTemplateGrpc.Builder relatgrpc = RelationshipTemplateGrpc.newBuilder();  	

		//ID and Name can be null
		try {
			relatgrpc.setId(relatTempl.getId());
		} catch (NullPointerException ex) {}    	
		try {
			relatgrpc.setName(relatTempl.getName());
		} catch (NullPointerException ex) {}

		//Source and Target values cannot be null
		try {
			TNodeTemplate sourceNode = (TNodeTemplate) relatTempl.getSourceElement().getRef();
			TNodeTemplate targetNode = (TNodeTemplate) relatTempl.getTargetElement().getRef();
			source = sourceNode.getId();
			target = targetNode.getId();
		} catch (NullPointerException ex) {
			throw new NullPointerException("Invalid NodeTemplate reference in RelationshipTemplate with id:" + relatTempl.getId());
		}    	
		relatgrpc.setIdSourceNodeTemplate(source)
		.setIdTargetNodeTemplate(target);    	
		return relatgrpc.build();
	}
}
