package com.service.provider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.service.error.ErrorResponse;
import com.service.exception.AppAuthenticationRestService.DecryptedServiceTicketPacketValidationException;

@Provider
public class DecryptedServiceTicketPacketValidationExceptionMapper implements ExceptionMapper<DecryptedServiceTicketPacketValidationException>{

	@Override
	public Response toResponse(DecryptedServiceTicketPacketValidationException exception) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorId(exception.getErrorID());
		errorResponse.setErrorMessage(exception.getMessage());
		return Response.status(exception.getErrorID()).entity(errorResponse).type(MediaType.APPLICATION_JSON).build();
	}

	
}
