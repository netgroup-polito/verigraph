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
import it.polito.verigraph.grpc.*;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.tosca.*;

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
    			} else 
    				System.out.println("[ToscaClient] Error receiving TopologyTemplates: " + received.getErrorMessage());	
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
    	TServiceTemplate serviceTemplate = XmlParsingUtils.obtainServiceTemplate(toscaFile); //catchare eccezione FileNotFound e FileNotToscaCompliance
    	
    	//DA GESTIRE CON ECCEZIONE SCRITTE QUA SOPRA
    	/*if(jaxbServ == null) {
    		System.out.println("[ToscaClient] Unable to retrieve any Service Template from file...");
    		return;
    	}*/
    	
    	//Retrieving of list of NodeTemplate and RelationshipTemplate
    	List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
    	List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();
    
    	for(TNodeTemplate nt : XmlParsingUtils.obtainNodeTemplates(serviceTemplate) )
    		nodes.add(parseNodeTemplate(nt));
    	for(TRelationshipTemplate rt : XmlParsingUtils.obtainRelationshipTemplates(serviceTemplate) )
    		relats.add(parseRelationshipTemplate(rt));
    	
    	//Creating TopologyTemplateGrpc object to be sent to server
    	TopologyTemplateGrpc topologyTemplateGrpc = TopologyTemplateGrpc.newBuilder()
    			.setId(0) //Setting Id of the new topology template
    			.addAllNodeTemplate(nodes)
    			.addAllRelationshipTemplate(relats)
    			.build();
    	
    	//Sending and response analysing
    	NewTopologyTemplate response = blockingStub.createTopologyTemplate(topologyTemplateGrpc);
    	if(response.getSuccess())
    		System.out.println("[ToscaClient] TopologyTemplate successfully created.");
    	else
    		System.out.println("[ToscaClient] Error during TopologyTemplate creation : " + response.getErrorMessage());    		
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
