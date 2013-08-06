/**
 * 
 */
package com.kerberos.db.service;

/**
 * @author raunak
 *
 */
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.db.dao.IServiceTicketDAO;
import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.model.TGT;

public class ServiceTicketServiceImpl implements IServiceTicketService {

	private @Autowired IServiceTicketDAO iServiceTicketDAO;
	
	@Override
	public void saveServiceTicket(String serviceSessionKey, TGT tgt, String serviceName, Date serviceTicketTimeOut){
		ServiceTicket serviceTicketPojo = new ServiceTicket();
		serviceTicketPojo.setIdentifier(serviceSessionKey);
		serviceTicketPojo.setTgt(tgt);
		serviceTicketPojo.setServiceName(serviceName);
		serviceTicketPojo.setExpiresOn(serviceTicketTimeOut);
		iServiceTicketDAO.persist(serviceTicketPojo);
	}

	@Override
	public ServiceTicket findServiceTicketByID(String serviceSessionID) {
		
		return iServiceTicketDAO.findServiceTicketByID(serviceSessionID);
	}
	
	@Override
	public ServiceTicket findActiveServiceTicketByTGTAndServiceName(TGT tgt, String serviceName){
		return iServiceTicketDAO.findActiveServiceTicketByTGTAndServiceName(tgt, serviceName);
	}

	@Override
	public void merge(ServiceTicket serviceTicket) {
		iServiceTicketDAO.merge(serviceTicket);
	}

	@Override
	public void deactivateServiceTicket(ServiceTicket serviceTicket) {
		serviceTicket.setActive(false);
		iServiceTicketDAO.merge(serviceTicket);
	}
}
