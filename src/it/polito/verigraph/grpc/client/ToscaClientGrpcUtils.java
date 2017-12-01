package it.polito.verigraph.grpc.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import it.polito.verigraph.tosca.XmlParsingUtils;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.*;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;

public class ToscaClientGrpcUtils {
	
	/** Default configuration for a Tosca NodeTemplate non compliant with Verigraph types*/
	public static final String defaultConfID = new String("0");
	public static final String defaultDescr = new String("Default Configuration");
	public static final String defaultConfig = new String("{ }");
	
	/** Helper for Client interface*/
	public static final String helper = new String("");

	/** Returns the (first) TopologyTemplate found in the TOSCA-compliant XML file */
	public static TopologyTemplateGrpc obtainTopologyTemplateGrpc (String filepath) throws IOException, JAXBException, DataNotFoundException, ClassCastException{
		List<TServiceTemplate> serviceTList = XmlParsingUtils.obtainServiceTemplates(filepath);
		TServiceTemplate serviceTemplate = serviceTList.get(0); //obtain only the first ServiceTemplate of the TOSCA compliance file

		//Retrieving of list of NodeTemplate and RelationshipTemplate
	    List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
	    List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();	    
	    for(TNodeTemplate nt : XmlParsingUtils.obtainNodeTemplates(serviceTemplate) )
	    	nodes.add(ToscaClientGrpcUtils.parseNodeTemplate(nt));
	    for(TRelationshipTemplate rt : XmlParsingUtils.obtainRelationshipTemplates(serviceTemplate) )
	    	relats.add(ToscaClientGrpcUtils.parseRelationshipTemplate(rt));
	    
	    //Creating TopologyTemplateGrpc object to be sent to server
	    return TopologyTemplateGrpc.newBuilder()
	    		.setId(0) //useless value since the server chooses the actual value for the GraphID
	    		.addAllNodeTemplate(nodes)
	    		.addAllRelationshipTemplate(relats)
	    		.build();
	}
	
	
    /** Parsing method: TNodeTemplate(tosca) --> NodeTemplateGrpc */
    public static NodeTemplateGrpc parseNodeTemplate(TNodeTemplate nodeTempl) throws ClassCastException, NullPointerException {   	
    	Boolean isVerigraphCompl = true;
    	Type type;
    	
    	//NodeTemplateGrpc building
    	NodeTemplateGrpc.Builder nodegrpc = NodeTemplateGrpc.newBuilder()
    			.setId(0); //useless value since the server chooses the actual value for the NodeID
    	try {
    		nodegrpc.setName(nodeTempl.getName());
    	} catch (NullPointerException ex) {
    		throw new NullPointerException("A name must be specified for each Node");
    	}
    	
    	try { 
    		type = Type.valueOf(nodeTempl.getType().getLocalPart().toLowerCase());
    	} catch (IllegalArgumentException ex) {
    		//in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be an endhost node
			type = Type.endhost;
			isVerigraphCompl = false;
    	}
    	nodegrpc.setType(type);
    	ToscaConfigurationGrpc.Builder grpcConfig;   	
    	if(isVerigraphCompl) {
    		TConfiguration nodeConfig = XmlParsingUtils.obtainConfiguration(nodeTempl);
        	grpcConfig = ToscaConfigurationGrpc.newBuilder();
        	//These fields are optional in TOSCA xml
        	try {
        		grpcConfig.setId(nodeConfig.getConfID());        		
        	} catch(NullPointerException ex) {}
       		try {
       			grpcConfig.setDescription(nodeConfig.getConfDescr());
       		} catch(NullPointerException ex) {}
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
    	RelationshipTemplateGrpc.Builder relatgrpc = RelationshipTemplateGrpc.newBuilder()  	
    			.setId(0); //useless value since the server chooses the actual value for the NeighbourID
    	try {
    		relatgrpc.setName(relatTempl.getName());
    	} catch (NullPointerException ex) {
    		//throw new NullPointerException("A name must be specified for each Relationship");
    		//No problem if name is not specified for the Relationship since the server will change it for its management
    	}
    	try {
    		source = ((QName)relatTempl.getSourceElement().getRef()).getLocalPart();
    		target = ((QName)relatTempl.getTargetElement().getRef()).getLocalPart();
    	} catch (NullPointerException ex) {
    		throw new NullPointerException("A valid Node name must be specified as SourceElement and Target Element for each Relationship");
    	}    	
    	relatgrpc.setIdSourceNodeTemplate(Long.valueOf(source).longValue())
    		.setIdTargetNodeTemplate(Long.valueOf(target).longValue());
    	
    	return relatgrpc.build();
    }
    
    
    /** Create a TopologyTemplateGrpc object */
    public static TopologyTemplateGrpc createTopologyTemplateGrpc(long id, List<NodeTemplateGrpc> nodeList, List<RelationshipTemplateGrpc> relatList) throws Exception {
    	TopologyTemplateGrpc.Builder topolBuild = TopologyTemplateGrpc.newBuilder().setId(id);
    	
    	if(nodeList.isEmpty())
    		throw new Exception("NodeTemplate list cannot be empty");
    	else
    		topolBuild.addAllNodeTemplate(nodeList);
    	
    	if(relatList.isEmpty())
    		throw new Exception("RelatioshipTemplate list cannot be empty");
    	else
    		topolBuild.addAllRelationshipTemplate(relatList);
    	
    	return topolBuild.build();
    }
    
    
    /** Create a NodeTemplateGrpc object */
    public static NodeTemplateGrpc createNodeTemplateGrpc (String name, long id, String type, ToscaConfigurationGrpc config) throws Exception {
    	NodeTemplateGrpc.Builder nodeBuilder = NodeTemplateGrpc.newBuilder().setId(id); 
    
    	Type nodeType;
    	if(name != null)
             nodeBuilder.setName(name);
    	else
             throw new Exception("NodeTemplate must have a name");

        if(type == null)
        	 throw new Exception("NodeTemplate must have a type");
        try { 
     		nodeType = Type.valueOf(type.toLowerCase());
     	} catch (IllegalArgumentException ex) {
     		//in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be an endhost node
 			nodeType = Type.endhost;
 		}
     	
        if(config != null)
        	nodeBuilder.setConfiguration(config);
        else
        	throw new Exception("NodeTemplate must have a configuration");
             
        return nodeBuilder.build();
    }
    
    
    /** Create a RelationshipTemplateGrpc object */
    public static RelationshipTemplateGrpc createRelationshipTemplateGrpc (String name, long id, long source, long dest) throws Exception{
    	RelationshipTemplateGrpc.Builder relatBuilder = RelationshipTemplateGrpc.newBuilder()
    			.setId(id)
    			.setIdSourceNodeTemplate(source)
    			.setIdTargetNodeTemplate(dest); 
    	
    	if(name != null)
            relatBuilder.setName(name);
        
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
        	throw new Exception("ToscaConfigurationGrpc must have a configuration");
        
        return confBuilder.build();
    }
    
    
    /** Create a ToscaPolicy */
    public static ToscaPolicy createToscaPolicy(String src, String dst, String type, String middlebox, long idTopologyTemplate) throws IllegalArgumentException{
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
}
