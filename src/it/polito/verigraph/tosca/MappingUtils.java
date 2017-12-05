package it.polito.verigraph.tosca;

import java.util.Map;

import javax.xml.namespace.QName;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.classes.Definitions;
import it.polito.verigraph.tosca.classes.TConfiguration;
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
				TRelationshipTemplate relat = mapRelationship(neigh, node);
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
		
		QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaVerigraphDefinition",
		//QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12",
				node.getFunctional_type().substring(0, 1).toUpperCase() + node.
				getFunctional_type().substring(1) + "Type");
		nodeTemplate.setType(type);

		TConfiguration config = mapConfiguration(node.getConfiguration());
		nodeTemplate.getAny().add(config);

		return nodeTemplate;
	}
	
	// Secondo me bisogna settare gli oggetti giusti nei ref al posto di settare semplicemente delle stringhe
	

	public static TRelationshipTemplate mapRelationship(Neighbour neigh, Node sourceNode) {
		TRelationshipTemplate relationship = new TRelationshipTemplate();
		
		SourceElement source = new SourceElement();
		source.setRef(sourceNode.getId());
		TargetElement target = new TargetElement();
		target.setRef(neigh.getId());
		
		relationship.setId(String.valueOf(sourceNode.getId())); //Neighbour does not have a neighbourID! RelationshipTemplate does, so it is set to sourceNodeID
		relationship.setSourceElement(source);
		relationship.setTargetElement(target);
		relationship.setName(sourceNode.getName()+"to"+neigh.getName());
		
		return relationship;
	}
	
	
    public static TConfiguration mapConfiguration(Configuration conf) {
    		TConfiguration configuration = new TConfiguration();
    		
    		configuration.setConfID(conf.getId());
    		configuration.setConfDescr(conf.getDescription());
    		configuration.setJSON(conf.getConfiguration().asText());
    		
    		return configuration;
    }
}
