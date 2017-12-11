package it.polito.verigraph.tosca;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.classes.Definitions;
import it.polito.verigraph.tosca.classes.TExtensibleElements;
import it.polito.verigraph.tosca.classes.TNodeTemplate;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate.SourceElement;
import it.polito.verigraph.tosca.classes.TRelationshipTemplate.TargetElement;
import it.polito.verigraph.tosca.classes.TServiceTemplate;
import it.polito.verigraph.tosca.classes.TTopologyTemplate;

public class MappingUtils {

	public static Definitions mapGraph(Graph graph) {
		Definitions definitions = new Definitions();
		TServiceTemplate serviceTemplate = new TServiceTemplate();
		TTopologyTemplate topologyTemplate = new TTopologyTemplate();

		for(Node node : graph.getNodes().values()) {
			TNodeTemplate nodeTemplate = mapNode(node);
			topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);

			// RelationshipTemplate mapping
			Map<Long,Neighbour> neighMap = node.getNeighbours();
			for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
				Neighbour neigh = myentry.getValue();
				TRelationshipTemplate relat = mapRelationship(graph, node, neigh);
				topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(relat);
			}
		}

		serviceTemplate.setId(String.valueOf(graph.getId()));
		serviceTemplate.setTopologyTemplate(topologyTemplate);
		definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);

		return definitions;
	}


	public static TNodeTemplate mapNode(Node node){
		TNodeTemplate nodeTemplate = new TNodeTemplate();

		nodeTemplate.setId(String.valueOf(node.getId()));
		nodeTemplate.setName(node.getName());

		//QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaVerigraphDefinition",
		QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12",
				node.getFunctional_type().substring(0, 1).toUpperCase() + node.
				getFunctional_type().substring(1) + "Type");
		nodeTemplate.setType(type);

		it.polito.verigraph.tosca.classes.Configuration config = mapModelConfiguration(node.getConfiguration());
		nodeTemplate.getAny().add(config);

		return nodeTemplate;
	}

	// Secondo me bisogna settare gli oggetti giusti nei ref al posto di settare semplicemente delle stringhe


	public static TRelationshipTemplate mapRelationship(Graph graph, Node sourceNode, Neighbour neigh) {
		TRelationshipTemplate relationship = new TRelationshipTemplate();
		SourceElement source = new SourceElement();
		TargetElement target = new TargetElement();

		Node targetNode = graph.getNodes().get(neigh.getId());

		TNodeTemplate sourceNT = mapNode(sourceNode);
		TNodeTemplate targetNT = mapNode(targetNode);

		source.setRef(sourceNT);
		target.setRef(targetNT);

		relationship.setId(String.valueOf(sourceNode.getId())); //Neighbour does not have a neighbourID! RelationshipTemplate does, so it is set to sourceNodeID
		relationship.setSourceElement(source);
		relationship.setTargetElement(target);
		relationship.setName(sourceNode.getName()+"to"+neigh.getName());

		return relationship;
	}


	public static it.polito.verigraph.tosca.classes.Configuration mapModelConfiguration(Configuration conf) {
		it.polito.verigraph.tosca.classes.Configuration configuration = new it.polito.verigraph.tosca.classes.Configuration();

		configuration.setConfID(conf.getId());
		configuration.setConfDescr(conf.getDescription());
		configuration.setJSON(prettyPrintJsonString(conf.getConfiguration()));

		return configuration;
	}

	public static String prettyPrintJsonString(JsonNode jsonNode) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(jsonNode.toString(), Object.class);
			return System.getProperty("line.separator") + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json) + System.getProperty("line.separator");
		} catch (Exception e) {
			return "Sorry, pretty print didn't work";
		}
	}


	public static Graph mapTopologyTemplate(Definitions definitions) throws DataNotFoundException, BadRequestException {
		Graph graph = new Graph();
		Map<Long, Node> nodes = new HashMap<>();

		List<TExtensibleElements> elements = definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation();

		// Retrieve the list of ServiceTemplate in Definitions
		List<TServiceTemplate> serviceTemplates = elements.stream().filter(p -> p instanceof TServiceTemplate)
				.map(obj -> (TServiceTemplate) obj).collect(Collectors.toList());
		if (serviceTemplates.isEmpty())
			throw new DataNotFoundException("There is no ServiceTemplate into the Definitions object");

		List<TNodeTemplate> nodeTemplates = XmlParsingUtils.obtainNodeTemplates(serviceTemplates.get(0));

		for (TNodeTemplate nodeTemplate : nodeTemplates) {
			Node node = mapNodeTemplate(nodeTemplate);
			nodes.put(Long.valueOf(nodeTemplate.getId()), node);
		}

		// Add Neighbour to the Node of the list
		List<TRelationshipTemplate> relationshipTemplates = XmlParsingUtils.obtainRelationshipTemplates(serviceTemplates.get(0));
		mapRelationshipTemplates(nodes, relationshipTemplates);

		// Add Nodes and ID to the graph
		graph.setNodes(nodes);

		try {
			graph.setId(Long.valueOf(serviceTemplates.get(0).getId()));
		} catch (NumberFormatException ex) {
			throw new BadRequestException("If you want to store your TopologyTemplate on this server, the TopologyTemplate ID must be a number.");
		}

		return graph;
	}


	public static Node mapNodeTemplate(TNodeTemplate nodeTemplate) {
		Node node = new Node();
		
		String toscaType =  nodeTemplate.getType().toString();
		toscaType = toscaType.replace("Type", "").replace("{http://docs.oasis-open.org/tosca/ns/2011/12}", "");
		toscaType = toscaType.toLowerCase();
		
		try {
			node.setId(Long.valueOf(nodeTemplate.getId()));
		} catch(NumberFormatException ex) {
			throw new BadRequestException("The NodeTemplate ID must be a number.");
		}
		try {
			node.setName(nodeTemplate.getName());
			Configuration conf = mapToscaConfiguration(XmlParsingUtils.obtainConfiguration(nodeTemplate));
			node.setConfiguration(conf);
			node.setFunctional_type(toscaType); // replace because the namespace it's included 
		} catch(NullPointerException ex) {
			throw new BadRequestException("The NodeTemplate id:"+node.getId()+" has wrong fields representation.");
		}    	
		return node;
	}


	public static void mapRelationshipTemplates(Map<Long, Node> nodes, List<TRelationshipTemplate> relationshipTemplates) {
		//Map<Long,Node> updNodes = nodes; //new list to be filled with updated Node (update = Node + its Neighbour)
		for(TRelationshipTemplate relationshipTemplate : relationshipTemplates) {
			if (relationshipTemplate != null) {
				try {
					// Retrieve the target Node name and generate a new Neighbour
					TNodeTemplate targetNodeTemplate = (TNodeTemplate) relationshipTemplate.getTargetElement().getRef();
					String neighName = nodes.get(Long.valueOf(targetNodeTemplate.getId())).getName();
					Neighbour neigh = new Neighbour();
					neigh.setName(neighName);
					neigh.setId(Long.valueOf(relationshipTemplate.getId()));

					//Retrieve the Neighbour map of the source Node and add the Neighbour
					TNodeTemplate sourceNodeTemplate = (TNodeTemplate) relationshipTemplate.getSourceElement().getRef();
					Node source = nodes.get(Long.valueOf(sourceNodeTemplate.getId()));
					Map<Long,Neighbour> sourceNodeNeighMap = source.getNeighbours();
					if(sourceNodeNeighMap.containsKey(neigh.getId()))
						throw new BadRequestException("The RelationshipTemplate ID must be unique."); 
					else
						sourceNodeNeighMap.put(neigh.getId(), neigh);
					source.setNeighbours(sourceNodeNeighMap);

					//Update the Node list
					nodes.put(Long.valueOf(sourceNodeTemplate.getId()), source);
				} catch(NullPointerException | NumberFormatException ex) {
					throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
				}
			}
		}
	}

	public static Configuration mapToscaConfiguration(it.polito.verigraph.tosca.classes.Configuration configuration) {
		Configuration conf = new Configuration();
		
		JsonNode rootNode = null;
		try {
			if (configuration.getConfID() != null)
				conf.setId(configuration.getConfID());
			else
				conf.setId("");
			if (configuration.getConfDescr() != null)
				conf.setDescription(configuration.getConfDescr());
			else
				conf.setDescription("");
			
			ObjectMapper mapper = new ObjectMapper();	        
			try {
				if ("".equals(configuration.getJSON()))
					rootNode = mapper.readTree("[]");
				else
					rootNode = mapper.readTree(configuration.getJSON());
			} catch (NullPointerException e) {
				conf.setConfiguration(mapper.readTree("[]"));
				return conf; // Controllare mapping Angelo
			}
		} catch (IOException e) {
			throw new BadRequestException("NodeTemplate configuration is invalid.");
		}
		conf.setConfiguration(rootNode);
		return conf;
	}
}
