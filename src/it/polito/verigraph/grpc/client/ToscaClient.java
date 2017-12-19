package it.polito.verigraph.grpc.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import it.polito.verigraph.grpc.GetRequest;
import it.polito.verigraph.grpc.Status;
import it.polito.verigraph.grpc.tosca.NewTopologyTemplate;
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.ToscaPolicy;
import it.polito.verigraph.grpc.tosca.ToscaRequestID;
import it.polito.verigraph.grpc.tosca.ToscaVerificationGrpc;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc;
import it.polito.verigraph.grpc.tosca.ToscaVerigraphGrpc.ToscaVerigraphBlockingStub;
import it.polito.verigraph.tosca.converter.grpc.ToscaGrpcUtils;
import it.polito.verigraph.tosca.converter.grpc.YamlGrpcUtils;


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
		boolean response_ok = true;

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
			response_ok = false;
		}
		if(response_ok) {
			System.out.println("[getTopologyTemplates] All TopologyTemplates correctly received.");
			return templates;
		} else {
			System.out.println("[getTopologyTemplates] RPC failed.");
			return new ArrayList<TopologyTemplateGrpc>(); //Function returns empty list in case of error
		}
	}   


	/** Obtain a TopologyTemplate by ID */
	public TopologyTemplateGrpc getTopologyTemplate(String id) {
		ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
		TopologyTemplateGrpc response = TopologyTemplateGrpc.newBuilder().build();
		try {
			System.out.println("[getTopologyTemplate] Receiving TopologyTemplate...");
			response = blockingStub.getTopologyTemplate(request);       	
			if(response.getErrorMessage().equals("")){
				System.out.println("[getTopologyTemplate] Received TopologyTemplate --> id:" + response.getId());
				return response;
			} else {
				System.out.println("[getTopologyTemplate] Received a TopologyTemplate with error: " + response.getErrorMessage());
				return response;
			}
		} catch (StatusRuntimeException ex) {
			warning("[getTopologyTemplate] RPC failed: " + ex.getStatus());
			return TopologyTemplateGrpc.newBuilder().setErrorMessage(ex.getStatus().getDescription()).build();
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
			return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build(); //getDescription potrebbe essere vuoto
		} 
	}    


	/** Update a TopologyTemplate, takes in input a TopologyTemplateGrpc and the Topology's ID to be updated*/   
	public NewTopologyTemplate updateTopologyTemplate(TopologyTemplateGrpc topol, String id) {
		//Checking if the inserted string is an object
		try {
			Long.valueOf(id);
		} catch (NumberFormatException ex) {
			System.out.println("[updateTopologyTemplate] The ID must a number according to Verigraph implementation.");
			return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage("The ID must a number according to Verigraph implementation.").build();
		}

		//Update the topology ID
		TopologyTemplateGrpc.Builder updTopol = TopologyTemplateGrpc.newBuilder();
		try {
			updTopol.setId(id)
			.addAllNodeTemplate(topol.getNodeTemplateList())
			.addAllRelationshipTemplate(topol.getRelationshipTemplateList());
		} catch (Exception ex) {
			System.out.println("[updateTopologyTemplate] Error: Incorrect fields implementation.");
			return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage("Error: Incorrect fields implementation.").build();
		}

		//Sending updated Topology and analyzing response
		try {

			System.out.println("[updateTopologyTemplate] Sending the updated TopologyTemplate...");
			NewTopologyTemplate response = blockingStub.updateTopologyTemplate(updTopol.build());
			if(response.getSuccess())
				System.out.println("[updateTopologyTemplate] TopologyTemplate successfully updated.");
			else
				System.out.println("[updateTopologyTemplate] TopologyTemplate not updated: " + response.getErrorMessage());    	
			return response;

		} catch (StatusRuntimeException ex) {
			warning("[updateTopologyTemplate] RPC failed: " + ex.getStatus());
			return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build();
		} 
	}   


	/** Delete a TopologyTemplate by ID */
	public Status deleteTopologyTemplate(String id) {
		try {
			Long.valueOf(id);
		} catch (NumberFormatException ex) {
			System.out.println("[deleteTopologyTemplate] The ID must a number according to Verigraph implementation.");
			return Status.newBuilder().setSuccess(false).setErrorMessage("The ID must a number according to Verigraph implementation.").build();
		}
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
			return Status.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build();
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
			System.out.println("[verifyToscaPolicy] Result: " + response.getResult());
			System.out.println("[verifyToscaPolicy] Comment: " + response.getComment());
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
				System.out.println("Ok"); */

			//XML PARSING
			TopologyTemplateGrpc fileTopology = ToscaGrpcUtils.obtainTopologyTemplateGrpc("D:\\GIT_repository\\verigraph/tosca_examples/DummyServiceTemplate.xml");
		
		/*	//YAML PARSING
			TopologyTemplateGrpc fileTopology = YamlGrpcUtils.obtainTopologyTemplateGrpc("D:\\GIT_repository\\verigraph/tosca_examples/DummyServiceTemplate.yaml");

		/*	//DELETE TESTING
			Status response = client.deleteTopologyTemplate("270");*/

			//CREATE TESTING
			NewTopologyTemplate created = client.createTopologyTemplate(fileTopology);

		/*	//UPDATE TESTING
			TopologyTemplateGrpc fileTopology2 = ToscaGrpcUtils.obtainTopologyTemplateGrpc("D:\\GIT_repository\\verigraph\\tosca_examples\\DummyServiceTemplate2.xml");
			client.updateTopologyTemplate(fileTopology2, created.getTopologyTemplate().getId()); */

		/*	//GET TESTING
			TopologyTemplateGrpc received = client.getTopologyTemplate(created.getTopologyTemplate().getId());*/

		/*	//POLICY TESTING
			ToscaPolicy mypolicy = ToscaGrpcUtils.createToscaPolicy("host1", "host2", "reachability", null, "33");
			ToscaVerificationGrpc myverify = client.verifyPolicy(mypolicy);*/

			//Print all Topology on server
			ToscaGrpcUtils.printTopologyTemplates(client.getTopologyTemplates());
			client.deleteTopologyTemplate(created.getTopologyTemplate().getId());

		} catch(Exception ex){
			System.out.println("Error: ");
			ex.printStackTrace();
			ex.getMessage();			
		} finally {
			//System.out.println("Closing client...");
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

	private void warning(String msg) {
		logger.log(Level.WARNING, msg);
	}

}
