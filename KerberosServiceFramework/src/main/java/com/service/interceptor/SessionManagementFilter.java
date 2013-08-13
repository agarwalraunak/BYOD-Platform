/**
 * 
 */
package com.service.interceptor;

import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.service.app.rest.representation.AccessServiceRequest;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @author raunak
 *
 */
public class SessionManagementFilter implements  ContainerRequestFilter {

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
		
		Map<String, Object> properties = request.getProperties();
		System.out.println();
		AccessServiceRequest accessRequest = null;
		try{
		accessRequest = request.getEntity(AccessServiceRequest.class);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return request;
	}
	
	

}
