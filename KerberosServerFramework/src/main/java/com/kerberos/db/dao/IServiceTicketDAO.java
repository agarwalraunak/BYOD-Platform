/**
 * 
 */
package com.kerberos.db.dao;

import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.model.TGT;

/**
 * @author raunak
 *
 */
public interface IServiceTicketDAO {

	/**
	 * @param serviceTicket
	 */
	void persist(ServiceTicket serviceTicket);
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
}
