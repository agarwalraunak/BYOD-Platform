package com.login.provider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.login.error.ErrorResponse;
import com.login.exception.common.InternalSystemException;

@Provider
public class InternalSystemExceptionMapper implements ExceptionMapper<InternalSystemException>{

	@Override
	public Response toResponse(InternalSystemException exception) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorId(exception.getErrorID());
		errorResponse.setErrorMessage(exception.getMessage());
		return Response.status(exception.getErrorID()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
	}

	
}
