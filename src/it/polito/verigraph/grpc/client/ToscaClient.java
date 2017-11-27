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

import java.io.FileInputStream;
import java.io.IOException;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import it.polito.verigraph.grpc.tosca.*;
//import it.polito.verigraph.grpc.*;
import it.polito.verigraph.tosca.classes.*;

import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;
import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc.Type;

public class ToscaClient {
	
    private final ManagedChannel channel;
    private final ToscaVerigraphBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(ToscaClient.class.getName());
    private static FileHandler fh;
    
    
    public ToscaClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }
	
    /** Construct client for accessing toscaVerigraph server using the existing channel. */
    public ToscaClient(ManagedChannelBuilder<?> channelBuilder) {
    	  channel = channelBuilder.build();
    	  blockingStub = ToscaVerigraphGrpc.newBlockingStub(channel);
    }
    
    /** Close the channel */
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }    
    
    /** Obtain a list of the available TopologyTemplates*/
    public List<TopologyTemplateGrpc> getTopologyTemplates(){
    	List<TopologyTemplateGrpc> templates = new ArrayList<TopologyTemplateGrpc>();
    	GetRequest request = GetRequest.newBuilder().build();
    	
    	/*Iterates on received topology templates, prints on log file in case of errors*/
    	Iterator<TopologyTemplateGrpc> receivedTemplates;
    	try {
    		receivedTemplates = blockingStub.getTopologyTemplates(request);
    		System.out.println("[ToscaClient] Receiving Topology Templates...");
    		while(receivedTemplates.hasNext()) {
    			TopologyTemplateGrpc received = receivedTemplates.next();
    			if(received.getErrorMessage().equals("")) {
    				System.out.println("[ToscaClient] Received Template: id - " + received.getId());
    				templates.add(received);
    			} else {
    				System.out.println("[ToscaClient] Error receiving TopologyTemplates: " + received.getErrorMessage());
    				return templates; //DA VALUTARE
    			}	
    		}
    	} catch (StatusRuntimeException ex) {
    		System.err.println("[ToscaClient] RPC failed on getTopologyTemplates : " + ex.getMessage());
    		warning("RPC failed - on getTopologyTemplates method", ex.getStatus());
    	}
    	
    	return templates;
    }   
    
    /** Obtain a TopologyTemplate by ID */
    public TopologyTemplateGrpc getTopologyTemplate(long id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	TopologyTemplateGrpc response = blockingStub.getTopologyTemplate(request);
            if(response.getErrorMessage().equals("")){
                System.out.println("[ToscaClient] Received TopologyTemplate : id - " + response.getId());
                return response;
            }else{
                System.err.println("[ToscaClient] Error receiving TopologyTemplate : " + response.getErrorMessage());
                return response;
            }
        } catch (StatusRuntimeException ex) {
            System.err.println("[ToscaClient] RPC failed on getTopologyTemplate: " + ex.getStatus());
            return null;
        }
    }
    
    /** Creates a new TopologyTemplate, takes in input a tosca compliant filename */   
    public void createTopologyTemplate(String toscaFile) {
    	TServiceTemplate serviceTemplate = obtainServiceTemplate(toscaFile); //catchare eccezione FileNotFound e FileNotToscaCompliance
    	
    	//DA GESTIRE CON ECCEZIONE DI QUA SOPRA
    	/*if(jaxbServ == null) {
    		System.out.println("[ToscaClient] Unable to retrieve any Service Template from file...");
    		return;
    	}*/
 
    	TTopologyTemplate topologyTemplate = serviceTemplate.getTopologyTemplate();
    	TopologyTemplateGrpc.Builder topologyTemplateGrpc = TopologyTemplateGrpc.newBuilder();    	
    	
    	//Setting Id of the new topology template
    	topologyTemplateGrpc = topologyTemplateGrpc.setId(0);
    	
    	//Retrieving of list of NodeTemplate and RelationshipTemplate
    	List<TEntityTemplate> elements = topologyTemplate.getNodeTemplateOrRelationshipTemplate();
    	List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
    	List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();
    
    	for(int i = 0; i < elements.size(); i++) {
    		if(elements.get(i) instanceof TNodeTemplate)
    			nodes.add(parseToscaNode((TNodeTemplate)elements.get(i)));
    		else if(elements.get(i) instanceof TRelationshipTemplate) {
    			
    		}
    		
    	}
    	
    	
    	
    }    
    
    /** Method for parsing a tosca Node into a Grpc Node */
   
    public NodeTemplateGrpc parseToscaNode(TNodeTemplate toscaNode) {
    	
    	Boolean isVerigraphCompl = true;
    	
    	NodeTemplateGrpc.Builder parsed = NodeTemplateGrpc.newBuilder();
    	parsed.setId(toscaNode.getId()); //to convert
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
    	
    		
    	if(isVerigraphCompl) {
    		TConfiguration nodeConfig = ((TConfiguration)toscaNode.getProperties().getAny());
        	ConfigurationGrpc grpcConfig = ConfigurationGrpc.newBuilder()
   			 	 .setId(nodeConfig.getConfID())
   				 .setDescription(nodeConfig.getConfDescr())
   			     .setConfiguration(nodeConfig.getJSON()).build();
        	parsed.setConfiguration(grpcConfig);
        	
    	}else {
    		parsed.setConfiguration(); //to define a default configuration in client utils 
    	}
	
    	return parsed.build();
    }
    
    
    /** The clients prints logs on File - to be defined, two levels of log*/
    private void setUpLogger(){
    	try {  
    		// This block configure the logger with handler and formatter  
    		fh = new FileHandler("C:\\Users\\mikx_\\git\\verigraph\\tosca_support\\toscaGrpc\\toscaClientLog.log");  
    		logger.addHandler(fh);
    		SimpleFormatter formatter = new SimpleFormatter();  
    		fh.setFormatter(formatter);  
    	} catch (SecurityException e) {  
    		e.printStackTrace();  
    	} catch (IOException e) {  
    		e.printStackTrace();  
    	}  
    }
    
    private void info(String msg,  Object... params) {
        logger.log(Level.INFO, msg, params);    
    }

    private void warning(String msg, Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
    
}
