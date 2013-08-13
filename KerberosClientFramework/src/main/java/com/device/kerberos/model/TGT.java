/**
 * 
 */
package com.device.kerberos.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author raunak
 *
 */
public class TGT {

	private String tgtPacket;
	private Map<String, ServiceTicket> serviceTickets;
	private Date created;
	
	/**
	 * @param tgtPacket
	 */
	public TGT(String tgtPacket){
		this.tgtPacket = tgtPacket;
		serviceTickets = new HashMap<String, ServiceTicket>();
		created = new Date();
	}
	
	/**
	 * @return the tgtPacket
	 */
	public String getTgtPacket() {
		return tgtPacket;
	}
	/**
	 * @param tgtPacket the tgtPacket to set
	 */
	public void setTgtPacket(String tgtPacket) {
		this.tgtPacket = tgtPacket;
	}
	/**
	 * @return the serviceTickets
	 */
	public Map<String, ServiceTicket> getServiceTickets() {
		return serviceTickets;
	}
	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}
	
	/**
	 * @param serviceName
	 * @return
	 */
	public ServiceTicket findActiveServiceTicketByServiceName(String serviceName){
		ServiceTicket ticket =  serviceTickets.get(serviceName);
		if (ticket != null && ticket.getExpiryTime().after(new Date())){
			return ticket;
		}
		return null;
	}
	
	/**
	 * @param serviceSessionID
	 * @param encServiceTicket
	 * @param serviceName
	 * @param expiryTime
	 * @return
	 */
	public ServiceTicket createServiceTicket(String serviceSessionID, String encServiceTicket, String serviceName, Date expiryTime){
		
		ServiceTicket serviceTicket = new ServiceTicket(serviceSessionID, encServiceTicket, serviceName, expiryTime);
		serviceTickets.put(serviceName, serviceTicket);
		
		return serviceTicket;
	}
	
}
