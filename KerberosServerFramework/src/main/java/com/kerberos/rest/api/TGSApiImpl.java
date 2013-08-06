package com.kerberos.rest.api;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;
import com.kerberos.dateutil.IDateUtil;
import com.kerberos.db.model.TGT;
import com.kerberos.db.service.ITGTService;
import com.kerberos.encryption.IEncryptionUtil;
import com.kerberos.keyserver.KeyServerUtil;
import com.kerberos.rest.representation.ServiceTicketResponse;

/**
 * @author raunak
 *
 */
public class TGSApiImpl implements ITGSApi{

	private static Logger log = Logger.getLogger(TGSApiImpl.class);
	
	private @Autowired IEncryptionUtil iEncryptionUtil;
	private @Autowired ITGTService iTgtService;
	private @Autowired IDateUtil iDateUtil;
	private @Autowired KeyServerUtil keyServerUtil;
	
	public enum ServiceTicketRequestAttributes{
		USERNAME, SESSION_KEY, SERVICE_NAME, TGT, REQUEST_AUTHENTICATOR;
	}
	
	@Override
	public Map<ServiceTicketRequestAttributes, String> getServiceTicketRequestAttributes(String encTgtPacket, String encAuthenticator, String encServiceName, SecretKey kdcMasterKey)
	throws InvalidAttributeValueException{

		log.debug("Entering getServiceTicketRequestAttributes");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encAuthenticator, encServiceName, encTgtPacket) || kdcMasterKey == null){
			log.error("Invalid input parameter to getServiceTicketRequestAttributes");
			throw new InvalidAttributeValueException("Invalid input parameter to getServiceTicketRequestAttributes can not be null");
		}
		
		String tgtPacket = iEncryptionUtil.decrypt(kdcMasterKey, encTgtPacket)[0];
		log.debug("Validating decrypted TGT Packet");
		if (tgtPacket == null){
			log.debug("Decrypted TGT Packet found null");
			return null;
		}
		
		String[] ticketAttributes = tgtPacket.split(",");
		if (ticketAttributes.length != 3){
			log.debug("TGT is invalid");
			return null;
		}
		
		String username = ticketAttributes[0];
		String sessionKey = ticketAttributes[1];
		String tgtExpiryTime = ticketAttributes[2];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, sessionKey, tgtExpiryTime)){
			log.debug("TGT is invalid");
			return null;
		}
		
		TGT tgt = iTgtService.findTGTForSessionKey(sessionKey);
		//Generating Key from SessionKey
		SecretKey sessionKeySecretKey = iEncryptionUtil.generateSecretKey(sessionKey);
		
		String[] decryptedOutput = iEncryptionUtil.decrypt(sessionKeySecretKey, encServiceName, encAuthenticator);
		String serviceName = decryptedOutput[0];
		String authenticatorString = decryptedOutput[1];
		
		if (!iEncryptionUtil.validateDecryptedAttributes(authenticatorString, serviceName)){
			return null;
		}
		
		if (!validateTGTPacket(tgt, tgtExpiryTime) || !iDateUtil.validateAuthenticator(iDateUtil.generateDateFromString(authenticatorString))){
			return null;
		}
		
		Map<ServiceTicketRequestAttributes, String> requestAttributes = new HashMap<TGSApiImpl.ServiceTicketRequestAttributes, String>();
		
		requestAttributes.put(ServiceTicketRequestAttributes.SESSION_KEY, sessionKey);
		requestAttributes.put(ServiceTicketRequestAttributes.USERNAME, username);
		requestAttributes.put(ServiceTicketRequestAttributes.SERVICE_NAME, serviceName);
		requestAttributes.put(ServiceTicketRequestAttributes.REQUEST_AUTHENTICATOR, authenticatorString);
		
		log.debug("Returning from getServiceTicketRequestAttributes");
		
		return requestAttributes;
	}
	
	@Override
	public SecretKey getSecretKeyForServiceName(String serviceName) throws InvalidAttributeValueException{
		
		log.debug("Entering getSecretKeyFromGivenServiceName");
		if (serviceName == null || serviceName.isEmpty()){
			log.debug("Invalid input parameter to getSecretKeyFromGivenServiceName");
			throw new InvalidAttributeValueException("Input parameter to getSecretKeyFromGivenServiceName can not be null");
		}
		SecretKey serviceKey = null;
		try {
			if (serviceName.equalsIgnoreCase(SecretKeyType.KEY_SERVER.getValue())){
				serviceKey = keyServerUtil.getKeyFromKeyStore(null, SecretKeyType.KEY_SERVER);
			}
			else{
			serviceKey = keyServerUtil.getKeyFromKeyStore(serviceName, SecretKeyType.SERVICE_KEY);
			}
		} catch (InvalidAttributeValueException | NoSuchAlgorithmException
				| UnrecoverableEntryException | KeyStoreException
				| CertificateException | NamingException | IOException e) {
			log.error("Unable to fetch the key from Key Server. Detailed exception attached below:");
			e.printStackTrace();
		}
		return serviceKey;
	}
	
	@Override
	public boolean validateAppTGTPacket(String encAppTGTPacket, SecretKey kdcMasterKey) throws InvalidAttributeValueException{
		
		log.debug("Entering validateAppTGTPacket method");
		
		if (!iEncryptionUtil.validateDecryptedAttributes(encAppTGTPacket) || kdcMasterKey == null){
			throw new InvalidAttributeValueException("Invalid input parameter to validateAppTGTPacket");
		}
		
		String appTgtPacket = iEncryptionUtil.decrypt(kdcMasterKey, encAppTGTPacket)[0];
		if (!iEncryptionUtil.validateDecryptedAttributes(appTgtPacket)){
			return false;
		}
		
		String[] appTicketAttributes = appTgtPacket.split(",");
		String appSessionKey = appTicketAttributes[1];
		String appTgtExpiryTime = appTicketAttributes[2];
		
		TGT appTGT = iTgtService.findTGTForSessionKey(appSessionKey);
		
		if (!validateTGTPacket(appTGT, appTgtExpiryTime)){
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean validateTGTPacket(TGT tgt, String tgtExpiryTime){
		
		//Check if the TGT for the SessionKey exists
		//Check if TGT has expired or not
		Date tgtTimeOutDate = iDateUtil.generateDateFromString(tgtExpiryTime);
		if(tgt == null || tgtTimeOutDate.before(new Date())){
			return false;
		}
				
		return true;
	}
	
	@Override
	public ServiceTicketResponse createServiceTicketResponse(String username, String serviceName, String serviceSessionKey, 
			String serviceTicketExpiryString, Date authenticator, SecretKey serviceKey, SecretKey sessionKey) throws InvalidAttributeValueException{
		
		if (!iEncryptionUtil.validateDecryptedAttributes(username, serviceName, serviceSessionKey, serviceTicketExpiryString)
			|| authenticator == null || serviceKey == null || sessionKey == null){
			throw new InvalidAttributeValueException("Invalid input parameter to createServiceTicketResponse");
		}
		
		String serviceTicket = username + ","+ serviceSessionKey+","+serviceTicketExpiryString;
		String serviceEncServiceTicket = iEncryptionUtil.encrypt(serviceKey, serviceTicket)[0];
		String sessionEncServiceTicket = iEncryptionUtil.encrypt(sessionKey, serviceEncServiceTicket)[0];
		
		Date responseAuthenticator = iDateUtil.createResponseAuthenticator(authenticator);
		String responseAuthenticatorStr = iDateUtil.generateStringFromDate(responseAuthenticator);
		
		String[] encAttributes = iEncryptionUtil.encrypt(sessionKey, responseAuthenticatorStr, serviceSessionKey, serviceName);
		String encAuthenticator = encAttributes[0];
		String encServiceSessionKey = encAttributes[1];
		String encServiceName = encAttributes[2];
		
		ServiceTicketResponse response = new ServiceTicketResponse();
		
		response.setEncServiceName(encServiceName);
		response.setEncAuthenticator(encAuthenticator);
		response.setEncServiceSessionID(encServiceSessionKey);
		response.setEncServiceTicket(sessionEncServiceTicket);
		
		return response;
	}
}
