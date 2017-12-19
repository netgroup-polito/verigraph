package it.polito.verigraph.tosca;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
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
import it.polito.verigraph.tosca.yaml.beans.AntispamNode;
import it.polito.verigraph.tosca.yaml.beans.CacheNode;
import it.polito.verigraph.tosca.yaml.beans.DpiNode;
import it.polito.verigraph.tosca.yaml.beans.EndhostNode;
import it.polito.verigraph.tosca.yaml.beans.EndpointNode;
import it.polito.verigraph.tosca.yaml.beans.FieldModifierNode;
import it.polito.verigraph.tosca.yaml.beans.FirewallNode;
import it.polito.verigraph.tosca.yaml.beans.MailClientNode;
import it.polito.verigraph.tosca.yaml.beans.MailServerNode;
import it.polito.verigraph.tosca.yaml.beans.NatNode;
import it.polito.verigraph.tosca.yaml.beans.NodeTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.RelationshipTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.TopologyTemplateYaml;
import it.polito.verigraph.tosca.yaml.beans.VpnAccessNode;
import it.polito.verigraph.tosca.yaml.beans.VpnExitNode;
import it.polito.verigraph.tosca.yaml.beans.WebClientNode;
import it.polito.verigraph.tosca.yaml.beans.WebServerNode;
import it.polito.neo4j.jaxb.FunctionalTypes;

public class MappingUtils {

	/** model --> tosca_xml*/

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


