package it.polito.escape.verify.database;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class DatabaseClass {
	
	private static final DatabaseClass instance = new DatabaseClass();
	
	private ConcurrentHashMap<Long, Graph>	graphs			= new ConcurrentHashMap<>();

	private static String							persistenceFile	= System.getProperty("catalina.base") + "/webapps/verify/json/" + "database.json";
	
	
	protected DatabaseClass(){
		initialize();
		loadDatabase();
	}
	
	private void initialize() {
		graphs = new ConcurrentHashMap<>();
		persistenceFile = System.getProperty("catalina.base") + "/webapps/verify/json/" + "database.json";
	}

	private void loadDatabase() {
		ObjectMapper mapper = new ObjectMapper();
		
		List<Graph> parsedGraphs = null;
		try {
			File databaseFile = new File(persistenceFile);
			parsedGraphs = mapper.readValue(databaseFile, TypeFactory.defaultInstance().constructCollectionType(List.class, Graph.class));
		}
		catch (JsonParseException e) {
			System.out.println("No database to load");
			return;
		}
		catch (JsonMappingException e) {
			System.out.println("No database to load");
			return;
		}
		catch (IOException e) {
			System.out.println("No database to load");
			return;
		}
		
		System.out.println("Loading database...");
		
		
		for (Graph graph : parsedGraphs){
			
			graph.setId(getNumberOfGraphs() + 1);
			
			for (Map.Entry<Long, Node> nodeEntry : graph.getNodes().entrySet()){
				nodeEntry.getValue().setId(nodeEntry.getKey());
				
				for (Map.Entry<Long, Neighbour> neighbourEntry : nodeEntry.getValue().getNeighbours().entrySet()){
					neighbourEntry.getValue().setId(neighbourEntry.getKey());
				}
			}
			
			graphs.put(graph.getId(), graph);
		}
		
		System.out.println("Database loaded!");
		System.out.println(graphs.size() + " added");
	}

	public static DatabaseClass getInstance() {
		return instance;
	}
	
	public ConcurrentHashMap<Long, Graph> getGraphs() {
		return graphs;
	}

	public synchronized int getNumberOfGraphs() {
		return graphs.size();
	}

	public synchronized int getGraphNumberOfNodes(long graphId) {
		Graph graph = graphs.get(graphId);
		if (graph == null)
			return 0;
		Map<Long, Node> nodes = graph.getNodes();
		if (nodes == null)
			return 0;
		return nodes.size();
	}

	public void persistDatabase() {
		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(new File(persistenceFile), graphs);
		}
		catch (JsonGenerationException e) {
			throw new InternalServerErrorException("Unable to persist database due to a JsonGenerationException: "
													+ e.getMessage());
		}
		catch (JsonMappingException e) {
			throw new InternalServerErrorException("Unable to persist database due to a JsonMappingException: "
													+ e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to persist database due to an IOException: "
													+ e.getMessage());
		}

	}
}
