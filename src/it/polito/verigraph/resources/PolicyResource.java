package it.polito.verigraph.resources;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.ErrorMessage;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.service.NodeService;
import it.polito.verigraph.model.Policy;
import it.polito.verigraph.model.Restrictions;
import it.polito.verigraph.service.PolicyService;

@Api( hidden= true, value = "", description = "Manage policies" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PolicyResource {
    PolicyService policyService = new PolicyService();

    @GET
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns all policies of a given graph",
            notes = "Returns an array of policies belonging to a given graph",
            response = Policy.class,
            responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "All the policies have been returned in the message body", response = Policy.class, responseContainer = "List") })
    public List<Policy> getPolicies(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException{
        return policyService.getAllPolicies(graphId);
    }

    @POST
    @ApiOperation(
            httpMethod = "POST",
            value = "Creates a policy in a given graph",
            notes = "Creates a single policy for a given graph",
            response = Response.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid policy supplied", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 201, message = "Policy successfully created", response = Policy.class)})
    public Response addPolicy(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "New policy object", required = true) Policy policy,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyInvalidIdException {
        Policy newPolicy = policyService.addPolicy(graphId, policy);
        String newId = String.valueOf(newPolicy.getId());
        URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
        return Response.created(uri)
                .entity(newPolicy)
                .build();
    }

    @GET
    @Path("{policyId}")
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns a policy of a given graph",
            notes = "Returns a single policy of a given graph",
            response = Node.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or policy id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or policy not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "The requested policy has been returned in the message body", response = Policy.class)})
    public Policy getPolicy(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Policy id", required = true) @PathParam("policyId") long policyId,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException{
        Policy policy = policyService.getPolicy(graphId, policyId);
        policy.addLink(getUriForSelf(uriInfo, graphId, policy), "self");
        return policy;
    }

    @PUT
    @Path("{policyId}/restrictions")
    @ApiOperation(
            httpMethod = "PUT",
            value = "Adds/edits the restrictions to a policy of a given graph",
            notes = "Sets the restrictions for the policy. The available types of restrictions are: selection, set, sequence and list.")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or policy id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or policy not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Restrictions updated for the requested policy", response=Restrictions.class)})
    public Restrictions addPolicyRestrictions(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Policy id", required = true) @PathParam("policyId") long policyId,
            @ApiParam(value = "Policy restrictions", required = true) Restrictions policyRestrictions,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException, MyInvalidIdException{

        Restrictions restr = policyService.addPolicyRestrictions(graphId, policyId, policyRestrictions);
        return restr;
    }


    @PUT
    @Path("{policyId}")
    @ApiOperation(
            httpMethod = "PUT",
            value = "Edits a policy of a given graph",
            notes = "Edits a single policy of a given graph",
            response = Policy.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid policy object", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Invalid graph and/or policy id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or policy not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Policy edited successfully", response = Policy.class)})
    public Policy updatePolicy(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Policy id", required = true) @PathParam("policyId") long policyId,
            @ApiParam(value = "Updated policy object", required = true) Policy policy) throws JAXBException, IOException, MyInvalidIdException{
        policy.setId(policyId);
        return policyService.updatePolicy(graphId, policy);
    }

    @DELETE
    @Path("{policyId}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Deletes a policy of a given graph",
            notes = "Deletes a single policy of a given graph")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or policy id", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 204, message = "Policy successfully deleted")})
    public void deletePolicy(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Policy id", required = true) @PathParam("policyId") long policyId) throws JsonParseException, JsonMappingException, JAXBException, IOException{
        policyService.removePolicy(graphId, policyId);
    }

    private String getUriForSelf(UriInfo uriInfo, long graphId, Policy policy) {
        String uri = uriInfo.getBaseUriBuilder()
                .path(GraphResource.class)
                .path(GraphResource.class, "getPolicyResource")
                .resolveTemplate("graphId", graphId)
                .path(Long.toString(policy.getId()))
                .build()
                .toString();
        return uri;
    }
}