	private static TNodeTemplate mapNode(Node node){
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


	private static TRelationshipTemplate mapRelationship(Graph graph, Node sourceNode, Neighbour neigh) {
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


	private static it.polito.verigraph.tosca.classes.Configuration mapModelConfiguration(Configuration conf) {
		it.polito.verigraph.tosca.classes.Configuration configuration = new it.polito.verigraph.tosca.classes.Configuration();

		configuration.setConfID(conf.getId());
		configuration.setConfDescr(conf.getDescription());
		
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(it.polito.verigraph.tosca.classes.Configuration.class, new XmlAntispamDeserializer());
		mapper.registerModule(module);
		
		
		try {
			configuration = mapper.readValue(conf.getConfiguration().asText(), it.polito.verigraph.tosca.classes.Configuration.class);
			configuration.setConfID(conf.getId());
			configuration.setConfDescr(conf.getDescription());
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return configuration;
	}


	/** tosca_xml --> model */

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


	private static Node mapNodeTemplate(TNodeTemplate nodeTemplate) {
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


	private static void mapRelationshipTemplates(Map<Long, Node> nodes, List<TRelationshipTemplate> relationshipTemplates) {
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

	private static Configuration mapToscaConfiguration(it.polito.verigraph.tosca.classes.Configuration configuration) {
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



	/** model --> tosca_yaml */

	public static ServiceTemplateYaml mapGraphYaml(Graph graph) {
		ServiceTemplateYaml serviceTemplate = new ServiceTemplateYaml();
		TopologyTemplateYaml topologyTemplate = new TopologyTemplateYaml();
		topologyTemplate.setNode_templates(new HashMap<String,NodeTemplateYaml>());
		topologyTemplate.setRelationship_templates(new HashMap<String,RelationshipTemplateYaml>());
		serviceTemplate.setMetadata(new HashMap<String,String>());

		for(Node node : graph.getNodes().values()) {
			NodeTemplateYaml nodeTemplate;
			try {
				nodeTemplate = mapNodeYaml(node);
			} catch (IOException e) {
				throw new BadRequestException("Error while mapping a Node in Yaml object.");
			}
			topologyTemplate.getNode_templates().put(String.valueOf(node.getId()), nodeTemplate); //shall we catch NumberFormatException?

			Map<Long,Neighbour> neighMap = node.getNeighbours();
			for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
				Neighbour neigh = myentry.getValue();
				RelationshipTemplateYaml relat = mapRelationshipYaml(node, neigh);
				topologyTemplate.getRelationship_templates().put(String.valueOf(node.getId()), relat); //Neighbour does not have a neighbourID! RelationshipTemplate does, so it is set to sourceNodeID
			}
		}

		serviceTemplate.getMetadata().put("template_id", String.valueOf(graph.getId()));
		serviceTemplate.setTopology_template(topologyTemplate); 
		return serviceTemplate;
	}


	private static NodeTemplateYaml mapNodeYaml(Node node) throws JsonParseException, JsonMappingException, IOException {

		//TODO 
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		JsonNode configNode = node.getConfiguration().getConfiguration();
		//configNode.findValue(fieldName)


		FunctionalTypes nodeType = FunctionalTypes.valueOf(node.getFunctional_type().toUpperCase());
		switch(nodeType) {
		case ANTISPAM:
			AntispamNode antispamNode = new AntispamNode();
			antispamNode.setName(node.getName());
			antispamNode.setType("verigraph.nodeTypes." + 
					node.getFunctional_type().substring(0, 1).toUpperCase() + 
					node.getFunctional_type().substring(1));
			AntispamNode.AntispamConfigurationYaml asConfig = null;
			asConfig = mapper.convertValue(configNode, AntispamNode.AntispamConfigurationYaml.class);
			antispamNode.setProperties(asConfig);
			return antispamNode;

		case ENDHOST:
			EndhostNode endhostNode = new EndhostNode();
			endhostNode.setName(node.getName());
			endhostNode.setType("verigraph.nodeTypes." + 
					node.getFunctional_type().substring(0, 1).toUpperCase() + 
					node.getFunctional_type().substring(1));
			EndhostNode.EndhostConfigurationYaml ehConfig = null;
			ehConfig = mapper.readValue(configNode.asText(), EndhostNode.EndhostConfigurationYaml.class);
			endhostNode.setProperties(ehConfig);
			return endhostNode;

		default:
			System.out.println("boomboom");
			return null;
		}

		/*FIREWALL,
	    ENDHOST,
	    ENDPOINT,
	    ANTISPAM,
	    CACHE,
	    DPI,
	    MAILCLIENT,
	    MAILSERVER,
	    NAT,
	    VPNACCESS,
	    VPNEXIT,
	    WEBCLIENT,
	    WEBSERVER,
	    FIELDMODIFIER;*/
	}


	private static RelationshipTemplateYaml mapRelationshipYaml(Node sourceNode, Neighbour neigh) {
		RelationshipTemplateYaml relationship = new RelationshipTemplateYaml();
		relationship.setProperties(new HashMap<String,String>());

		relationship.setType("verigraph.relationshipType.generic");
		relationship.getProperties().put("source_id", String.valueOf(sourceNode.getId())); //to be catched?
		relationship.getProperties().put("target_id", String.valueOf(neigh.getId()));
		relationship.getProperties().put("name", sourceNode.getName()+"to"+neigh.getName());

		return relationship;
	}



	/** tosca_yaml --> model */

	public static Graph mapTopologyTemplateYaml(ServiceTemplateYaml yamlServiceTemplate) throws BadRequestException {
		Graph graph = new Graph();
		Map<Long, Node> graphNodes = new HashMap<>();
		Map<String, NodeTemplateYaml> nodes = new HashMap<>();
		Map<String, RelationshipTemplateYaml> relats = new HashMap<>();

		nodes = yamlServiceTemplate.getTopology_template().getNode_templates();

		for (Map.Entry<String, NodeTemplateYaml> nodeYamlEntry : nodes.entrySet()) {
			Node node = mapNodeTemplateYaml(nodeYamlEntry.getValue());

			try {
				graphNodes.put(Long.valueOf(nodeYamlEntry.getKey()), node);
			} catch (NumberFormatException e) {
				throw new BadRequestException("The NodeTemplate ID must be a number.");
			}

		}

		// Add Neighbours to the Nodes of the list
		relats = yamlServiceTemplate.getTopology_template().getRelationship_templates();
		mapRelationshipTemplatesYaml(graphNodes, relats);

		// Add Nodes and ID to the graph
		graph.setNodes(graphNodes);
		try {
			graph.setId(Long.valueOf(yamlServiceTemplate.getMetadata().get("template_id")));
		} catch (NumberFormatException ex) {
			throw new BadRequestException("If you want to use this service, the TopologyTemplate ID must be a number.");
		} catch (NullPointerException ex) {} //ID is not mandatory for the user since VeriGraph provides its IDs

		return graph;
	}


	private static Node mapNodeTemplateYaml(NodeTemplateYaml yamlNodeTemplate) {
		Node node = new Node();

		String type =  yamlNodeTemplate.getType().replace("verigraph.nodeTypes.", "").toLowerCase();

		try {
			node.setName(yamlNodeTemplate.getName());
			Configuration conf = mapConfigurationYaml(yamlNodeTemplate);
			node.setConfiguration(conf);
			node.setFunctional_type(type); 
		} catch(NullPointerException ex) {
			throw new BadRequestException("A NodeTemplate has wrong fields representation.");
		}    	
		return node;
	}


	private static void mapRelationshipTemplatesYaml(Map<Long, Node> graphNodes, Map<String, RelationshipTemplateYaml> relats) {
		//updated nodes (update = Node + its Neighbours)
		for(Map.Entry<String, RelationshipTemplateYaml> yamlRelationshipTemplate : relats.entrySet()) {
			try {
				// Retrieve relationship information
				String target = yamlRelationshipTemplate.getValue().getProperties().get("target_id");
				String source = yamlRelationshipTemplate.getValue().getProperties().get("source_id");
				String name = yamlRelationshipTemplate.getValue().getProperties().get("name");

				Neighbour neigh = new Neighbour();
				neigh.setName(name);
				neigh.setId(Long.valueOf(target));

				//Retrieve the Neighbour map of the source Node and add the Neighbour
				Node sourceNode = graphNodes.get(Long.valueOf(source));
				Map<Long,Neighbour> sourceNodeNeighMap = sourceNode.getNeighbours();
				if(sourceNodeNeighMap.containsKey(neigh.getId()))
					throw new BadRequestException("The RelationshipTemplate ID must be unique."); 
				else
					sourceNodeNeighMap.put(neigh.getId(), neigh);
				sourceNode.setNeighbours(sourceNodeNeighMap);

				//Update the Node list
				graphNodes.put(Long.valueOf(source), sourceNode);

			} catch(NullPointerException | NumberFormatException ex) {
				throw new BadRequestException("A RelationshipTemplate has wrong fields representation.");
			}

		}

	}


	private static Configuration mapConfigurationYaml(NodeTemplateYaml node) {
		ObjectMapper mapper = new ObjectMapper();
		NodeTemplateYaml.ConfigurationYaml yamlConfiguration = null;
		Configuration config = new Configuration();
		JsonNode jsonConfiguration = null;

		// Find out node type, retrieve the corresponding configuration and convert it properly
		try {
			if(node instanceof AntispamNode) {
				yamlConfiguration = ((AntispamNode)node).getProperties();
			}else if(node instanceof CacheNode) {
				yamlConfiguration = ((CacheNode)node).getProperties();
			}else if(node instanceof DpiNode) {
				yamlConfiguration = ((DpiNode)node).getProperties();
			}else if(node instanceof EndhostNode) {
				yamlConfiguration = ((EndhostNode)node).getProperties();
			}else if(node instanceof EndpointNode) {
				yamlConfiguration = ((EndpointNode)node).getProperties();
			}else if(node instanceof FieldModifierNode) {
				yamlConfiguration = ((FieldModifierNode)node).getProperties();
			}else if(node instanceof FirewallNode) {
				yamlConfiguration = ((FirewallNode)node).getProperties();
			}else if(node instanceof MailClientNode) {
				yamlConfiguration = ((MailClientNode)node).getProperties();
			}else if(node instanceof MailServerNode) {
				yamlConfiguration = ((MailServerNode)node).getProperties();
			}else if(node instanceof NatNode) {
				yamlConfiguration = ((NatNode)node).getProperties();
			}else if(node instanceof VpnAccessNode) {
				yamlConfiguration = ((VpnAccessNode)node).getProperties();
			}else if(node instanceof VpnExitNode) {
				yamlConfiguration = ((VpnExitNode)node).getProperties();
			}else if(node instanceof WebClientNode) {
				yamlConfiguration = ((WebClientNode)node).getProperties();
			}else if(node instanceof WebServerNode) {
				yamlConfiguration = ((WebServerNode)node).getProperties();
			}else {
				throw new BadRequestException("The provided node is of unknown type, unable to retrieve the node configuration");
			}

			String stringConfiguration = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(yamlConfiguration);
			jsonConfiguration = mapper.readTree(stringConfiguration); 
			config.setConfiguration(jsonConfiguration);
			config.setDescription("");
			config.setId("");

		} catch (NullPointerException | IOException e) {
			throw new BadRequestException("Not able to retrieve a valid configuration");
		} 

		return config;
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
}
