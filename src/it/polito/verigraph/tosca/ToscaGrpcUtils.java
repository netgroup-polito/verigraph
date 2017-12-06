package it.polito.verigraph.tosca;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.tosca.XmlParsingUtils;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.*;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Test;
import it.polito.verigraph.model.Verification;

public class ToscaGrpcUtils {

	/** CLIENT UTILITY METHODS */

	/** Default configuration for a Tosca NodeTemplate non compliant with Verigraph types*/
	public static final String defaultConfID = new String("");
	public static final String defaultDescr = new String("Default Configuration");
	public static final String defaultConfig = new String("");

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
	public static NodeTemplateGrpc parseNodeTemplate(TNodeTemplate nodeTempl) throws ClassCastException, NullPointerException {   	
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
				grpcConfig.setConfiguration(defaultConfID);
			}
			try {
				grpcConfig.setDescription(nodeConfig.getConfDescr());
			} catch(NullPointerException ex) {
				grpcConfig.setConfiguration(defaultDescr);
			}
			try {
				grpcConfig.setConfiguration(nodeConfig.getJSON());
			} catch(NullPointerException ex) {
				grpcConfig.setConfiguration(defaultConfig);
			}
		}
		else {
			grpcConfig = ToscaConfigurationGrpc.newBuilder()
					.setId(defaultConfID)
					.setDescription(defaultDescr)
					.setConfiguration(defaultConfig);
		}   			
		nodegrpc.setConfiguration(grpcConfig.build());
		return nodegrpc.build();   
	}  


	/** Parsing method: TRelationshipTemplate(tosca) --> RelationshipTemplateGrpc */
	public static RelationshipTemplateGrpc parseRelationshipTemplate(TRelationshipTemplate relatTempl) throws ClassCastException{   	
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


	/** Create a TopologyTemplateGrpc object */
	public static TopologyTemplateGrpc createTopologyTemplateGrpc(String id, List<NodeTemplateGrpc> nodeList, List<RelationshipTemplateGrpc> relatList) throws IllegalArgumentException {
		TopologyTemplateGrpc.Builder topolBuild = TopologyTemplateGrpc.newBuilder().setId(id);   	
		if(nodeList.isEmpty())
			throw new IllegalArgumentException("NodeTemplate list cannot be empty");
		else
			topolBuild.addAllNodeTemplate(nodeList);

		if(relatList.isEmpty())
			throw new IllegalArgumentException("RelatioshipTemplate list cannot be empty");
		else
			topolBuild.addAllRelationshipTemplate(relatList);  	
		return topolBuild.build();
	}


	/** Create a NodeTemplateGrpc object */
	public static NodeTemplateGrpc createNodeTemplateGrpc (String name, String id, String type, ToscaConfigurationGrpc config) throws IllegalArgumentException {
		NodeTemplateGrpc.Builder nodeBuilder = NodeTemplateGrpc.newBuilder();   
		Type nodeType;
		if(id != null)
			nodeBuilder.setId(id);
		else
			throw new IllegalArgumentException("NodeTemplate must have an id");
		if(name != null)
			nodeBuilder.setName(name);
		else
			throw new IllegalArgumentException("NodeTemplate must have a name");
		if(type == null)
			throw new IllegalArgumentException("NodeTemplate must have a type");
		try { 
			nodeType = Type.valueOf(type.toLowerCase());
		} catch (IllegalArgumentException ex) {
			//in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be an endhost node
			nodeType = Type.endhost;
		}
		nodeBuilder.setType(nodeType);
		if(config != null)
			nodeBuilder.setConfiguration(config);
		else
			throw new IllegalArgumentException("NodeTemplate must have a configuration");            
		return nodeBuilder.build();
	}


	/** Create a RelationshipTemplateGrpc object */
	public static RelationshipTemplateGrpc createRelationshipTemplateGrpc (String name, String id, String source, String target) throws IllegalArgumentException{
		RelationshipTemplateGrpc.Builder relatBuilder = RelationshipTemplateGrpc.newBuilder();  	
		if(id != null)
			relatBuilder.setId(id);
		else
			throw new IllegalArgumentException("RelationshipTemplate must have an id");
		if(name != null)
			relatBuilder.setName(name);
		if(source != null)
			relatBuilder.setIdSourceNodeTemplate(source);
		else
			throw new IllegalArgumentException("RelationshipTemplate must have a source Node reference");
		if(target != null)
			relatBuilder.setIdTargetNodeTemplate(target);
		else
			throw new IllegalArgumentException("RelationshipTemplate must have a target Node reference");      
		return relatBuilder.build();    
	}


	/** Create a ToscaConfigurationGrpc object */
	public static ToscaConfigurationGrpc createToscaConfigurationGrpc (String id, String descr, String config) throws Exception{
		ToscaConfigurationGrpc.Builder confBuilder = ToscaConfigurationGrpc.newBuilder();	
		if(id != null)
			confBuilder.setId(id);
		if(descr != null)
			confBuilder.setDescription(descr);
		if(config != null)
			confBuilder.setConfiguration(config);
		else
			throw new IllegalArgumentException("ToscaConfigurationGrpc must have a configuration");       
		return confBuilder.build();
	}


	/** Create a ToscaPolicy */
	public static ToscaPolicy createToscaPolicy(String src, String dst, String type, String middlebox, String idTopologyTemplate) throws IllegalArgumentException{
		if(!validMiddlebox(type, middlebox))
			throw new IllegalArgumentException("Not valid middlebox valid with this type");
		ToscaPolicy.Builder policy = ToscaPolicy.newBuilder();
		policy.setIdTopologyTemplate(idTopologyTemplate);
		if(src != null)
			policy.setSource(src);
		else{
			throw new IllegalArgumentException("Please insert a valid source field");
		}
		if(dst != null)
			policy.setDestination(dst);
		else{
			throw new IllegalArgumentException("Please insert a valid destination field");
		}
		if(type != null)
			policy.setType(ToscaPolicy.PolicyType.valueOf(type));
		else{
			throw new IllegalArgumentException("Please insert a valid type field");
		}
		if(middlebox != null)
			policy.setMiddlebox(middlebox);
		else{
			throw new IllegalArgumentException("Please insert a valid middlebox field");
		}
		return policy.build();
	}

	/** Validate a middlebox */
	public static boolean validMiddlebox(String type, String middlebox) {
		if(type == null)
			return false;
		if(type.equals("reachability") && (middlebox == null || middlebox.equals("")))
			return true;
		if(type.equals("isolation") && !(middlebox == null || middlebox.equals("")))
			return true;
		if(type.equals("traversal") && !(middlebox == null || middlebox.equals("")))
			return true;
		return false;
	}

	public static void printTopologyTemplates(List<TopologyTemplateGrpc> topologyList) {
		for(TopologyTemplateGrpc g : topologyList) {
			System.out.println("* TopologyTemplate id: " + g.getId());
			for (NodeTemplateGrpc n: g.getNodeTemplateList())
				System.out.println(" \tNodeTemplate id:" + n.getId() + " name:" + n.getName());
			for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
				System.out.println(" \tRelationshipTemplate id:" + rel.getId() + " name:" + rel.getName());
			System.out.println("** Topology ended");
		}
		System.out.println("\n* All Topology showed");
	}


	/** SERVER UTILITY METHODS */

	/** Mapping method --> from model Graph to grpc TopologyTemplate */
	public static TopologyTemplateGrpc obtainTopologyTemplate(Graph graph) {
		TopologyTemplateGrpc.Builder topol = TopologyTemplateGrpc.newBuilder();
		topol.setId(String.valueOf(graph.getId()));

		//NodeTemplate 
		for(Node node : graph.getNodes().values()) {
			NodeTemplateGrpc nt = obtainNodeTemplate(node);
			topol.addNodeTemplate(nt);
			//RelationshipTemplate
			Map<Long,Neighbour> neighMap = node.getNeighbours();
			for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
				Neighbour neigh = myentry.getValue();
				RelationshipTemplateGrpc relat = obtainRelationshipTemplate(neigh, node);
				topol.addRelationshipTemplate(relat);
			}
		}
		return topol.build();
	}


	/** Mapping method --> from model Node to grpc NodeTemplate */
	public static NodeTemplateGrpc obtainNodeTemplate(Node node){
		NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder();

		nodegrpc.setId(String.valueOf(node.getId()));
		nodegrpc.setName(node.getName());
		nodegrpc.setType(NodeTemplateGrpc.Type.valueOf(node.getFunctional_type().toLowerCase()));

		ToscaConfigurationGrpc config = obtainToscaConfiguration(node.getConfiguration());
		nodegrpc.setConfiguration(config); 

		return nodegrpc.build();
	}


	/** Mapping method --> from model Neighbour to grpc RelationshipTemplate */
	public static RelationshipTemplateGrpc obtainRelationshipTemplate(Neighbour neigh, Node sourceNode) {
		RelationshipTemplateGrpc.Builder relat = RelationshipTemplateGrpc.newBuilder();
		relat.setId(String.valueOf(sourceNode.getId())); //Neighbour does not have a neighbourID! RelationshipTemplate does, so it is set to sourceNodeID
		relat.setIdSourceNodeTemplate(String.valueOf(sourceNode.getId()));
		relat.setIdTargetNodeTemplate(String.valueOf(neigh.getId()));
		relat.setName(sourceNode.getName()+"to"+neigh.getName());
		return relat.build();
	}


	/** Mapping method --> from model Configuration to grpc ToscaConfigurationGrpc */
	public static ToscaConfigurationGrpc obtainToscaConfiguration(Configuration conf) {
		return ToscaConfigurationGrpc.newBuilder()
				.setId(conf.getId())
				.setDescription(conf.getDescription())
				.setConfiguration(conf.getConfiguration().toString())
				.build();
	}

	/** Mapping method --> from model Verification to grpc ToscaVerificationGrpc */
	public static ToscaVerificationGrpc obtainToscaVerification(Verification verify){
		ToscaVerificationGrpc.Builder ver = ToscaVerificationGrpc.newBuilder();
		ver.setComment(verify.getComment());
		ver.setResult(verify.getResult());
		for(Test test:verify.getTests()){
			ToscaTestGrpc.Builder tst = ToscaTestGrpc.newBuilder().setResult(test.getResult());
			for(Node node:test.getPath()){
				NodeTemplateGrpc nodetempl = obtainNodeTemplate(node);
				tst.addNodeTemplate(nodetempl);
			}
			ver.addTest(tst);
		}
		return ver.build();
	}



	/** Mapping method --> from grpc TopologyTemplateGrpc to model Graph */
	public static Graph deriveGraph(TopologyTemplateGrpc request) throws BadRequestException {
		Graph graph = new Graph();
		Map<Long, Node> nodes = new HashMap<>();

		//Create a list of Node without Neighbour
		for(NodeTemplateGrpc nodetempl : request.getNodeTemplateList()){
			Node node = deriveNode(nodetempl);
			if(nodes.containsKey(node.getId())) //It necessary to check uniqueness here otherwise a .put with the same key will overwrite the old node
				throw new BadRequestException("The NodeTemplate ID must be unique."); 
			else 
				nodes.put(node.getId(), node);
		}

		//Add Neighbour to the Node of the list
		List<RelationshipTemplateGrpc> relatList = request.getRelationshipTemplateList();
		nodes = deriveNeighboursNode(nodes, relatList);

		//Add Node and ID to the graph
		graph.setNodes(nodes);
		try {
			graph.setId(Long.valueOf(request.getId()));
		} catch(NumberFormatException ex) {
			throw new BadRequestException("If you want to store your TopologyTemplate on this server, the TopologyTemplate ID must be a number.");
		}
		return graph;
	}


	/** Mapping method --> from grpc NodeTemplate to model Node (with no Neighbour)*/
	public static Node deriveNode(NodeTemplateGrpc nodegrpc) throws BadRequestException {
		Node node = new Node();
		try {
			node.setId(Long.valueOf(nodegrpc.getId()));
		} catch(NumberFormatException ex) {
			throw new BadRequestException("The NodeTemplate ID must be a number.");
		}
		try {
			node.setName(nodegrpc.getName());
			Configuration conf = deriveConfiguration(nodegrpc.getConfiguration());
			node.setConfiguration(conf);
			node.setFunctional_type(nodegrpc.getType().toString());
		} catch(NullPointerException ex) {
			throw new BadRequestException("The NodeTemplate id:"+node.getId()+" has wrong fields representation.");
		}    	
		return node;
	}



	/** Mapping method --> from a list of model Node to a list of model Node with their Neighbour */
	public static Map<Long,Node> deriveNeighboursNode(Map<Long,Node> nodes, List<RelationshipTemplateGrpc> relatList) throws BadRequestException{
		Map<Long,Node> updNodes = nodes; //new list to be filled with updated Node (update = Node + its Neighbour)
		for(RelationshipTemplateGrpc relat : relatList) {   		
			try {
				//Retrieve the target Node name and generate a new Neighbour
				String neighName = updNodes.get(Long.valueOf(relat.getIdTargetNodeTemplate())).getName();
				Neighbour neigh = new Neighbour();
				neigh.setName(neighName);
				neigh.setId(Long.valueOf(relat.getId()));

				//Retrieve the Neighbour map of the source Node and add the Neighbour
				Node source = updNodes.get(Long.valueOf(relat.getIdSourceNodeTemplate()));
				Map<Long,Neighbour> sourceNodeNeighMap = source.getNeighbours();
				if(sourceNodeNeighMap.containsKey(neigh.getId()))
					throw new BadRequestException("The RelationshipTemplate ID must be unique."); 
				else
					sourceNodeNeighMap.put(neigh.getId(), neigh);
				source.setNeighbours(sourceNodeNeighMap);

				//Update the Node list
				updNodes.put(Long.valueOf(relat.getIdSourceNodeTemplate()), source);
			} catch(NullPointerException | NumberFormatException ex) {
				throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
			}    	
		}
		return updNodes;
	}

	/** Mapping method --> from ToscaConfiguration to model Configuration */
	public static Configuration deriveConfiguration(ToscaConfigurationGrpc request) throws BadRequestException {
		Configuration conf = new Configuration();
		JsonNode rootNode = null;
		try {
			conf.setId(request.getId());
			conf.setDescription(request.getDescription());
			ObjectMapper mapper = new ObjectMapper();	        
			if ("".equals(request.getConfiguration()))
				rootNode=mapper.readTree("[]");
			else
				rootNode = mapper.readTree(request.getConfiguration());
		} catch (IOException | NullPointerException e) {
			throw new BadRequestException("NodeTemplate configuration is invalid.");
		}
		conf.setConfiguration(rootNode);
		return conf;
	}

}
