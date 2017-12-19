/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.verigraph.model.ErrorMessage;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.providers.DefinitionsProvider;
import it.polito.verigraph.providers.YamlReaderProvider;
import it.polito.verigraph.providers.YamlWriterProvider;
import it.polito.verigraph.resources.beans.VerificationBean;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.service.TopologyTemplateService;
import it.polito.verigraph.service.VerificationService;
import it.polito.verigraph.tosca.MappingUtils;
import it.polito.verigraph.tosca.classes.Definitions;
import it.polito.verigraph.tosca.yaml.beans.ServiceTemplateYaml;
import org.glassfish.jersey.server.ResourceConfig;

import javax.print.attribute.standard.Media;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@Path("/graphs")
@Api(value = "/graphs", description = "Manage graphs")
//@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
//@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
public class GraphResource /*extends ResourceConfig */{
    TopologyTemplateService topologyTemplateService = new TopologyTemplateService();
    GraphService graphService = new GraphService();
    VerificationService verificationService = new VerificationService();

//    // TODO Solve Swagger issues (when generating documentation an exception is thrown)
//    public GraphResource() {
//        register(DefinitionsProvider.class); // registering a resolver for TOSCA XML
//        register(YamlReaderProvider.class); // registering a resolver for TOSCA YAML
//        register(YamlWriterProvider.class);
//    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
    @ApiOperation(httpMethod = "GET",
            value = "Returns all graphs",
            notes = "Returns an array of graphs",
            response = Response.class,
            responseContainer = "List")
    @ApiResponses(value =
            {
                    @ApiResponse(
                            code = 200,
                            message = "All the graphs have been returned in the message body",
                            response = Graph.class,
                            responseContainer = "List"),
                    @ApiResponse(
                            code = 200,
                            message = "All the graphs have been returned in the message body",
                            response = Definitions.class,
                            responseContainer = "List"),
                    @ApiResponse(
                            code = 200,
                            message = "All the graphs have been returned in the message body",
                            response = Response.class, // BUG
                            responseContainer = "List"),
                    @ApiResponse(
                            code = 500,
                            message = "Internal server error",
                            response = ErrorMessage.class)
            })
    public Response getGraphs(@Context HttpHeaders headers) throws JsonProcessingException, MyNotFoundException {
        if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
            List<Definitions> definitions = topologyTemplateService.getAllTopologyTemplates();
            GenericEntity<List<Definitions>> entity = new GenericEntity<List<Definitions>>(definitions) {
            }; // Anonymous class
            return Response.ok().entity(entity).build();
        } else if (headers.getAcceptableMediaTypes().contains(new MediaType("application", "x-yaml"))) {
            List<ServiceTemplateYaml> yamlServiceList = topologyTemplateService.getAllTopologyTemplatesYaml();
            GenericEntity<List<ServiceTemplateYaml>> entity = new GenericEntity<List<ServiceTemplateYaml>>(yamlServiceList) {};
            return Response.ok().entity(entity).build();
        } else {
            return Response.ok().entity(graphService.getAllGraphs()).build();
        }
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
    @ApiOperation(httpMethod = "POST",
            value = "Creates a graph",
            notes = "Creates a single graph",
            response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Invalid graph supplied", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
		/*	@ApiResponse(code = 201, message = "Graph successfully created", response = Graph.class),
            @ApiResponse(code = 201, message = "Graph successfully created", response = Definitions.class),*/
            @ApiResponse(code = 201, message = "Graph successfully created", response = Response.class)})

    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "User's name", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "User's email", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "id", value = "User ID", required = true, dataType = "long", paramType = "query")
    })
    public Response addGraph(@Context HttpHeaders headers, @ApiParam(value = "New graph object", required = true) Graph graph,
                             @Context UriInfo uriInfo) throws JAXBException, IOException, MyInvalidIdException {

        if (headers.getMediaType().equals(MediaType.APPLICATION_XML_TYPE)) {
            Graph newGraph = graphService.addGraph((Graph) graph);
            Definitions newTopologyTemplate = MappingUtils.mapGraph(newGraph);
            String newId = String.valueOf(newGraph.getId());
            URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
            GenericEntity<Definitions> entity = new GenericEntity<Definitions>(newTopologyTemplate) {
            }; // Anonymous class
            return Response.created(uri).type(MediaType.APPLICATION_XML_TYPE).entity(entity).build();

        } else if (headers.getMediaType().equals(new MediaType("application", "x-yaml"))) {
            Graph newGraph = graphService.addGraph((Graph) graph);
            ServiceTemplateYaml yamlServiceTemplate = MappingUtils.mapGraphYaml(newGraph);
            String newId = String.valueOf(newGraph.getId());
            URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
            GenericEntity<ServiceTemplateYaml> entity = new GenericEntity<ServiceTemplateYaml>(yamlServiceTemplate) {
            };
            return Response.created(uri).type("application/x-yaml").entity(entity).build();

        } else {
            ObjectMapper mapper = new ObjectMapper();
            Graph customGraph = mapper.convertValue(graph, Graph.class);
            Graph newGraph = graphService.addGraph(customGraph);
            String newId = String.valueOf(newGraph.getId());
            URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
            return Response.created(uri).entity(newGraph).build();
        }
    }

    @GET
    @Path("/{graphId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(httpMethod = "GET",
            value = "Returns a graph",
            notes = "Returns a single graph",
            response = Response.class)
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
		/*	@ApiResponse(code = 200, message = "The requested graph has been returned in the message body", response = Graph.class),
			@ApiResponse(code = 200, message = "The requested graph has been returned in the message body", response = Definitions.class),*/
            @ApiResponse(code = 200, message = "The requested graph has been returned in the message body", response = Response.class)})
    public Response getGraph(@Context HttpHeaders headers,
                             @ApiParam(value = "Graph id", required = true)
                             @PathParam("graphId") long graphId,
                             @Context UriInfo uriInfo)
            throws JAXBException, IOException {
        if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
            Definitions topologyTemplate = topologyTemplateService.getTopologyTemplate(graphId);
            GenericEntity<Definitions> entity = new GenericEntity<Definitions>(topologyTemplate) {
            }; // Anonymous class
            return Response.ok().entity(entity).build();
        } else if (headers.getAcceptableMediaTypes().contains("application/x-yaml")) {
            ServiceTemplateYaml yamlServiceTemplate = topologyTemplateService.getTopologyTemplateYaml(graphId);
            GenericEntity<ServiceTemplateYaml> entity = new GenericEntity<ServiceTemplateYaml>(yamlServiceTemplate) {
            };
            return Response.ok().entity(entity).build();
        } else {
            return Response.ok().entity(graphService.getGraph(graphId)).build();
        }
        //        graph.addLink(getUriForSelf(uriInfo, graph), "self");
        //        graph.addLink(getUriForNodes(uriInfo, graph), "nodes");
    }

    @PUT
    @Path("/{graphId}")
    @ApiOperation(httpMethod = "PUT", value = "Edits a graph", notes = "Edits a single graph", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid graph object", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Graph edited successfully", response = Response.class)})
    public Response updateGraph(
            @Context HttpHeaders headers,
            @ApiParam(value = "Graph id", required = true)
            @PathParam("graphId") long id,
            @ApiParam(value = "Updated graph object", required = true) Graph graph)
            throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {

        if (headers.getMediaType().equals(MediaType.APPLICATION_XML_TYPE)) {
            graph.setId(id);
            Graph newGraph = graphService.updateGraph(graph);
            Definitions newTopologyTemplate = MappingUtils.mapGraph(newGraph);
            GenericEntity<Definitions> entity = new GenericEntity<Definitions>(newTopologyTemplate) {
            }; // Anonymous class
            return Response.ok().type(MediaType.APPLICATION_XML_TYPE).entity(entity).build();
        } else if (headers.getMediaType().equals("application/x-yaml")) {
            graph.setId(id);
            Graph newGraph = graphService.updateGraph(graph);
            ServiceTemplateYaml yamlServiceTemplate = MappingUtils.mapGraphYaml(newGraph);
            GenericEntity<ServiceTemplateYaml> entity = new GenericEntity<ServiceTemplateYaml>(yamlServiceTemplate) {
            }; // Anonymous class
            return Response.ok().type("application/x-yaml").entity(entity).build();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            Graph customGraph = mapper.convertValue(graph, Graph.class);
            customGraph.setId(id);
            Graph newGraph = graphService.updateGraph(customGraph);
            return Response.ok().entity(newGraph).build();
        }
    }

    @DELETE
    @Path("/{graphId}")
    @ApiOperation(httpMethod = "DELETE", value = "Deletes a graph", notes = "Deletes a signle graph")
    @ApiResponses(value = {@ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 204, message = "Graph successfully deleted")})
    public void deleteGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id) {
        graphService.removeGraph(id);
    }

    @GET
    @Path("/{graphId}/policy")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/x-yaml"})
    @ApiOperation(httpMethod = "GET",
            value = "Verifies a given policy in a graph",
            notes = "In order to verify a given policy (e.g. 'reachability') all nodes of the desired graph must have a valid configuration.")
    @ApiResponses(value = {
            @ApiResponse(code = 403,
                    message = "Invalid graph id or invalid configuration for source and/or destination node",
                    response = ErrorMessage.class),
            @ApiResponse(code = 404,
                    message = "Graph not found or source node not found or destination node not found or configuration for source and/or destination node not available",
                    response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class)
    })
    public Response verifyGraph(@Context HttpHeaders headers, @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
                                    @ApiParam(value = "'source' and 'destination' must refer to names of existing nodes in the same graph, 'type' refers to the required verification between the two (e.g. 'reachability')", required = true) @BeanParam VerificationBean verificationBean)
            throws MyInvalidDirectionException, JsonParseException, JsonMappingException, JAXBException, IOException {

        Verification verification = verificationService.verify(graphId, verificationBean);

        if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
            // TODO
//            GenericEntity<ToscaXmlVerification> entity = new GenericEntity<ToscaXmlVerification>(MappingUtils.mapVerificationToXml) {}; // Anonymous class
//            return Response.ok().entity(entity).build();
            return null;
        } else if (headers.getAcceptableMediaTypes().contains(new MediaType("application", "x-yaml"))) {
            // TODO
//            GenericEntity<ToscaXmlVerification> entity = new GenericEntity<ToscaXmlVerification>(MappingUtils.mapVerificationToYaml) {}; // Anonymous class
//            return Response.ok().entity(entity).build();
            return null;
        } else {
            return Response.ok().entity(verification).build();
        }
    }

    private String getUriForSelf(UriInfo uriInfo, Graph graph) {
        String uri = uriInfo.getBaseUriBuilder()
                .path(GraphResource.class)
                .path(Long.toString(graph.getId()))
                .build()
                .toString();
        return uri;
    }

    @GET
    @Path("/{graphId}/paths")
    @ApiOperation(httpMethod = "GET", value = "Retrieve all paths between two nodes")
    @ApiResponses(value = {
            @ApiResponse(code = 403,
                message = "Invalid graph id or invalid configuration for source and/or destination node",
                response = ErrorMessage.class),
            @ApiResponse(code = 404,
                message = "Graph not found or source node not found or destination node not found or configuration for source and/or destination node not available",
                response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class)
    })
    public Response getPaths(@Context HttpHeaders headers, @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
                                     @ApiParam(value = "'source' must refer to name of existing nodes in the same graph",
                                             required = true) @QueryParam("source") String srcName,
                                     @ApiParam(value = "'destination' must refer to name of existing nodes in the same graph",
                                             required = true) @QueryParam("destination") String dstName)
            throws MyInvalidDirectionException, JsonParseException, JsonMappingException, JAXBException, IOException {

        List<List<Node>> paths = verificationService.getPaths(graphId, srcName, dstName);

        if (headers.getAcceptableMediaTypes().contains(MediaType.APPLICATION_XML_TYPE)) {
            GenericEntity<Definitions> entity = new GenericEntity<Definitions>(MappingUtils.mapPathsToXml(paths)) {};
            return Response.ok().entity(entity).build();
        } else if (headers.getAcceptableMediaTypes().contains(new MediaType("application", "x-yaml"))) {
            GenericEntity<List<ServiceTemplateYaml>> entity = new GenericEntity<List<ServiceTemplateYaml>>(MappingUtils.mapPathsToYaml(paths)) {};
            return Response.ok().entity(entity).build();
        } else {
            return Response.ok().entity(paths).build();
        }
    }


    private String getUriForNodes(UriInfo uriInfo, Graph graph) {
        String uri = uriInfo.getBaseUriBuilder()
                .path(GraphResource.class)
                .path(GraphResource.class, "getNodeResource")
                // .path(NodeResource.class)
                .resolveTemplate("graphId", graph.getId())
                .build()
                .toString();
        return uri;
    }

    @Path("/{graphId}/nodes")
    public NodeResource getNodeResource() {
        return new NodeResource();
    }
}