/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import it.polito.verigraph.grpc.GetRequest;
import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.NewTopologyTemplate;
import it.polito.verigraph.grpc.NodeTemplateGrpc;
import it.polito.verigraph.grpc.RequestID;
import it.polito.verigraph.grpc.Status;
import it.polito.verigraph.grpc.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.ToscaPolicy;
import it.polito.verigraph.grpc.ToscaRequestID;
import it.polito.verigraph.grpc.ToscaTestGrpc;
import it.polito.verigraph.grpc.ToscaVerificationGrpc;
import it.polito.verigraph.grpc.VerigraphGrpc;

public class ToscaClient {

  private final ManagedChannel channel;
  private final VerigraphGrpc.VerigraphBlockingStub blockingStub;

  public ToscaClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
  }

  /** Construct client for accessing toscaVerigraph server using the existing channel. */
  public ToscaClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = VerigraphGrpc.newBlockingStub(channel);
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
      System.out.println("++ Receiving TopologyTemplates...");
      while(receivedTemplates.hasNext()) {
        TopologyTemplateGrpc received = receivedTemplates.next();
        if(received.getErrorMessage().equals("")) {
          System.out.println("++ Correctly received TopologyTemplate --> id:" + received.getId());
          templates.add(received);
        } else
          System.out.println("-- Received a TopologyTemplate with error: " + received.getErrorMessage());
      }
    } catch (StatusRuntimeException ex) {
      System.out.println("-- RPC failed: " + ex.getMessage());
      response_ok = false;
    }
    if(response_ok) {
      System.out.println("++ All TopologyTemplates correctly received.");
      return templates;
    } else {
      return null; //Function returns null in case of error to differentiate from empty list case.
    }
  }


  /** Obtain a TopologyTemplate by ID */
  public TopologyTemplateGrpc getTopologyTemplate(String id) {
    ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
    TopologyTemplateGrpc response = TopologyTemplateGrpc.newBuilder().build();
    try {
      System.out.println("++ Receiving TopologyTemplate...");
      response = blockingStub.getTopologyTemplate(request);
      if(response.getErrorMessage().equals("")){
        System.out.println("++ Received TopologyTemplate --> id:" + response.getId());
        return response;
      } else {
        System.out.println("-- Error: " + response.getErrorMessage());
        return response;
      }
    } catch (StatusRuntimeException ex) {
      System.out.println("-- RPC failed: " + ex.getStatus());
      return TopologyTemplateGrpc.newBuilder().setErrorMessage(ex.getStatus().getDescription()).build();
    }
  }


  /** Creates a new TopologyTemplate, takes in input a TopologyTemplateGrpc  */
  public NewTopologyTemplate createTopologyTemplate(TopologyTemplateGrpc topol) {
    try {
      //Sending new Topology and analyzing response
      System.out.println("++ Sending the new TopologyTemplate...");
      NewTopologyTemplate response = blockingStub.createTopologyTemplate(topol);
      if(response.getSuccess())
        System.out.println("++ TopologyTemplate successfully created with id: "+ response.getTopologyTemplate().getId());
      else
        System.out.println("-- TopologyTemplate creation failed: " + response.getErrorMessage());
      return response;

    } catch (StatusRuntimeException ex) {
      System.out.println("-- RPC failed: " + ex.getStatus());
      return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build();
      //getDescription may be empty
    }
  }


  /** Update a TopologyTemplate, takes in input a TopologyTemplateGrpc and the Topology's ID to be updated*/
  public NewTopologyTemplate updateTopologyTemplate(TopologyTemplateGrpc topol, String id) {
    //Checking if the inserted string is an object
    try {
      Long.valueOf(id);
    } catch (NumberFormatException ex) {
      System.out.println("-- The ID must a number according to Verigraph implementation.");
      return NewTopologyTemplate.newBuilder().setSuccess(false)
              .setErrorMessage("The ID must a number according to Verigraph implementation.").build();
    }

    //Update the topology ID
    TopologyTemplateGrpc.Builder updTopol = TopologyTemplateGrpc.newBuilder();
    try {
      updTopol.setId(id)
      .addAllNodeTemplate(topol.getNodeTemplateList())
      .addAllRelationshipTemplate(topol.getRelationshipTemplateList());
    } catch (Exception ex) {
      System.out.println("-- Error: Incorrect fields implementation.");
      return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage("Error: Incorrect fields implementation.").build();
    }

    //Sending updated Topology and analyzing response
    try {

      System.out.println("++ Sending the updated TopologyTemplate...");
      NewTopologyTemplate response = blockingStub.updateTopologyTemplate(updTopol.build());
      if(response.getSuccess())
        System.out.println("++ TopologyTemplate successfully updated.");
      else
        System.out.println("-- TopologyTemplate not updated: " + response.getErrorMessage());
      return response;

    } catch (StatusRuntimeException ex) {
      System.out.println("-- RPC failed: " + ex.getStatus());
      return NewTopologyTemplate.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build();
    }
  }


  /** Delete a TopologyTemplate by ID */
  public Status deleteTopologyTemplate(String id) {
    try {
      Long.valueOf(id);
    } catch (NumberFormatException ex) {
      System.out.println("-- The ID must a number according to Verigraph implementation.");
      return Status.newBuilder().setSuccess(false)
              .setErrorMessage("The ID must a number according to Verigraph implementation.").build();
    }
    ToscaRequestID request = ToscaRequestID.newBuilder().setIdTopologyTemplate(id).build();
    try {
      System.out.println("++ Sending delete request...");
      Status response = blockingStub.deleteTopologyTemplate(request);
      if(response.getSuccess())
        System.out.println("++ TopologyTemplate successfully deleted.");
      else
        System.out.println("-- Error deleting TopologyTemplate : " + response.getErrorMessage());
      return response;

    } catch (StatusRuntimeException ex) {
      System.out.println("-- RPC failed: " + ex.getStatus());
      return Status.newBuilder().setSuccess(false).setErrorMessage(ex.getStatus().getDescription()).build();
    }
  }


  /** VerifyPolicy */
  public ToscaVerificationGrpc verifyPolicy(ToscaPolicy policy){
    ToscaVerificationGrpc response;
    try {
      System.out.println("++ Sending ToscaPolicy...");
      response = blockingStub.verifyToscaPolicy(policy);
      if(!response.getErrorMessage().equals("")){
        System.out.println("-- Error in operation: " + response.getErrorMessage());
      }
      else {
        System.out.println("++ Result: " + response.getResult());
        System.out.println("++ Comment: " + response.getComment());

        for(ToscaTestGrpc test : response.getTestList()){
          System.out.println("++ Traversed nodes:");
          for(NodeTemplateGrpc node : test.getNodeTemplateList()){
            System.out.println("\t Node "+node.getName());
          }
        }
      }
      return response;
    } catch (StatusRuntimeException e) {
      System.out.println("-- RPC failed: " + e.getStatus());
      return ToscaVerificationGrpc.newBuilder().setSuccessOfOperation(false)
              .setErrorMessage(e.getStackTrace().toString()).build();
    }
  }


  //Methods added for backward compatibility with JSON grpc, only create and update methods need to be redefined
  //The reason is that the tosca Grpc converter requires a coherent numbering of IDs while the previous
  //implementations exploits names to identify nodes and can considers IDs as not strictly required attributes.

  public NewGraph createGraph(GraphGrpc gr) {
    NewGraph response;
    try {
      response = blockingStub.createGraph(gr);
      if(response.getSuccess())
        System.out.println("++ TopologyTemplate successfully created with id: "+ response.getGraph().getId());
      else
        System.out.println("-- TopologyTemplate creation failed: " + response.getErrorMessage());
      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("-- RPC failed: " + e.getStatus());
      return NewGraph.newBuilder().setSuccess(false).setErrorMessage(e.getStatus().getDescription()).build();
    }

  }

  public NewGraph updateGraph(long idGraph, GraphGrpc newGraph) {

    GraphGrpc gr = GraphGrpc.newBuilder(newGraph).setId(idGraph).build();
    NewGraph response;
    try {
      response = blockingStub.updateGraph(gr);
      if(response.getSuccess())
        System.out.println("++ TopologyTemplate successfully created with id: "+ response.getGraph().getId());
      else
        System.out.println("-- TopologyTemplate creation failed: " + response.getErrorMessage());
      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("-- RPC failed: " + e.getStatus());
      return NewGraph.newBuilder().setSuccess(false).setErrorMessage(e.getStatus().getDescription()).build();
    }
  }

  public GraphGrpc getGraph(long idGraph) {

    RequestID request = RequestID.newBuilder().setIdGraph(idGraph).build() ;
    try {
      System.out.println("++ Receiving TopologyTemplate...");
      GraphGrpc graph = blockingStub.getGraph(request);
      System.out.println("++ Received TopologyTemplate --> id:" + graph.getId());
      if(!graph.getErrorMessage().equals("")){
        System.out.println("-- Error : " + graph.getErrorMessage());
        return graph;
      }
      return graph;
    } catch (StatusRuntimeException ex) {
      System.err.println("-- RPC failed: " + ex.getStatus());
      return null;
    }
  }


  public List<GraphGrpc> getGraphs() {
    List<GraphGrpc> graphsReceived = new ArrayList<GraphGrpc>();
    GetRequest request = GetRequest.newBuilder().build();
    Iterator<GraphGrpc> graphs;
    try {
      graphs = blockingStub.getGraphs(request);
      System.out.println("++ Receiving TopologyTemplates...");
      while (graphs.hasNext()) {
        GraphGrpc graph = graphs.next();
        if(graph.getErrorMessage().equals("")){
          System.out.println("++ Correctly received graph --> id:" + graph.getId());
          graphsReceived.add(graph);
        }else{
          System.out.println("-- Received a graph with error : " + graph.getErrorMessage());
        }
      }
    } catch (StatusRuntimeException ex) {
      System.err.println("-- RPC failed : " + ex.getStatus());
      return null;
    }
    return graphsReceived;
  }
}
