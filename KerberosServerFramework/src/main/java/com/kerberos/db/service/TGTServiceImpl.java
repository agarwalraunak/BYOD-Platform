/**
 * 
 */
package com.kerberos.db.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.db.dao.ITGTDAO;
import com.kerberos.db.model.ServiceTicket;
import com.kerberos.db.model.TGT;

/**
 * @author raunak
 *
 */
public class TGTServiceImpl implements ITGTService{
	
	private @Autowired ITGTDAO tgtDAO;
	private @Autowired IServiceTicketService iServiceTicketService;
	
	@Override
	public void saveTGT(TGT tgt){
		tgtDAO.persist(tgt);
	}
	
	@Override
	public TGT findTGTForSessionKey(String sessionKey){
		return tgtDAO.findTGTforSessionKey(sessionKey);
	}
	
	@Override
	public TGT findActiveTGTForUsername(String username){
		return tgtDAO.findActiveTGTForUsername(username);
	}

	@Override
	public void merge(TGT tgt){
		tgtDAO.merge(tgt);
	}

	@Override
	public void deactiveTGT(TGT tgt) {
		tgt.setIsActive(false);
		//deactive all the service tickets related to the tgt
		for (ServiceTicket serviceTicket : tgt.getServiceTickets()) {
			iServiceTicketService.deactivateServiceTicket(serviceTicket);
		}
		merge(tgt);
	}
}
