package com.service.provider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.service.error.ErrorResponse;
import com.service.exception.common.UnauthenticatedUserException;

@Provider
public class UnauthenticatedUserExceptionMapper implements ExceptionMapper<UnauthenticatedUserException>{

	@Override
	public Response toResponse(UnauthenticatedUserException exception) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorId(exception.getErrorID());
		errorResponse.setErrorMessage(exception.getMessage());
		return Response.status(exception.getErrorID()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
	}

	
}
