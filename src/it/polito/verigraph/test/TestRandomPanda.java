package it.polito.verigraph.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.z3.Status;

import it.polito.verigraph.client.VerifyClient;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.random.GraphGen;
import it.polito.verigraph.random.GraphGenPanda;
import it.polito.verigraph.random.PolicyGen;
import it.polito.verigraph.random.RandomGenerator;
import it.polito.verigraph.service.VerificationService;
import it.polito.verigraph.solver.GeneratorSolver;
import it.polito.verigraph.solver.Scenario;
import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Paths;
//perform scalability tests with different scenarios of RandomGenerator
public class TestRandomPanda {
	   private VerificationService verificationService = new VerificationService();
	private static Random random;
	static Integer seed=(int) new Date().getTime();
	static Integer maxMiddleboxes=6;
	static FunctionalTypes type;
	public static void main(String[] args) {
		seed = 52545654;
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
		
				GraphGenPanda graph = new GraphGenPanda(random, 1, 1, 1,type,true);
				
				   //Scenario tmp=new Scenario(graph, null);
					//				 tmp.createScenario();
				//IsolationResult res = t.getValue().run(src, dst);
				Graph g = new Graph();
				g.setNodes(graph.getNodes());
				
				
				List<String> list=new ArrayList<String>();
				list.add(graph.clientNodes[0].getName());
				list.add(graph.middleNodes[0].getName());
				list.add(graph.middleNodes[1].getName());
				list.add(graph.middleNodes[2].getName());
				list.add(graph.middleNodes[3].getName());
				list.add(graph.middleNodes[4].getName());
				list.add(graph.middleNodes[5].getName());
				list.add(graph.serverNodes[0].getName());
				
				Scenario tmp=new Scenario(graph, list);
				tmp.createScenarioPanda();
				GeneratorSolver gs=new GeneratorSolver(tmp, list);
				System.out.println(gs.getPaths());
	            gs.genSolver();
	            IsolationResult res = gs.run(graph.clientNodes[0].getName(), graph.serverNodes[0].getName());
	            
	            
	            
	            if (res.result == Status.UNSATISFIABLE){
	            	System.out.println("UNSAT");
	            }else if(res.result == Status.SATISFIABLE){
	                System.out.println("SAT");
	            }else if(res.result == Status.UNKNOWN){
	            	System.out.println("UNKNOWN");
	            } else {
	            	System.out.println("UNKNOWN");
	            }
	}

}
