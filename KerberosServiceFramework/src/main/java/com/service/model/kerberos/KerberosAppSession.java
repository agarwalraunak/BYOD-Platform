/**
 * 
 */
package com.service.model.kerberos;

import com.service.model.Session;


/**
 * @author raunak
 *
 */
public class KerberosAppSession extends Session{
	
	private TGT tgt;
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
