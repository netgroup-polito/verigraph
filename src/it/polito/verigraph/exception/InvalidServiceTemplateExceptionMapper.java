/*******************************************************************************
 * Copyright (c) 2018 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
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