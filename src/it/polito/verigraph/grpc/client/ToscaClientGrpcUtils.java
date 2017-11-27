package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import it.polito.verigraph.tosca.classes.*;

import java.io.FileInputStream;
import java.io.IOException;
import it.polito.verigraph.tosca.classes.*;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;

import it.polito.verigraph.tosca.*;



public class ToscaClientGrpcUtils {
	
	/** Default configuration for a Tosca node non compliant with Verigraph types*/
	public static final String defaultConfID = new String("0");
	public static final String defaultDescr = new String("Default Confiuration");
	public static final String defaultConfig = new String("");//add endhost default configuration
	
	

    /** Method for parsing a tosca Node into a Grpc Node */
    public NodeTemplateGrpc parseNodeTemplate(TNodeTemplate toscaNode) {
    	
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
	

}
