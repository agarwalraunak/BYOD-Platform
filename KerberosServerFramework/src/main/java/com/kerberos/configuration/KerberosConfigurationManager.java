/**
 * 
 */
package com.kerberos.configuration;

/**
 * @author raunak
 *
 */
public class KerberosConfigurationManager {
	
	private int TGT_TIME_OUT;
	private int SERVICE_TICKET_TIME_OUT;
	
	public KerberosConfigurationManager(int TGT_TIME_OUT, int SERVICE_TICKET_TIME_OUT){
		this.TGT_TIME_OUT = TGT_TIME_OUT;
		this.SERVICE_TICKET_TIME_OUT = SERVICE_TICKET_TIME_OUT;
	}
	
	/**
	 * @return the tGT_TIME_OUT
	 */
	public int getTGT_TIME_OUT() {
		return TGT_TIME_OUT;
	}
	/**
	 * @return the sERVICE_TICKET_TIME_OUT
	 */
	public int getSERVICE_TICKET_TIME_OUT() {
		return SERVICE_TICKET_TIME_OUT;
	}
	
	
	

}
