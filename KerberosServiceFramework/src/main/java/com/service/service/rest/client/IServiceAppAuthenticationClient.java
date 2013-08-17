package com.service.service.rest.client;

import com.service.exception.common.InternalSystemException;
import com.service.model.kerberos.ServiceTicket;
import com.service.model.service.ServiceSession;

public interface IServiceAppAuthenticationClient {

	/**
	 * @param url
	 * @param serviceTicket
	 * @return ServiceSession or null if fails to get the Service Application Authenticated
	 * @throws InternalSystemException
	 */
	ServiceSession authenticateAppServiceTicket(String url,
			ServiceTicket serviceTicket) throws InternalSystemException;

}
