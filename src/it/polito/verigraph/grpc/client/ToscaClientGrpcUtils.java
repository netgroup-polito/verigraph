package it.polito.verigraph.grpc.client;


import javax.xml.namespace.QName;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;


public class ToscaClientGrpcUtils {
	
	/** Default configuration for a Tosca node non compliant with Verigraph types*/
	public static final String defaultConfID = new String("0");
	public static final String defaultDescr = new String("Default Configuration");
	public static final String defaultConfig = new String("");//add endhost default configuration
	
	

    /** Method for parsing a tosca Node into a Grpc Node */
    public static NodeTemplateGrpc parseNodeTemplate(TNodeTemplate toscaNode) {
    	
    	Boolean isVerigraphCompl = true;
    	
    	//Start building our node
    	NodeTemplateGrpc.Builder parsed = NodeTemplateGrpc.newBuilder();
    	parsed.setId(Long.valueOf(toscaNode.getId()).longValue()); //to convert
    	parsed.setName(toscaNode.getName());

    	//In case our node is not tosca compliant, we assume it to be an endhost node
    	switch(toscaNode.getType().getLocalPart().toLowerCase()) {
    		case "antispam":
    			parsed.setType(Type.antispam);
    		case "cache":
    			parsed.setType(Type.cache);
    		case "dpi":
    			parsed.setType(Type.dpi);
    		case "endhost":
    			parsed.setType(Type.endhost);
    		case "endpoint":
    			parsed.setType(Type.endpoint);
    		case "fieldmodifier":
    			parsed.setType(Type.fieldmodifier);
    		case "firewall":
    			parsed.setType(Type.firewall);
    		case "mailclient":
    			parsed.setType(Type.mailclient);
    		case "mailserver":
    			parsed.setType(Type.mailserver);
    		case "nat":
    			parsed.setType(Type.nat);
    		case "vpnaccess":
    			parsed.setType(Type.vpnaccess);
    		case "vpnexit":
    			parsed.setType(Type.vpnexit);
    		case "webclient":
    			parsed.setType(Type.webclient);
    		case "webserver":
    			parsed.setType(Type.webserver);
    		default:
    			parsed.setType(Type.endhost);
    			isVerigraphCompl = false;
    	}
    	
    	
    	ConfigurationGrpc grpcConfig;
    	
    	if(isVerigraphCompl) {
    		TConfiguration nodeConfig = ((TConfiguration)toscaNode.getProperties().getAny());
        	grpcConfig = ConfigurationGrpc.newBuilder()
   			 	 .setId(nodeConfig.getConfID())
   				 .setDescription(nodeConfig.getConfDescr())
   			     .setConfiguration(nodeConfig.getJSON()).build();
    	}else {
        	grpcConfig = ConfigurationGrpc.newBuilder()
   			 	 .setId(ToscaClientGrpcUtils.defaultConfID)
   				 .setDescription(ToscaClientGrpcUtils.defaultDescr)
   			     .setConfiguration(ToscaClientGrpcUtils.defaultConfig).build();
    	}
	
    	parsed.setConfiguration(grpcConfig);
    	return parsed.build();
    }
	
    
    
    /** Cast a tosca RelationshipTemplate into a grpc RelationshipTemplate */
    public static RelationshipTemplateGrpc parseRelationshipTemplate(TRelationshipTemplate toscaRel)
    	throws ClassCastException{
    	
    	RelationshipTemplateGrpc.Builder parsed = RelationshipTemplateGrpc.newBuilder();
    	
    	parsed.setId(Long.valueOf(toscaRel.getId()).longValue())
    		.setName(toscaRel.getName());
    	
    	String source = ((QName)toscaRel.getSourceElement().getRef()).getLocalPart();
    	String target = ((QName)toscaRel.getTargetElement().getRef()).getLocalPart();
    	
    	parsed.setIdSourceNodeTemplate(Long.valueOf(source).longValue())
    		.setIdTargetNodeTemplate(Long.valueOf(target).longValue());
    	
    	return parsed.build();
    }
    

}
