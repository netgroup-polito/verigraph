package it.polito.verigraph.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception exception) {
        exception.printStackTrace();
        return Response.status(500).build();
    }
}