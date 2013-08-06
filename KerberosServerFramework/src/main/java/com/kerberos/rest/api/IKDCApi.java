/**
 * 
 */
package com.kerberos.rest.api;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.db.model.TGT;
import com.kerberos.rest.api.KDCApiImpl.TicketAttributes;
import com.kerberos.rest.representation.AuthenticationResponse;

/**
 * @author raunak
 *
 */
public interface IKDCApi {

	/**
	 * @param username
	 * @param sessionKey
	 * @param tgtExpiryDate
	 * @throws InvalidAttributeValueException 
	 */
	void createTGT(String username, String sessionKey, Date tgtExpiryDate) throws InvalidAttributeValueException;

	/**
	 * @param loginAppName
	 * @param appPassword
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAttributeValueException 
	 * @throws InvalidAttributeValueException 
	 */
	SecretKey generatePasswordSymmetricKey(String loginAppName,
			String appPassword) throws NoSuchAlgorithmException, InvalidAttributeValueException, InvalidAttributeValueException;

	/**
	 * @param username
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	String fetchPassworkFromDirectoryForUsername(String username)
			throws InvalidAttributeValueException;

	/**
	 * @param encUsername
	 * @param encSessionKey
	 * @param encTGTPacket
	 * @return
	 * @throws InvalidAttributeValueException 
	 */
	AuthenticationResponse createAuthenticationResponse(String encUsername,
			String encSessionKey, String encTGTPacket) throws InvalidAttributeValueException;

	/**
	 * @param tgt
	 * @return
	 */
	boolean checkIfTGTIsValid(TGT tgt) ;

	/**
	 * @param username
	 * @param tgtExpiryDateTimeStr
	 * @param sessionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	Map<TicketAttributes, String> createResponseAttrbiutes(String username,
			Date tgtExpiryDateTimeStr, String sessionKey)
			throws InvalidAttributeValueException;

	
}
