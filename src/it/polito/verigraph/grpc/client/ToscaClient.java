package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import java.io.IOException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.tosca.*;
import it.polito.verigraph.grpc.tosca.*;
import it.polito.verigraph.grpc.*;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;


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
    		System.out.println("[getTopologyTemplates] Receiving TopologyTemplates...");
    		while(receivedTemplates.hasNext()) {
    			TopologyTemplateGrpc received = receivedTemplates.next();
    			if(received.getErrorMessage().equals("")) {
    				System.out.println("[getTopologyTemplates] Correctly received TopologyTemplate --> id:" + received.getId());
    				templates.add(received);
    			} else 
    				System.out.println("[getTopologyTemplates] Received a TopologyTemplate with error: " + received.getErrorMessage());	
    		}
    	} catch (StatusRuntimeException ex) {
    		warning("[getTopologyTemplates] RPC failed: " + ex.getMessage());
    	}
    	System.out.println("[getTopologyTemplates] All TopologyTemplates received.");
    	return templates;
    }   
    
    
    /** Obtain a TopologyTemplate by ID */
    public TopologyTemplateGrpc getTopologyTemplate(String id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	System.out.println("[getTopologyTemplate] Receiving TopologyTemplate...");
        	TopologyTemplateGrpc response = blockingStub.getTopologyTemplate(request);       	
            if(response.getErrorMessage().equals("")){
            	System.out.println("[getTopologyTemplate] Received TopologyTemplate --> id:" + response.getId());
                return response;
            } else {
            	System.out.println("[getTopologyTemplate] Received a TopologyTemplate with error: " + response.getErrorMessage());
                return response;
            }
        } catch (StatusRuntimeException ex) {
        	warning("[getTopologyTemplate] RPC failed: " + ex.getStatus());
            return null;
        }
    }
    
    
    /** Creates a new TopologyTemplate, takes in input a TopologyTemplateGrpc  */   
    public NewTopologyTemplate createTopologyTemplate(TopologyTemplateGrpc topol) {
    	try {
    		//Sending new Topology and analyzing response
    		System.out.println("[createTopologyTemplate] Sending the new TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.createTopologyTemplate(topol);
    		if(response.getSuccess()) 
    			System.out.println("[createTopologyTemplate] TopologyTemplate successfully created.");
    		else
    			System.out.println("[createTopologyTemplate] TopologyTemplate creation failed: " + response.getErrorMessage());    	
    		return response;
    		
    	} catch (StatusRuntimeException ex) {
    		warning("[createTopologyTemplate] RPC failed: " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (ClassCastException | DataNotFoundException ex) {
        	warning("[createTopologyTemplate] RPC failed: ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } 
    }    
    
    
    /** Update a TopologyTemplate, takes in input a TopologyTemplateGrpc, the ID must be well set inside the TopologyTemplateGrpc */   
    public NewTopologyTemplate updateTopologyTemplate(TopologyTemplateGrpc topol) {
    	try {
	    	//Sending updated Topology and analyzing response
    		System.out.println("[updateTopologyTemplate] Sending the updated TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.updateTopologyTemplate(topol);
    		if(response.getSuccess())
    			System.out.println("[updateTopologyTemplate] TopologyTemplate successfully updated.");
	    	else
	    		System.out.println("[updateTopologyTemplate] TopologyTemplate not updated: " + response.getErrorMessage());    	
    		return response;
    		
    	} catch (StatusRuntimeException ex) {
    		warning("[updateTopologyTemplate] RPC failed: " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (ClassCastException | DataNotFoundException ex) {
        	warning("[updateTopologyTemplate] RPC failed: ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } 
    }   
    
    
    /** Delete a TopologyTemplate by ID */
    public Status deleteTopologyTemplate(String id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	System.out.println("[deleteTopologyTemplate] Sending delete request...");
        	Status response = blockingStub.deleteTopologyTemplate(request);
            if(response.getSuccess())
            	System.out.println("[deleteTopologyTemplate] TopologyTemplate successfully deleted.");
            else
            	System.out.println("[deleteTopologyTemplate] Error deleting TopologyTemplate : " + response.getErrorMessage());           
            return response;
            
        } catch (StatusRuntimeException ex) {
        	warning("[deleteTopologyTemplate] RPC failed: " + ex.getStatus());
            return Status.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        }
    }
    
    
    /** VerifyPolicy */
    public ToscaVerificationGrpc verifyPolicy(ToscaPolicy policy){
    	ToscaVerificationGrpc response;
        try {
        	System.out.println("[verifyToscaPolicy] Sending ToscaPolicy...");
            response = blockingStub.verifyPolicy(policy);
            if(!response.getErrorMessage().equals("")){
            	System.out.println("[verifyToscaPolicy] Error in operation: " + response.getErrorMessage());
            }
            System.out.println("[verifyToscaPolicy] Result : " + response.getResult());
            System.out.println("[verifyToscaPolicy] Comment : " + response.getComment());
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
        ToscaClient client = new ToscaClient("localhost" , 50051);
        
        try {        	
        	/*//Create a new Topology
        	ToscaConfigurationGrpc config1 = ToscaGrpcUtils.createToscaConfigurationGrpc("01", "blabla", "");
        	ToscaConfigurationGrpc config2 = ToscaGrpcUtils.createToscaConfigurationGrpc("02", "blabla2", "");
                	
        	NodeTemplateGrpc node1 = ToscaGrpcUtils.createNodeTemplateGrpc("host1", "101", "endhost", config1);
        	NodeTemplateGrpc node2 = ToscaGrpcUtils.createNodeTemplateGrpc("host2", "102", "endpoint", config2);       	
        	RelationshipTemplateGrpc relat1 = ToscaGrpcUtils.createRelationshipTemplateGrpc("HOST1toHOST2", "901", "101", "102");
        	
            List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
            nodes.add(node1);
            nodes.add(node2);
            List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();
            relats.add(relat1);
            TopologyTemplateGrpc topol = ToscaGrpcUtils.createTopologyTemplateGrpc("10", nodes, relats);
            
            NewTopologyTemplate createdTopol = client.createTopologyTemplate(topol);
            if(createdTopol.getSuccess() == true)
            	System.out.println("Created graph with id :"+ createdTopol.getTopologyTemplate().getId());
            
            //Print all Topology on server
            List<TopologyTemplateGrpc> topolList = client.getTopologyTemplates(); 
            ToscaGrpcUtils.printTopologyTemplates(topolList);
          
            //Update a Topology 	(put a firewall between the two nodes)
            ToscaConfigurationGrpc config3 = ToscaGrpcUtils.createToscaConfigurationGrpc("03", "blabla3", "");
            NodeTemplateGrpc node3 = ToscaGrpcUtils.createNodeTemplateGrpc("myfirewall", "103", "firewall", config3);
            RelationshipTemplateGrpc relat2 = ToscaGrpcUtils.createRelationshipTemplateGrpc("newRelat1", "902", "101", "103");
            RelationshipTemplateGrpc relat3 = ToscaGrpcUtils.createRelationshipTemplateGrpc("newRelat2", "903", "103", "102");
            nodes.add(node3);
            relats.remove(relat1);
            relats.add(relat2);
            relats.add(relat3);
            TopologyTemplateGrpc topol2 = ToscaGrpcUtils.createTopologyTemplateGrpc(createdTopol.getTopologyTemplate().getId(), nodes, relats);
            
            NewTopologyTemplate updatedTopol = client.updateTopologyTemplate(topol2);
            
            //Print all Topology on server
            topolList = client.getTopologyTemplates(); 
            ToscaGrpcUtils.printTopologyTemplates(topolList);
            
            //Delete a Topology
            Status result = client.deleteTopologyTemplate(updatedTopol.getTopologyTemplate().getId());
            if(result.getSuccess())
            	System.out.println("Topology deleted.");
            List<TopologyTemplateGrpc> topols = client.getTopologyTemplates();
            if(topols.isEmpty())
            		System.out.println("Ok");
        	*/
            
        	//Print all Topology on server
        	List<TopologyTemplateGrpc> topol = client.getTopologyTemplates();
        	ToscaGrpcUtils.printTopologyTemplates(topol);           
            
            TopologyTemplateGrpc fileTopology = ToscaGrpcUtils.obtainTopologyTemplateGrpc("D:\\GIT_repository\\verigraph\\tosca_support\\DummyServiceTemplate.xml");
        	client.createTopologyTemplate(fileTopology);
        	
        	topol = client.getTopologyTemplates();
        	ToscaGrpcUtils.printTopologyTemplates(topol);   
        	
        } catch(Exception ex){
            System.out.println("Error: "); 
            ex.printStackTrace();
            ex.getMessage();
            System.out.println("Closing client...");
        } finally {
            try {
				client.shutdown();
			} catch (InterruptedException e) {
				System.out.println("Error: "); 
				e.printStackTrace();
			}
        }
    }

    
    /** The clients prints logs on File - to be defined, two levels of log*/
    private void setUpLogger(){
    	try {  
    		// This block configure the logger with handler and formatter  
    		fh = new FileHandler(".\\grpc_clientTosca_log.txt");  
    		logger.addHandler(fh);
    		SimpleFormatter formatter = new SimpleFormatter();  
    		fh.setFormatter(formatter);  
    	} catch (SecurityException e) {  
    		e.printStackTrace();  
    	} catch (IOException e) {  
    		e.printStackTrace();  
    	}  
    }
    
    /*private void info(String msg) {
        logger.log(Level.INFO, msg);  
    }*/

    private void warning(String msg) {
        logger.log(Level.WARNING, msg);
    }
 
}
