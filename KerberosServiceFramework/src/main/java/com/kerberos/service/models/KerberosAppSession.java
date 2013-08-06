/**
 * 
 */
package com.kerberos.service.models;

import java.util.Date;


/**
 * @author raunak
 *
 */
public class KerberosAppSession extends KerberosSession {
	
	private TGT tgt;
	
	public KerberosAppSession(){
		this.setCreated(new Date());
	}
	/**
	 * @return the tgt
	 */
	public TGT getTgt() {
		return tgt;
	}

	/**
	 * @param tgt the tgt to set
	 */
	public void setTgt(TGT tgt) {
		this.tgt = tgt;
	}
}
