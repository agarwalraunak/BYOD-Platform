/**
 * 
 */
package com.kerberos.service.interceptor;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @author raunak
 *
 */
public class SessionManagementFilter implements  ContainerRequestFilter {

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
//		Map<String, Object> properties = request.getProperties();
//		System.out.println();
//		try{
//		AccessServiceRequest accessRequest = request.getEntity(AccessServiceRequest.class);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		MultivaluedMap<String, String> queryParams = request.getQueryParameters();
		
		return request;
	}
	
	

}
