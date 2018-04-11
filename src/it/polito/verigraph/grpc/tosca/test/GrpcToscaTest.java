/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.grpc.tosca.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

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
import it.polito.verigraph.grpc.Status;
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

    public GrpcToscaTest() {
        this.generateTestTemplate();
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
    public void test0Creation() {
        System.out.println("\nTest A: Graph Creation.");

        NewTopologyTemplate response = client.createTopologyTemplate(testTemplate);
        assertNotNull("Returned a NULL graph", response);
        assertEquals(response.getSuccess(), true);
        assertEquals("Error report: " + response.getErrorMessage(), "", response.getErrorMessage());

        Status resp = client.deleteTopologyTemplate(response.getTopologyTemplate().getId());
        assertEquals("Error while deleting testTemplate.", true, resp.getSuccess());

        System.out.println("Test A completed\n");

        return;
    }


    @Test
    public void test1Reading() {
        System.out.println("\nTest B: Graph Reading.");

        //Creating a test graph on remote repository
        System.out.println("Phase B.1 -- Creating a test graph.");
        NewTopologyTemplate response = client.createTopologyTemplate(simpleTestTemplate);
        assertNotNull("Returned a NULL graph", response);
        assertEquals(true, response.getSuccess());
        assertEquals("Error report: " + response.getErrorMessage(), "", response.getErrorMessage());

        //Reading remote graph.
        System.out.println("Phase B.2 -- Reading remote graph.");
        TopologyTemplateGrpc retrieved = client.getTopologyTemplate(response.getTopologyTemplate().getId());
        assertNotNull("Retrieved a NULL graph", retrieved);
        assertEquals(retrieved.getId(), response.getTopologyTemplate().getId());

        //Nodes checking
        System.out.println("Phase B.3 -- Checking graph's nodes.");
        assertEquals(retrieved.getNodeTemplateCount(), 3);
        assertEquals("Node1 name error", response.getTopologyTemplate().getNodeTemplateList().get(0).getName(),
                retrieved.getNodeTemplateList().get(0).getName());
        assertEquals("Node2 name error", response.getTopologyTemplate().getNodeTemplateList().get(1).getName(),
                retrieved.getNodeTemplateList().get(1).getName());
        assertEquals("Node3 name error", response.getTopologyTemplate().getNodeTemplateList().get(2).getName(),
                retrieved.getNodeTemplateList().get(2).getName());

        //Relationships checking
        System.out.println("Phase B.4 -- Checking graph's relationships.");
        assertEquals(retrieved.getRelationshipTemplateCount(), 4);
        String source1=null, target1=null;
        String source2=null, target2=null;
        String source3=null, target3=null;
        String source4=null, target4=null;
        for (NodeTemplateGrpc node : retrieved.getNodeTemplateList()){
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(0).getIdSourceNodeTemplate()))
                source1=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(0).getIdTargetNodeTemplate()))
                target1=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(1).getIdSourceNodeTemplate()))
                source2=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(1).getIdTargetNodeTemplate()))
                target2=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(2).getIdSourceNodeTemplate()))
                source3=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(2).getIdTargetNodeTemplate()))
                target3=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(3).getIdSourceNodeTemplate()))
                source4=node.getName();
            if(node.getId().equals(retrieved.getRelationshipTemplateList().get(3).getIdTargetNodeTemplate()))
                target4=node.getName();
        }

        assertEquals("Relat1 name error", retrieved.getRelationshipTemplateList().get(0).getName(), source1+"to"+target1);
        assertEquals("Relat2 name error", retrieved.getRelationshipTemplateList().get(1).getName(), source2+"to"+target2);
        assertEquals("Relat3 name error", retrieved.getRelationshipTemplateList().get(2).getName(), source3+"to"+target3);
        assertEquals("Relat4 name error", retrieved.getRelationshipTemplateList().get(3).getName(), source4+"to"+target4);

        System.out.println("Phase B.5 -- Deleting graph.");
        Status resp = client.deleteTopologyTemplate(response.getTopologyTemplate().getId());
        assertEquals("Error while deleting simpleTestTemplate", true, resp.getSuccess());    


        System.out.println("Test B completed.\n");
        return;
    }

    @Test
    public void test2Update() {
        System.out.println("\nTest C: Update.");

        //Creating a test graph on remote repository
        System.out.println("Phase C.1 -- Creating a test graph.");
        NewTopologyTemplate response = client.createTopologyTemplate(simpleTestTemplate);
        assertNotNull("Returned a NULL graph", response);
        assertEquals(true, response.getSuccess());
        assertEquals("Error report: " + response.getErrorMessage(), "", response.getErrorMessage());

        //Reading remote graph.
        System.out.println("Phase C.2 -- Reading remote graph.");
        TopologyTemplateGrpc retrieved = client.getTopologyTemplate(response.getTopologyTemplate().getId());
        assertNotNull("Retrieved a NULL graph", retrieved);
        assertEquals(retrieved.getId(), response.getTopologyTemplate().getId());

        //Updating a TopologyTemplateGrpc
        System.out.println("Phase C.3 -- Updating a test graph.");
        TopologyTemplateGrpc.Builder templ = TopologyTemplateGrpc.newBuilder();
        List<NodeTemplateGrpc> nodes = new ArrayList<NodeTemplateGrpc>();
        List<RelationshipTemplateGrpc> relats = new ArrayList<RelationshipTemplateGrpc>();

        ToscaConfigurationGrpc node1conf = ToscaConfigurationGrpc.newBuilder().setDescription("node1configuration")
                .setId("15").setConfiguration("[]").build();
        NodeTemplateGrpc node1 = NodeTemplateGrpc.newBuilder().setConfiguration(node1conf).setId("999")
                .setName("webserver1").setType(Type.webserver).build();
        nodes.add(node1);

        ToscaConfigurationGrpc node2conf = ToscaConfigurationGrpc.newBuilder().setDescription("node2configuration")
                .setId("16").setConfiguration("[{\r\n\"protocol\":\"HTTP_REQUEST\",\r\n \"url\":\"www.facebook.com\"\r\n }]").build();
        NodeTemplateGrpc node2 = NodeTemplateGrpc.newBuilder().setConfiguration(node2conf).setId("888")
                .setName("host2").setType(Type.endhost).build();
        nodes.add(node2);

        RelationshipTemplateGrpc rel0 = RelationshipTemplateGrpc.newBuilder().setId("1001")
                .setIdSourceNodeTemplate("999").setIdTargetNodeTemplate("888").setName("webserver1tohost2").build();
        relats.add(rel0);

        RelationshipTemplateGrpc rel1 = RelationshipTemplateGrpc.newBuilder().setId("1002")
                .setIdSourceNodeTemplate("888").setIdTargetNodeTemplate("999").setName("host2towebserver1").build();
        relats.add(rel1);

        TopologyTemplateGrpc newTestTemplate = templ.addAllNodeTemplate(nodes).addAllRelationshipTemplate(relats).setId("9").build();
        NewTopologyTemplate updated = client.updateTopologyTemplate(newTestTemplate, response.getTopologyTemplate().getId());
        assertNotNull("Returned a NULL graph", updated);
        assertEquals(true, updated.getSuccess());
        assertEquals("Error report: " + updated.getErrorMessage(), "", updated.getErrorMessage());

        //Reading remote graph.
        System.out.println("Phase C.4 -- Reading remote graph.");
        TopologyTemplateGrpc retrieved2 = client.getTopologyTemplate(response.getTopologyTemplate().getId());
        assertNotNull("Retrieved a NULL graph", retrieved2);
        assertEquals(retrieved2.getId(), response.getTopologyTemplate().getId());

        //Nodes checking
        System.out.println("Phase C.5 -- Checking updated graph's nodes.");
        assertEquals(retrieved2.getNodeTemplateCount(), 2);
        assertEquals("Node1 name error", updated.getTopologyTemplate().getNodeTemplateList().get(0).getName(),
                retrieved2.getNodeTemplateList().get(0).getName());
        assertEquals("Node2 name error", updated.getTopologyTemplate().getNodeTemplateList().get(1).getName(),
                retrieved2.getNodeTemplateList().get(1).getName());

        //Relationships checking
        System.out.println("Phase C.6 -- Checking updated graph's relationships.");
        assertEquals(retrieved2.getRelationshipTemplateCount(), 2);
        String source1=null, target1=null;
        String source2=null, target2=null;
        for (NodeTemplateGrpc node : retrieved2.getNodeTemplateList()){
            if(node.getId().equals(retrieved2.getRelationshipTemplateList().get(0).getIdSourceNodeTemplate()))
                source1=node.getName();
            if(node.getId().equals(retrieved2.getRelationshipTemplateList().get(0).getIdTargetNodeTemplate()))
                target1=node.getName();
            if(node.getId().equals(retrieved2.getRelationshipTemplateList().get(1).getIdSourceNodeTemplate()))
                source2=node.getName();
            if(node.getId().equals(retrieved2.getRelationshipTemplateList().get(1).getIdTargetNodeTemplate()))
                target2=node.getName();
        }

        assertEquals("Relat1 name error", retrieved2.getRelationshipTemplateList().get(0).getName(), source1+"to"+target1);
        assertEquals("Relat2 name error", retrieved2.getRelationshipTemplateList().get(1).getName(), source2+"to"+target2);

        System.out.println("Phase C.6 -- Deleting graph.");
        Status resp = client.deleteTopologyTemplate(updated.getTopologyTemplate().getId());
        assertEquals("Error while deleting simpleTestTemplate", true, resp.getSuccess());    

        System.out.println("Test C completed.\n");
        return;
    }


    @Test
    public void test3Verification() {
        System.out.println("\nTest D: Verification.");
        NewTopologyTemplate response = client.createTopologyTemplate(testTemplate);
        if(response == null | response.getSuccess() != true) {
            fail("Test failed, unable to load the graph.");
            return;
        }

        //The Id of the graph on which we are going to perform tests
        String testTemplateId = response.getTopologyTemplate().getId();

        //REACHABILITY test
        System.out.println("Phase 1.1 - Reachability SAT.");
        ToscaPolicy policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.reachability).setSource("host2").setDestination("host1").build();
        ToscaVerificationGrpc result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "SAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        result = null;
        System.out.println("Phase 1.2 - Reachability UNSAT.");
        policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.reachability).setSource("host1").setDestination("antispamNode1").build();
        result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "UNSAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        //ISOLATION test
        result = null;
        System.out.println("Phase 2.1 - Isolation SAT.");
        policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.isolation).setSource("host2").setDestination("host1").setMiddlebox("webserver1").build();
        result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "SAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        System.out.println("Phase 2.2 - Isolation UNSAT.");
        policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.isolation).setSource("host2").setDestination("host1").setMiddlebox("fw").build();
        result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "UNSAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        //TRAVERSAL test
        result = null;
        System.out.println("Phase 3.1 - Traversal SAT.");
        policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.traversal).setSource("host2").setDestination("host1").setMiddlebox("fw").build();
        result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "SAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        System.out.println("Phase 3.2 - Traversal UNSAT.");
        policy = ToscaPolicy.newBuilder().setIdTopologyTemplate(testTemplateId)
                .setType(PolicyType.traversal).setSource("host2").setDestination("webserver1").setMiddlebox("fw").build();
        result = client.verifyPolicy(policy);
        assertNotNull("There was no response", result);
        assertEquals("Unexpected result : " + result.getResult() + " - " + result.getComment(), "UNSAT", result.getResult());
        assertEquals("Error report: " + result.getErrorMessage(), "", result.getErrorMessage());

        Status resp = client.deleteTopologyTemplate(testTemplateId);
        assertEquals("Error while deleting testTemplate", true, resp.getSuccess());

        System.out.println("Test D completed.\n");
        return;
    }


    @Test
    public void test4Deletion() {
        System.out.println("\nTest E: Deletion");
        NewTopologyTemplate templ = client.createTopologyTemplate(testTemplate);    

        if(templ.getSuccess() != true) {
            fail("Unable to create the graph.");
            return;
        }else {
            Status resp = client.deleteTopologyTemplate(templ.getTopologyTemplate().getId());
            assertEquals("Error while deleting testTemplate", true, resp.getSuccess());
        }

        System.out.println("Test E completed.\n");
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

        TopologyTemplateGrpc.Builder templ2 = TopologyTemplateGrpc.newBuilder();
        List<NodeTemplateGrpc> nodes2 = new ArrayList<NodeTemplateGrpc>();
        List<RelationshipTemplateGrpc> relats2 = new ArrayList<RelationshipTemplateGrpc>();
        nodes2.add(node0);
        nodes2.add(node1);
        nodes2.add(node2);
        relats2.add(rel0);
        relats2.add(rel1);
        relats2.add(rel3);
        relats2.add(rel4);

        this.simpleTestTemplate = templ2.addAllNodeTemplate(nodes2).addAllRelationshipTemplate(relats2).setId("1").build();

    }

    /*class NodeTemplateGrpcComparator implements Comparator<NodeTemplateGrpc> {
        public int compare(NodeTemplateGrpc n0, NodeTemplateGrpc n1) {
            return n0.getName().compareTo(n1.getName());
        }
    }

    class RelationshipTemplateGrpcComparator implements Comparator<RelationshipTemplateGrpc> {
        public int compare(RelationshipTemplateGrpc n0, RelationshipTemplateGrpc n1) {
            int source = n0.getIdSourceNodeTemplate().compareTo(n1.getIdSourceNodeTemplate());
            int target = n0.getIdTargetNodeTemplate().compareTo(n1.getIdTargetNodeTemplate());
            return source + target;
        }
    }*/

}
