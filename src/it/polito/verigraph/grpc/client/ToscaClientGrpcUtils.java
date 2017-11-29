package it.polito.verigraph.grpc.client;

import java.util.List;

import javax.xml.namespace.QName;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.grpc.NodeGrpc.FunctionalType;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;

public class ToscaClientGrpcUtils {
	
	/** Default configuration for a Tosca NodeTemplate non compliant with Verigraph types*/
	public static final String defaultConfID = new String("0");
	public static final String defaultDescr = new String("Default Configuration");
	public static final String defaultConfig = new String("");
	
	/** Helper for Client interface*/
	public static final String helper = new String("");

	
    /** Parsing method: TNodeTemplate(tosca) --> NodeTemplateGrpc */
    public static NodeTemplateGrpc parseNodeTemplate(TNodeTemplate toscaNode) throws ClassCastException {   	
    	Boolean isVerigraphCompl = true;
    	
    	//NodeTemplateGrpc building
    	NodeTemplateGrpc.Builder parsed = NodeTemplateGrpc.newBuilder()
    			.setId(Long.valueOf(toscaNode.getId()).longValue()) //longValue is used to convert in long
    			.setName(toscaNode.getName());
    	switch(toscaNode.getType().getLocalPart().toLowerCase()) {
    		case "antispam":
    			parsed.setType(Type.antispam);
    			break;
    		case "cache":
    			parsed.setType(Type.cache);
    			break;
    		case "dpi":
    			parsed.setType(Type.dpi);
    			break;
    		case "endhost":
    			parsed.setType(Type.endhost);
    			break;
    		case "endpoint":
    			parsed.setType(Type.endpoint);
    			break;
    		case "fieldmodifier":
    			parsed.setType(Type.fieldmodifier);
    			break;
    		case "firewall":
    			parsed.setType(Type.firewall);
    			break;
    		case "mailclient":
    			parsed.setType(Type.mailclient);
    			break;
    		case "mailserver":
    			parsed.setType(Type.mailserver);
    			break;
    		case "nat":
    			parsed.setType(Type.nat);
    			break;
    		case "vpnaccess":
    			parsed.setType(Type.vpnaccess);
    			break;
    		case "vpnexit":
    			parsed.setType(Type.vpnexit);
    			break;
    		case "webclient":
    			parsed.setType(Type.webclient);
    			break;
    		case "webserver":
    			parsed.setType(Type.webserver);
    			break;
    		default: //in case the NodeTemplate is not TOSCA-Verigraph compliant, we assume it to be an endhost node
    			parsed.setType(Type.endhost);
    			isVerigraphCompl = false;
    			break;
    	}  	
    	ToscaConfigurationGrpc grpcConfig;   	
    	if(isVerigraphCompl) {
    		TConfiguration nodeConfig = ((TConfiguration)toscaNode.getProperties().getAny());
        	grpcConfig = ToscaConfigurationGrpc.newBuilder()
   			 	 .setId(nodeConfig.getConfID())
   				 .setDescription(nodeConfig.getConfDescr())
   			     .setConfiguration(nodeConfig.getJSON()).build();
    	} else {
        	grpcConfig = ToscaConfigurationGrpc.newBuilder()
   			 	 .setId(ToscaClientGrpcUtils.defaultConfID)
   				 .setDescription(ToscaClientGrpcUtils.defaultDescr)
   			     .setConfiguration(ToscaClientGrpcUtils.defaultConfig).build();
    	}	
    	parsed.setConfiguration(grpcConfig);
    	return parsed.build();   
    }  
    
    
    /** Parsing method: TRelationshipTemplate(tosca) --> RelationshipTemplateGrpc */
    public static RelationshipTemplateGrpc parseRelationshipTemplate(TRelationshipTemplate toscaRel) throws ClassCastException{   	
    	
    	//NodeTemplateGrpc building
    	RelationshipTemplateGrpc.Builder parsed = RelationshipTemplateGrpc.newBuilder()  	
    			.setId(Long.valueOf(toscaRel.getId()).longValue())
    			.setName(toscaRel.getName());
    	
    	String source = ((QName)toscaRel.getSourceElement().getRef()).getLocalPart();
    	String target = ((QName)toscaRel.getTargetElement().getRef()).getLocalPart();
    	
    	parsed.setIdSourceNodeTemplate(Long.valueOf(source).longValue())
    		.setIdTargetNodeTemplate(Long.valueOf(target).longValue());
    	
    	return parsed.build();
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
    	 NodeTemplateGrpc.Builder nodeBuilder = NodeTemplateGrpc.newBuilder().setId(id); //if you have called this method than you have inserted a valid long value

         if(name != null)
             nodeBuilder.setName(name);
         else
             throw new Exception("NodeTemplate must have a name");

         if(type!= null) {
        	 Type nodeType = Type.valueOf(type);
             nodeBuilder.setType(nodeType);
         } else
        	 throw new Exception("NodeTemplate must have a valid type");
         
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
    			.setIdTargetNodeTemplate(dest); //if you have called this method than you have inserted a valid long values
    	
    	if(name != null)
            relatBuilder.setName(name);
        else
            throw new Exception("RelationshipTemplate must have a name");
    	
    	return relatBuilder.build();    
    }
    
    
    /** Create a ToscaConfigurationGrpc object */
    public static ToscaConfigurationGrpc createToscaConfigurationGrpc (String id, String descr, String config) throws Exception{
        ToscaConfigurationGrpc.Builder confBuilder = ToscaConfigurationGrpc.newBuilder().setDescription(descr); //description can be null
    	
        if(id != null)
            confBuilder.setId(id);
        else
            throw new Exception("ToscaConfigurationGrpc must have an id");
        
        /*if(config != null)
        	confBuilder.setConfiguration(config);
        else
        	throw new Exception("ToscaConfigurationGrpc must have a configuration");*/
        
        return confBuilder.build();
    }
    
    
    /*/** Create a NodeTemplate.Type object 
    public static NodeTemplateGrpc.Type createNodeTemplateType (String type) throws Exception {
    	NodeTemplateGrpc.Type nodeType;
    	if(type == null)
    		throw new Exception("Invalid value for the NodeTemplateType");
    	else
    		nodeType = Type.valueOf(type); //check on correctness of value?

    	return nodeType;
    }*/
    
    
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
