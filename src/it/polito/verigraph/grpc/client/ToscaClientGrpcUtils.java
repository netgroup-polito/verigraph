package it.polito.verigraph.grpc.client;

import javax.xml.namespace.QName;
import it.polito.verigraph.tosca.classes.*;
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
    

}
