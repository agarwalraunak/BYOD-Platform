/**
 * 
 */
package com.kerberos.db.service;

import java.util.Date;

import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.model.TGT;

/**
 * @author raunak
 *
 */
public interface IServiceTicketService {

	/**
	 * @param serviceSessionKey
	 * @param tgt
	 * @param serviceName
	 * @param serviceTicketTimeOut
	 */
	void saveServiceTicket(String serviceSessionKey, TGT tgt,
			String serviceName, Date serviceTicketTimeOut);

	/**
	 * @param serviceSessionID
	 * @return
	 */
	ServiceTicket findServiceTicketByID(String serviceSessionID);

	/**
	 * @param tgt
	 * @param serviceName
	 * @return
	 */
	ServiceTicket findActiveServiceTicketByTGTAndServiceName(TGT tgt,
			String serviceName);
	
	/**
	 * @param serviceTicket
	 */
	void merge(ServiceTicket serviceTicket);
	/**
	 * @param serviceTicket
	 */
	void deactivateServiceTicket(ServiceTicket serviceTicket);
}
