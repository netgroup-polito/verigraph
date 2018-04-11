/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.tosca.converter.xml;

import java.io.IOException;
import java.util.Map;

import javax.xml.namespace.QName;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.tosca.jaxb.Definitions;
import it.polito.tosca.jaxb.TEntityTemplate.Properties;
import it.polito.tosca.jaxb.TNodeTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate;
import it.polito.tosca.jaxb.TRelationshipTemplate.SourceElement;
import it.polito.tosca.jaxb.TRelationshipTemplate.TargetElement;
import it.polito.tosca.jaxb.TServiceTemplate;
import it.polito.tosca.jaxb.TTopologyTemplate;

public class GraphToXml {
    /** model --> tosca_xml*/

    public static Definitions mapGraph(Graph graph) {
        Definitions definitions = new Definitions();
        TServiceTemplate serviceTemplate = mapPathToXml(graph);
        definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(serviceTemplate);

        return definitions;
    }

    // These functions have been split so that they can be reused for obtaining all the paths
    //into a single Definitions (see mapPathsToXml)
    public static TServiceTemplate mapPathToXml(Graph graph) {

        TServiceTemplate serviceTemplate = new TServiceTemplate();
        TTopologyTemplate topologyTemplate = new TTopologyTemplate();

        for(Node node : graph.getNodes().values()) {
            long i = 0;
            TNodeTemplate nodeTemplate = mapNode(node);
            topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(nodeTemplate);

            // RelationshipTemplate mapping
            Map<Long,Neighbour> neighMap = node.getNeighbours();
            for (Map.Entry<Long, Neighbour> myentry : neighMap.entrySet()) {
                Neighbour neigh = myentry.getValue();
                if (graph.getNodes().containsKey(neigh.getId())) {
                    // I have to check that because if I'm mapping a path (and not a graph) I could have
                    //as neighbour a node which is not in the path
                    TRelationshipTemplate relat = mapRelationship(graph, node, neigh, i);
                    topologyTemplate.getNodeTemplateOrRelationshipTemplate().add(relat);
                    i++; //Neighbour does not have a neighbourID! RelationshipTemplate does,
                    //so it is an incremental number for each node
                }
            }
        }

        serviceTemplate.setId(String.valueOf(graph.getId()));
        serviceTemplate.setTopologyTemplate(topologyTemplate);

        return serviceTemplate;
    }


    private static TNodeTemplate mapNode(Node node){
        TNodeTemplate nodeTemplate = new TNodeTemplate();

        nodeTemplate.setId(String.valueOf(node.getId()));
        nodeTemplate.setName(node.getName());

        //QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12/ToscaVerigraphDefinition")
        QName type = new QName("http://docs.oasis-open.org/tosca/ns/2011/12",
                node.getFunctional_type().substring(0, 1).toUpperCase() + node.
                getFunctional_type().substring(1) + "Type");
        nodeTemplate.setType(type);

        it.polito.tosca.jaxb.Configuration config = mapModelConfiguration(node.getConfiguration(),
                node.getFunctional_type().toLowerCase());
        //nodeTemplate.getAny().add(config);
        nodeTemplate.setProperties(new Properties());
        nodeTemplate.getProperties().setAny(config);
        return nodeTemplate;
    }


    private static TRelationshipTemplate mapRelationship(Graph graph, Node sourceNode, Neighbour neigh, long i) {
        TRelationshipTemplate relationship = new TRelationshipTemplate();
        SourceElement source = new SourceElement();
        TargetElement target = new TargetElement();

        Node targetNode = graph.getNodes().get(neigh.getId());

        TNodeTemplate sourceNT = mapNode(sourceNode);
        TNodeTemplate targetNT = mapNode(targetNode);

        source.setRef(sourceNT);
        target.setRef(targetNT);

        relationship.setId(String.valueOf(i));
        relationship.setSourceElement(source);
        relationship.setTargetElement(target);
        relationship.setName(sourceNode.getName()+"to"+neigh.getName());

        return relationship;
    }


    private static it.polito.tosca.jaxb.Configuration mapModelConfiguration(Configuration conf, String type) {
        it.polito.tosca.jaxb.Configuration configuration = new it.polito.tosca.jaxb.Configuration();
        try {
            //We are passing the configuration type to the Deserializer context
            configuration = MappingUtils.obtainToscaConfiguration(conf, type);

            //In Graph, ID and DESCRIPTION are always empty
            //configuration.setConfID(conf.getId());
            //configuration.setConfDescr(conf.getDescription());

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return configuration;
    }

}
