/**
 * 
 */
package com.kerberos.db.service;

import com.kerberos.db.model.TGT;

/**
 * @author raunak
 *
 */
public interface ITGTService {

	/**
	 * @param tgt
	 */
	void saveTGT(TGT tgt);
	/**
	 * @param sessionKey
	 * @return
	 */
	TGT findTGTForSessionKey(String sessionKey);
	/**
	 * @param username
	 * @return
	 */
	TGT findActiveTGTForUsername(String username);
	/**
	 * @param tgt
	 */
	void merge(TGT tgt);

	/**
	 * @param tgt
	 */
	void deactiveTGT(TGT tgt);
}
