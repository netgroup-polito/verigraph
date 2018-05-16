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
import it.polito.neo4j.jaxb.FunctionalTypes;
//perform scalability tests with different scenarios of RandomGenerator
public class TestRandom {

	private static Random random;
	static Integer seed=(int) new Date().getTime();
	static Integer maxMiddleboxes=6;
	static FunctionalTypes type;
	public static void main(String[] args) {
		//seed = 52545654;
		if (System.getProperty("it.polito.verigraph.test.TestRandom.seed") != null) {
			seed = Integer.parseInt(System.getProperty("it.polito.verigraph.test.TestRandom.seed"));
		}
		if (System.getProperty("it.polito.verigraph.test.TestRandom.middleboxes") != null) {
			maxMiddleboxes = Integer.parseInt(System.getProperty("it.polito.verigraph.test.TestRandom.middleboxes"));
		}
		if (System.getProperty("it.polito.verigraph.test.TestRandom.type") != null) {
			type = FunctionalTypes.valueOf(System.getProperty("it.polito.verigraph.test.TestRandom.type"));
		}
		
		random = new Random(seed);
		System.out.println("Seed: " + seed + " Middleboxes: "+maxMiddleboxes+" Type: "+ type);
		
		
		try {
			doTest();
		} catch (VerifyClientException e) {
			e.printStackTrace();
		}

	}

	private static void doTest() throws VerifyClientException {
		
		// create graph and policies
		//int nodes=4;
		//for (FunctionalTypes type : FunctionalTypes.values()) {
		//FunctionalTypes type = FunctionalTypes.DPI;
			//for(int i=2;i<=nodes;i++){
				VerifyClient client = new VerifyClient("http://localhost:8080/verigraph/api");
				GraphGen graph = new GraphGen(random, 1, 1, maxMiddleboxes,type,true);
				Graph g = new Graph();
				g.setNodes(graph.getNodes());
				Graph createdGraph = client.createGraph(g).readEntity(Graph.class);
				//client.printGraph(g);
				for (PolicyGen policyGen : graph.getPolicies().values()) {
					Verification result = client.getReachability(createdGraph.getId(), policyGen.getNodesrc().getName(),
							policyGen.getNodedst().getName());

					System.out.println("Test returned " + result.getResult() +" "+type);
					System.out.println("Nodes: " + (graph.getNodes().values().size()-2) + " for " + policyGen.getName());
				}
			//}
		//}
		
		
		

	}

}
