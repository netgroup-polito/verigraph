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
import it.polito.verigraph.grpc.client.*;
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
    		info("[getTopologyTemplates] Receiving TopologyTemplates...");
    		while(receivedTemplates.hasNext()) {
    			TopologyTemplateGrpc received = receivedTemplates.next();
    			if(received.getErrorMessage().equals("")) {
    				info("[getTopologyTemplates] Received TopologyTemplate --> id:" + received.getId());
    				templates.add(received);
    			} else 
    				info("[getTopologyTemplates] Error receiving TopologyTemplates: " + received.getErrorMessage());	
    		}
    	} catch (StatusRuntimeException ex) {
    		warning("[getTopologyTemplates] RPC failed : " + ex.getMessage());
    	}
    	info("[getTopologyTemplates] All TopologyTemplates received.");
    	return templates;
    }   
    
    
    /** Obtain a TopologyTemplate by ID */
    public TopologyTemplateGrpc getTopologyTemplate(long id) {
        ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
        try {
        	TopologyTemplateGrpc response = blockingStub.getTopologyTemplate(request);
        	info("[getTopologyTemplate] Receiving TopologyTemplate...");
            if(response.getErrorMessage().equals("")){
            	info("[getTopologyTemplate] Received TopologyTemplate --> id:" + response.getId());
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
    
    
    /** Creates a new TopologyTemplate, takes in input a TopologyTemplateGrpc  */   
    public NewTopologyTemplate createTopologyTemplate(TopologyTemplateGrpc topol) {
    	try {
    		//Sending new Topology and analyzing response
	    	info("[createTopologyTemplate] Sending the new TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.createTopologyTemplate(topol);
    		if(response.getSuccess()) 
    			info("[createTopologyTemplate] TopologyTemplate successfully created.");
    		else
    			warning("[createTopologyTemplate] RPC failed : " + response.getErrorMessage());    	
    		return response;
    		
    	} catch (StatusRuntimeException ex) {
    		warning("[createTopologyTemplate] RPC failed : " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (ClassCastException | DataNotFoundException ex) {
        	warning("[createTopologyTemplate] RPC failed : ");
    		ex.printStackTrace();
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
        } 
    }    
    
    
    /** Update a TopologyTemplate, takes in input a tosca compliant filename */   
    public NewTopologyTemplate updateTopologyTemplate(TopologyTemplateGrpc topol, long id) {
    	try {
	    	//Sending updated Topology and analyzing response
	    	info("[updateTopologyTemplate] Sending the updated TopologyTemplate...");
    		NewTopologyTemplate response = blockingStub.updateTopologyTemplate(topol);
    		if(response.getSuccess())
    			info("[updateTopologyTemplate] TopologyTemplate successfully updated.");
	    	else
	    		warning("[updateTopologyTemplate] RPC failed on updateTopologyTemplate : " + response.getErrorMessage());    	
    		return response;
    		
    	} catch (StatusRuntimeException ex) {
    		warning("[updateTopologyTemplate] RPC failed : " + ex.getStatus());
    		return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStackTrace().toString()).build();
    	} catch (ClassCastException | DataNotFoundException ex) {
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
    	List<Long> topologyIDList = new ArrayList<Long>(); //list of TopologyTemplate ID

        ToscaClient client = new ToscaClient("localhost" , 50051);
        try {        	
        	//Create a new Topology
        	ToscaConfigurationGrpc config1 = ToscaClientGrpcUtils.createToscaConfigurationGrpc("01", "blabla", "");
        	ToscaConfigurationGrpc config2 = ToscaClientGrpcUtils.createToscaConfigurationGrpc("02", "blabla2", "");
                	
        	NodeTemplateGrpc node1 = ToscaClientGrpcUtils.createNodeTemplateGrpc("Node1", 1, "endhost", config1);
        	NodeTemplateGrpc node2 = ToscaClientGrpcUtils.createNodeTemplateGrpc("Node2", 2, "endpoint", config2);       	
        	RelationshipTemplateGrpc relat1 = ToscaClientGrpcUtils.createRelationshipTemplateGrpc("nomeacaso", 100, 1, 2);
        	
            List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
            nodes.add(node1);
            nodes.add(node2);
            List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();
            relats.add(relat1);
            TopologyTemplateGrpc topol = ToscaClientGrpcUtils.createTopologyTemplateGrpc(10, nodes, relats);
            
            NewTopologyTemplate createdTopol = client.createTopologyTemplate(topol);
            
            if(createdTopol.getSuccess() == true){
                topologyIDList.add(createdTopol.getTopologyTemplate().getId());
                System.out.println("Created graph with id :"+ createdTopol.getTopologyTemplate().getId());
            }
            
            //Print all Topology on server
            for(TopologyTemplateGrpc g : client.getTopologyTemplates()) {
                System.out.println("* TopologyTemplate id: " + g.getId());
                for (NodeTemplateGrpc n: g.getNodeTemplateList())
                    System.out.println(" \tNodeTemplate id: " + n.getId() + " " + n.getName());
                for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
                    System.out.println(" \tRelationshipTemplate id: " + rel.getId() + " " + rel.getName());
                System.out.println("** Topology ended");
            }
            System.out.println("\n* All Topology showed");
            
            //Update a Topology 	(put a firewall between the two nodes)
            ToscaConfigurationGrpc config3 = ToscaClientGrpcUtils.createToscaConfigurationGrpc("03", "blabla3", "");
            NodeTemplateGrpc node3 = ToscaClientGrpcUtils.createNodeTemplateGrpc("Node3", 3, "firewall", config3);
            RelationshipTemplateGrpc relat2 = ToscaClientGrpcUtils.createRelationshipTemplateGrpc("lala", 101, 1, 3);
            RelationshipTemplateGrpc relat3 = ToscaClientGrpcUtils.createRelationshipTemplateGrpc("lala2", 102, 3, 2);
            nodes.add(node3);
            relats.remove(relat1);
            relats.add(relat2);
            relats.add(relat3);
            TopologyTemplateGrpc topol2 = ToscaClientGrpcUtils.createTopologyTemplateGrpc(createdTopol.getTopologyTemplate().getId(), nodes, relats);
            
            NewTopologyTemplate updatedTopol = client.updateTopologyTemplate(topol2, createdTopol.getTopologyTemplate().getId());
            
            //Print all Topology on server
            for(TopologyTemplateGrpc g : client.getTopologyTemplates()) {
                System.out.println("* TopologyTemplate id: " + g.getId());
                for (NodeTemplateGrpc n: g.getNodeTemplateList())
                    System.out.println(" \tNodeTemplate id: " + n.getId() + " " + n.getName());
                for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
                    System.out.println(" \tRelationshipTemplate id: " + rel.getId() + " " + rel.getName());
                System.out.println("** Topology ended");
            }
            System.out.println("\n* All Topology showed");
            
            //Delete a Topology
            Status result = client.deleteTopologyTemplate(updatedTopol.getTopologyTemplate().getId());
            if(result.getSuccess())
            	System.out.println("Topology deleted.");
            List<TopologyTemplateGrpc> topols = client.getTopologyTemplates();
            if(topols.isEmpty())
            		System.out.println("Ok");
        	
            //In the following there is a file.xml parsing, error on RelationshipTemplate ref element
            
        	/*//Print all Topology on server
            for(TopologyTemplateGrpc g : client.getTopologyTemplates()) {
                System.out.println("* TopologyTemplate id: " + g.getId());
                for (NodeTemplateGrpc n: g.getNodeTemplateList())
                    System.out.println(" \tNodeTemplate id: " + n.getId() + " " + n.getName());
                for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
                    System.out.println(" \tRelationshipTemplate id: " + rel.getId() + " " + rel.getName());
                System.out.println("** Topology ended");
            }
            System.out.println("\n* All Topology showed");
            
            TopologyTemplateGrpc fileTopology = ToscaClientGrpcUtils.obtainTopologyTemplateGrpc("D:\\GIT_repository\\verigraph\\tosca_support\\DummyServiceTemplate.xml");
        	client.createTopologyTemplate(fileTopology);
        	
        	//Print all Topology on server
            for(TopologyTemplateGrpc g : client.getTopologyTemplates()) {
                System.out.println("* TopologyTemplate id: " + g.getId());
                for (NodeTemplateGrpc n: g.getNodeTemplateList())
                    System.out.println(" \tNodeTemplate id: " + n.getId() + " " + n.getName());
                for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
                    System.out.println(" \tRelationshipTemplate id: " + rel.getId() + " " + rel.getName());
                System.out.println("** Topology ended");
            }
            System.out.println("\n* All Topology showed");*/
        	
        } catch(Exception ex){
            System.out.println("Error: "); 
            ex.getMessage();
            ex.printStackTrace();
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
    
    private void info(String msg) {
        logger.log(Level.INFO, msg);  
    }

    private void warning(String msg) {
        logger.log(Level.WARNING, msg);
    }
 
}
