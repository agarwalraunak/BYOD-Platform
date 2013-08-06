/**
 * 
 */
package com.kerberos.db.dao;

import com.kerberos.db.model.TGT;

/**
 * @author raunak
 *
 */
public interface ITGTDAO {

	/**
	 * @param tgt
	 */
	void persist(TGT tgt);
	/**
	 * @param sessionkey
	 * @return
	 */
	TGT findTGTforSessionKey(String sessionkey);
	/**
	 * @param username
	 * @return
	 */
	TGT findActiveTGTForUsername(String username);
	/**
	 * @param tgt
	 */
	void merge(TGT tgt);

}
