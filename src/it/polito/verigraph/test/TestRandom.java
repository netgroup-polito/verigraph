package it.polito.verigraph.test;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.client.VerifyClient;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.random.GraphGen;
import it.polito.verigraph.random.PolicyGen;
import it.polito.verigraph.random.RandomGenerator;

//perform scalability tests with different scenarios of RandomGenerator
public class TestRandom {
	
	private static Random random;
	public static void main(String[] args) {
		//TestRandom tr = new TestRandom();
		if(args.length==0){
			random = new Random(new Date().getTime());	
		}else{
			random = new Random(Integer.parseInt(args[0]));
		}
		
		try {
			doTest();
		} catch (VerifyClientException e) {
			e.printStackTrace();
		}

	}
	private static void doTest() throws VerifyClientException {
		VerifyClient client = new VerifyClient("http://localhost:8080/verigraph/api");
		//create graph and policies
		GraphGen graph = new GraphGen(random,0);
		Graph g = new Graph();
		g.setNodes(graph.getNodes());
		Graph createdGraph = client.createGraph(g).readEntity(Graph.class);
		
		for (PolicyGen policyGen : graph.getPolicies().values()) {
			Verification result = client.getReachability(createdGraph.getId(), policyGen.getNodesrc().getName(), policyGen.getNodedst().getName());
			System.out.println("Test returned " + result.getResult() + " for "+ policyGen.getName());
		}
		

	}

}
