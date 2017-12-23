package it.polito.verigraph.tosca.converter.grpc;

import java.util.List;

import it.polito.verigraph.grpc.tosca.NodeTemplateGrpc;
import it.polito.verigraph.grpc.tosca.RelationshipTemplateGrpc;
import it.polito.verigraph.grpc.tosca.TopologyTemplateGrpc;
import it.polito.verigraph.grpc.tosca.ToscaPolicy;

public class ToscaGrpcUtils {

	/** Default configuration for a Tosca NodeTemplate non compliant with Verigraph types*/
	public static final String defaultConfID = new String("");
	public static final String defaultDescr = new String("Default Configuration");
	public static final String defaultConfig = new String("[]");

	/** Create a ToscaPolicy */
	public static ToscaPolicy createToscaPolicy(String src, String dst, String type, String middlebox, String idTopologyTemplate) throws IllegalArgumentException{
		if(!validMiddlebox(type, middlebox))
			throw new IllegalArgumentException("Not valid middlebox valid with this type");
		ToscaPolicy.Builder policy = ToscaPolicy.newBuilder();
		policy.setIdTopologyTemplate(idTopologyTemplate);
		if(src != null)
			policy.setSource(src);
		else{
			throw new IllegalArgumentException("Please insert a valid source field");
		}
		if(dst != null)
			policy.setDestination(dst);
		else{
			throw new IllegalArgumentException("Please insert a valid destination field");
		}
		if(type != null)
			policy.setType(ToscaPolicy.PolicyType.valueOf(type));
		else{
			throw new IllegalArgumentException("Please insert a valid type field");
		}
		return policy.build();
	}

	/** Validate a middlebox */
	public static boolean validMiddlebox(String type, String middlebox) {
		if(type == null)
			return false;
		if(type.equals("reachability") && (middlebox == null || middlebox.equals("")))
			return true;
		if(type.equals("isolation") && !(middlebox == null || middlebox.equals("")))
			return true;
		if(type.equals("traversal") && !(middlebox == null || middlebox.equals("")))
			return true;
		return false;
	}

	public static void printTopologyTemplates(List<TopologyTemplateGrpc> topologyList) {
		for(TopologyTemplateGrpc g : topologyList) {
			System.out.println("* TopologyTemplate id: " + g.getId());
			for (NodeTemplateGrpc n: g.getNodeTemplateList())
				System.out.println(" \tNodeTemplate id:" + n.getId() + " name:" + n.getName());
			for (RelationshipTemplateGrpc rel: g.getRelationshipTemplateList())
				System.out.println(" \tRelationshipTemplate id:" + rel.getId() + " name:" + rel.getName());
			System.out.println("** Topology ended");
		}
		System.out.println("\n* All Topology showed");
	}

}
