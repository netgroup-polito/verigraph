package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.Scanner;
import java.util.NoSuchElementException;

import java.io.IOException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import javax.xml.bind.JAXBException;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.tosca.GetRequest;
import it.polito.verigraph.grpc.tosca.Status;
import it.polito.verigraph.grpc.*;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;
import it.polito.verigraph.tosca.classes.*;
import it.polito.verigraph.tosca.*;


public class ToscaClient {
	
    private final ManagedChannel channel;
    private final ToscaVerigraphBlockingStub blockingStub;
    private static final Logger logger = Logger.getLogger(ToscaClient.class.getName());
    private static FileHandler fh;
    
    private HashMap<Long,TopologyTemplateGrpc> myTemplates = new HashMap<Long,TopologyTemplateGrpc>(); 
    
    
    
    public ToscaClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }
	
    /** Construct client for accessing toscaVerigraph server using the existing channel. */
    public ToscaClient(ManagedChannelBuilder<?> channelBuilder) {
    	  channel = channelBuilder.build();
    	  blockingStub = ToscaVerigraphGrpc.newBlockingStub(channel);
    	  setUpLogger();
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
    		info("[getTopologyTemplates] Receiving TopologyTemplates...");
    		while(receivedTemplates.hasNext()) {
    			TopologyTemplateGrpc received = receivedTemplates.next();
    			if(received.getErrorMessage().equals("")) {
    				info("[getTopologyTemplates] Received TopologyTemplate: id - " + received.getId());
    				templates.add(received);
    			} else 
    				info("[getTopologyTemplates] Error receiving TopologyTemplates: " + received.getErrorMessage());	
    		}
    	} catch (StatusRuntimeException ex) {
    		warning("[getTopologyTemplates] RPC failed : " + ex.getMessage());
    	}  	
    	return templates;
    }   
    
    /** Obtain a TopologyTemplate by ID */
    public TopologyTemplateGrpc getTopologyTemplate(long id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	TopologyTemplateGrpc response = blockingStub.getTopologyTemplate(request);
        	info("[getTopologyTemplate] Receiving TopologyTemplate...");
            if(response.getErrorMessage().equals("")){
            	info("[getTopologyTemplate] Received TopologyTemplate : id - " + response.getId());
                return response;
            }else{
            	warning("[getTopologyTemplate] Error receiving TopologyTemplate : " + response.getErrorMessage());
                return response;
            }
        } catch (StatusRuntimeException ex) {
        	warning("[getTopologyTemplate] RPC failed : " + ex.getStatus());
            return null;
        }
    }
    
    /** Creates a new TopologyTemplate, takes in input a tosca compliant filename */   
    public NewTopologyTemplate createTopologyTemplate(String toscaFile) {
    	try {
    		List<TServiceTemplate> serviceTList = XmlParsingUtils.obtainServiceTemplates(toscaFile); 
	    	TServiceTemplate serviceTemplate = serviceTList.get(0); //obtain only the first ServiceTemplate of the TOSCA compliance file

	    	//Retrieving of list of NodeTemplate and RelationshipTemplate
	    	List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
	    	List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();	    
	    	for(TNodeTemplate nt : XmlParsingUtils.obtainNodeTemplates(serviceTemplate) )
	    		nodes.add(ToscaClientGrpcUtils.parseNodeTemplate(nt));
	    	for(TRelationshipTemplate rt : XmlParsingUtils.obtainRelationshipTemplates(serviceTemplate) )
	    		relats.add(ToscaClientGrpcUtils.parseRelationshipTemplate(rt));
	    	
	    	//Creating TopologyTemplateGrpc object to be sent to server
	    	TopologyTemplateGrpc topologyTemplateGrpc = TopologyTemplateGrpc.newBuilder()
	    			.setId(0) //Setting Id of the new topology template
	    			.addAllNodeTemplate(nodes)
	    			.addAllRelationshipTemplate(relats)
	    			.build();
	    	
	    	//Sending and response analyzing
	    	info("[createTopologyTemplate] Sending the new TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.createTopologyTemplate(topologyTemplateGrpc);
    		if(response.getSuccess()) 
    			info("[createTopologyTemplate] TopologyTemplate successfully created.");
    		else
    			warning("[createTopologyTemplate] RPC failed : " + response.getErrorMessage());
    	
    		return response;
    	} catch (StatusRuntimeException ex) {
    		warning("[createTopologyTemplate] RPC failed : " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (IOException ex) {
    		warning("[createTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (JAXBException ex) {
        	warning("[createTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (ClassCastException ex) {
        	warning("[createTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (DataNotFoundException ex) {
        	warning("[createTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        }
}   
    /** Update a TopologyTemplate, takes in input a tosca compliant filename */   
    public NewTopologyTemplate updateTopologyTemplate(String toscaFile, long id) {
    	try {
	    	List<TServiceTemplate> serviceTList = XmlParsingUtils.obtainServiceTemplates(toscaFile); 
	    	TServiceTemplate serviceTemplate = serviceTList.get(0); //obtain only the first SerivceTemplate of the TOSCA compliance file
	    	
	    	//Retrieving of list of NodeTemplate and RelationshipTemplate
	    	List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
	    	List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();	    
	    	for(TNodeTemplate nt : XmlParsingUtils.obtainNodeTemplates(serviceTemplate) )
	    		nodes.add(ToscaClientGrpcUtils.parseNodeTemplate(nt));
	    	for(TRelationshipTemplate rt : XmlParsingUtils.obtainRelationshipTemplates(serviceTemplate) )
	    		relats.add(ToscaClientGrpcUtils.parseRelationshipTemplate(rt));
	    	
	    	//Creating TopologyTemplateGrpc object to be sent to server
	    	TopologyTemplateGrpc topologyTemplateGrpc = TopologyTemplateGrpc.newBuilder()
	    			.setId(id) 
	    			.addAllNodeTemplate(nodes)
	    			.addAllRelationshipTemplate(relats)
	    			.build();
	    	
	    	//Sending and response analyzing
	    	info("[updateTopologyTemplate] Sending the updated TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.createTopologyTemplate(topologyTemplateGrpc);
    		if(response.getSuccess())
    			info("[updateTopologyTemplate] TopologyTemplate successfully updated.");
	    	else
	    		warning("[updateTopologyTemplate] RPC failed on updateTopologyTemplate : " + response.getErrorMessage());    	
    		return response;
    	} catch (StatusRuntimeException ex) {
    		warning("[updateTopologyTemplate] RPC failed : " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (IOException ex) {
    		warning("[updateTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (JAXBException ex) {
        	warning("[updateTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (ClassCastException ex) {
        	warning("[updateTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } catch (DataNotFoundException ex) {
        	warning("[updateTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        }
}   
    
    /** Delete a TopologyTemplate by ID */
    public Status deleteTopologyTemplate(long id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	info("[deleteTopologyTemplate] Sending delete request...");
        	Status response = blockingStub.deleteTopologyTemplate(request);
            if(response.getSuccess())
            	info("[deleteTopologyTemplate] TopologyTemplate successfully deleted.");
            else
            	warning("[deleteTopologyTemplate] Error deleting TopologyTemplate : " + response.getErrorMessage());           
            return response;
            
        } catch (StatusRuntimeException ex) {
        	warning("[deleteTopologyTemplate] RPC failed : " + ex.getStatus());
            return Status.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        }
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
    
    /** VerifyPolicy */
    public ToscaVerificationGrpc verifyPolicy(ToscaPolicy policy){
    	ToscaVerificationGrpc response;
        try {
        	info("[verifyToscaPolicy] Sending ToscaPolicy...");
            response = blockingStub.verifyPolicy(policy);
            if(!response.getErrorMessage().equals("")){
            	warning("[verifyToscaPolicy] Error in operation: " + response.getErrorMessage());
            }
            info("[verifyToscaPolicy] Result : "+response.getResult());
            info("[verifyToscaPolicy] Comment : "+response.getComment());
            /*//uncomment if you want to print the paths
            for(ToscaTestGrpc test : response.getTestList()){
            	System.out.println("Test : "+test.getResult()+". Traversed nodes:");
            	for(NodeTemplateGrpc node : test.getNodeTemplateList()){
            		System.out.println("Node "+node.getName());
            }
            }*/
            return response;
        } catch (StatusRuntimeException e) {
            warning("[verifyToscaPolicy] RPC failed: " + e.getStatus());
            return ToscaVerificationGrpc.newBuilder().setSuccessOfOperation(false).setErrorMessage(e.getStackTrace().toString()).build();
        }
    }
    
    
    
    
    public static void main(String args[]){
    	System.out.println("[grpcClient] Welcome to Verigraph Verification Serivice grpcClient...");
    	
    	Scanner input = new Scanner(System.in);
    	String command;
    	Long idref;
    	
    	while(true) {
    		System.out.println("[grpcClient] Insert command :");	
    		try{
    			command = input.next();
    			switch (command.toUpperCase()) {
    				case "GETALL": //gettopologyTemplates
    					this.getAll();
    					break;
    				case "GET":
    					//handle get
    					break;
    				case "CREATE":
    					//handle create
    					break;
    				case "DELETE":
    					//handle delete
    					break;
    				case "UPDATE":
    					//handle update
    					break;
    				case "VERIFY":
    					//handle update
    					break;
    				case "HELP":
    					System.out.println(ToscaClientGrpcUtils.helper);
    					break;
    				case "CLOSE":
    					//to be defined
    					break;
    				default:
    					//clean line
    					System.out.println(ToscaClientGrpcUtils.helper);
    					break;
    			}
    		
    		}catch(NoSuchElementException ex) {
    			System.err.println("[toscaClient] Unrecognized or incorrect command,"
    					+ " type help to know how to use the client...");
    			continue;
    		}
    	}
    	
    	
    }
    

    
    
    
    
    
    
    /** The clients prints logs on File - to be defined, two levels of log*/
    private void setUpLogger(){
    	try {  
    		// This block configure the logger with handler and formatter  
    		fh = new FileHandler(".\\ToscaClientLog.log");  
    		logger.addHandler(fh);
    		SimpleFormatter formatter = new SimpleFormatter();  
    		fh.setFormatter(formatter);  
    	} catch (SecurityException e) {  
    		e.printStackTrace();  
    	} catch (IOException e) {  
    		e.printStackTrace();  
    	}  
    }
    
    private void info(String msg) {
        logger.log(Level.INFO, msg);  
        System.out.println(msg);
    }

    private void warning(String msg) {
        logger.log(Level.WARNING, msg);
        System.err.println(msg);
    }
 
}
