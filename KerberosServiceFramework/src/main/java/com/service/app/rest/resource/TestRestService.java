/**
 * 
 */
package com.service.app.rest.resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.app.rest.api.IAccessServiceAPI;
import com.service.app.rest.representation.AccessServiceRequest;
import com.service.app.rest.representation.AccessServiceResponse;
import com.service.rest.exception.common.AuthenticatorValidationException;
import com.service.rest.exception.common.UnauthenticatedAppException;
import com.service.rest.exception.common.UnauthenticatedUserException;

/**
 * @author raunak
 *
 */

@Component
@Path("/test123/")
public class TestRestService {
	
	private @Autowired IAccessServiceAPI iAccessServiceAPI;

	@Path("/restservice/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AccessServiceResponse test(AccessServiceRequest request) throws UnauthenticatedAppException, UnauthenticatedUserException, AuthenticatorValidationException{
		
		
		Map<String, String> decData = iAccessServiceAPI.processAccessServiceRequest(request);
		
		Iterator<String> iterator = decData.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			System.out.println("Key: "+key+" :: Value: "+decData.get(key));
		}
			
		
		Map<String, String> responseData = new HashMap<String, String>();
		responseData.put("raunak", "agarwal");
		return iAccessServiceAPI.generateAccessServiceResponse(request, responseData);
		
	}
	
}

	