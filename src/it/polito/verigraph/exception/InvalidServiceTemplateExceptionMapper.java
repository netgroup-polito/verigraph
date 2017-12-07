package it.polito.verigraph.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verigraph.model.ErrorMessage;

@Provider
public class InvalidServiceTemplateExceptionMapper implements ExceptionMapper<InvalidServiceTemplateException> {

    @Override
    public Response toResponse(InvalidServiceTemplateException exception) {
        ErrorMessage errorMessage = new ErrorMessage( exception.getMessage(),
                400,
                "http://localhost:8080/verigraph/api-docs/");
        return Response.status(Status.BAD_REQUEST).entity(errorMessage).build();
    }

}