package it.polito.verigraph.grpc.tosca.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import it.polito.verigraph.grpc.NewTopologyTemplate;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.NodeTemplateGrpc.Type;
import it.polito.verigraph.grpc.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaConfigurationGrpc;
import it.polito.verigraph.grpc.ToscaPolicy;
import it.polito.verigraph.grpc.ToscaPolicy.PolicyType;
import it.polito.verigraph.grpc.ToscaVerificationGrpc;
import it.polito.verigraph.grpc.client.ToscaClient;
import it.polito.verigraph.grpc.server.Service;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GrpcToscaTest {
    private Service server;
    private ToscaClient client;
    private TopologyTemplateGrpc testTemplate, simpleTestTemplate;
    private String testTemplateId, simpleTestTemplateId; //The actual id assigned by neo4j, to be set after test0
    
    
    public GrpcToscaTest() {
    	this.generateTestTemplate();
    	this.testTemplateId = null;
    	this.simpleTestTemplateId = null;
    }
    
    @Before
    public void setUpBeforeClass() throws Exception {
        client = new ToscaClient("localhost" , 50051);
        server = new Service(50051);
        server.start();
        
    }
    
    @After
    public void tearDown() throws Exception {
        server.stop();
        client.shutdown();
    }
    
    
    @Test
    public void Test0Creation() {
    	System.out.println("Test A: Graph Creation.");
    	NewTopologyTemplate response = client.createTopologyTemplate(testTemplate);
    	
    	assertNotNull("there was no response", response);
    	assertEquals(response.getSuccess(), true);
    	assertEquals("error report: " + response.getErrorMessage() ,response.getErrorMessage(), "");
    	
    	//If there were no errors we can retrieve the actual Id
    	this.testTemplateId = response.getTopologyTemplate().getId();
    	
    	return;
    }
    
    @Test
    public void Test1Reading() {
    	return;
    }
    
    @Test
    public void Test2Update() {
    	return;
    }
    
    @Test
    public void Test3Verification() {
    	System.out.println("Test D: Verification.");
    	
    	//REACHABILITY test
    	System.out.println("Phase 1.1 - Reachability SAT.");
    	ToscaPolicy policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.reachability).setSource("host2").setDestination("host1").build();
    	
    	ToscaVerificationGrpc result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "SAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	result = null;
    	System.out.println("Phase 1.2 - Reachability UNSAT.");
    	policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.reachability).setSource("host1").setDestination("antispamNode1").build();
    	
    	result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "UNSAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	//ISOLATION test
    	result = null;
    	System.out.println("Phase 2.1 - Isolation SAT.");
    	policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.isolation).setSource("host2").setDestination("host1").setMiddlebox("webserver1").build();
    	
    	result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "SAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	System.out.println("Phase 2.2 - Isolation UNSAT.");
    	policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.isolation).setSource("host2").setDestination("host1").setMiddlebox("fw").build();
    	
    	result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "UNSAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	//TRAVERSAL test
    	result = null;
    	System.out.println("Phase 3.1 - Traversal SAT.");
    	policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.traversal).setSource("host2").setDestination("host1").setMiddlebox("webserver1").build();
    	
    	result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "SAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	System.out.println("Phase 3.2 - Traversal UNSAT.");
    	policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(this.testTemplateId)
    			.setType(PolicyType.traversal).setSource("host2").setDestination("host1").setMiddlebox("fw").build();
    	
    	result = client.verifyPolicy(policy);
    	assertNotNull("there was no response", result);
    	assertEquals("unexpected result : " + result.getResult() + " : " + result.getComment(), result.getResult(), "UNSAT");
    	assertEquals("error report: " + result.getErrorMessage(), result.getErrorMessage(), "");
    	
    	
    	return;
    }
    
    @Test
    public void Test4Deletion() {
    	return;
    }
    
    
    //Generates a correct instance of a TopologyTemplateGrpc to be used in tests
    public void generateTestTemplate() {
    	TopologyTemplateGrpc.Builder templ = TopologyTemplateGrpc.newBuilder();
		List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
		List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();
		
		//Definition of nodes
		ToscaConfigurationGrpc node0conf = ToscaConfigurationGrpc.newBuilder().setDescription("node0configuration")
				.setId("100").setConfiguration("[{\r\n\"webserver1\":\"host2\"\r\n}]").build();
		NodeTemplateGrpc node0 = NodeTemplateGrpc.newBuilder().setConfiguration(node0conf).setId("100")
				.setName("fw").setType(Type.firewall).build();
		nodes.add(node0);
		
		ToscaConfigurationGrpc node1conf = ToscaConfigurationGrpc.newBuilder().setDescription("node1configuration")
				.setId("101").setConfiguration("[]").build();
		NodeTemplateGrpc node1 = NodeTemplateGrpc.newBuilder().setConfiguration(node1conf).setId("101")
				.setName("webserver1").setType(Type.webserver).build();
		nodes.add(node1);
		
		ToscaConfigurationGrpc node2conf = ToscaConfigurationGrpc.newBuilder().setDescription("node2configuration")
				.setId("102").setConfiguration("[{\r\n\"protocol\":\"HTTP_REQUEST\",\r\n \"url\":\"www.facebook.com\"\r\n }]").build();
		NodeTemplateGrpc node2 = NodeTemplateGrpc.newBuilder().setConfiguration(node2conf).setId("102")
				.setName("host2").setType(Type.endhost).build();
		nodes.add(node2);
		
		ToscaConfigurationGrpc node3conf = ToscaConfigurationGrpc.newBuilder().setDescription("node3configuration")
				.setId("103").setConfiguration("[ {\r\n\"protocol\":\"HTTP_REQUEST\",\r\n\"url\":\"www.google.com\",\r\n\"destination\":\"server1\"\r\n}]").build();
		NodeTemplateGrpc node3 = NodeTemplateGrpc.newBuilder().setConfiguration(node3conf).setId("103")
				.setName("host1").setType(Type.endhost).build();
		nodes.add(node3);
		
		ToscaConfigurationGrpc node4conf = ToscaConfigurationGrpc.newBuilder().setDescription("node4configuration")
				.setId("104").setConfiguration("[\"host1\",\"host2\"]").build();
		NodeTemplateGrpc node4 = NodeTemplateGrpc.newBuilder().setConfiguration(node4conf).setId("104")
				.setName("antispamNode1").setType(Type.antispam).build();
		nodes.add(node4);
		
		//Building relationships
		RelationshipTemplateGrpc rel0 = RelationshipTemplateGrpc.newBuilder().setId("1001")
				.setIdSourceNodeTemplate("100").setIdTargetNodeTemplate("101").setName("fwToServ1").build();
		relats.add(rel0);
		
		RelationshipTemplateGrpc rel1 = RelationshipTemplateGrpc.newBuilder().setId("1002")
				.setIdSourceNodeTemplate("101").setIdTargetNodeTemplate("100").setName("serv1ToFw").build();
		relats.add(rel1);
		
		RelationshipTemplateGrpc rel2 = RelationshipTemplateGrpc.newBuilder().setId("1003")
				.setIdSourceNodeTemplate("100").setIdTargetNodeTemplate("103").setName("fwToHost1").build();
		relats.add(rel2);
		
		RelationshipTemplateGrpc rel3 = RelationshipTemplateGrpc.newBuilder().setId("1004")
				.setIdSourceNodeTemplate("100").setIdTargetNodeTemplate("102").setName("fwToHost2").build();
		relats.add(rel3);
		
		RelationshipTemplateGrpc rel4 = RelationshipTemplateGrpc.newBuilder().setId("1005")
				.setIdSourceNodeTemplate("102").setIdTargetNodeTemplate("100").setName("Host2Tofw").build();
		relats.add(rel4);
		
		RelationshipTemplateGrpc rel5 = RelationshipTemplateGrpc.newBuilder().setId("1006")
				.setIdSourceNodeTemplate("103").setIdTargetNodeTemplate("100").setName("Host1Tofw").build();
		relats.add(rel5);
		
    	this.testTemplate = templ.addAllNodeTemplate(nodes).addAllRelationshipTemplate(relats).setId("0").build();
    	
    	
    	//TODO Ciao angelicchio ho pensato i scriverti questo messagino così che tu scriva qui sotto
    	//la roba per generare il simple template, quando lo crei nella get salvane anche l'id reale nella variabile
    	//globale come è stato fatto per quello grosso così posso usare quello piccolo anche nella update. TVB ciao.
    }

}
