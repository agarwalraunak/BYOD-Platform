package com.kerberos.rest.api;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

import com.kerberos.db.model.TGT;
import com.kerberos.rest.api.TGSApiImpl.ServiceTicketRequestAttributes;
import com.kerberos.rest.representation.ServiceTicketResponse;

public interface ITGSApi {

	ServiceTicketResponse createServiceTicketResponse(String username,
			String serviceName, String serviceSessionKey,
			String serviceTicketExpiryString, Date authenticator,
			SecretKey servicekey, SecretKey sessionKey) throws InvalidAttributeValueException;

	Map<ServiceTicketRequestAttributes, String> getServiceTicketRequestAttributes(String encTgtPacket, String encAuthenticator, String encServiceName, SecretKey kdcMasterKey) throws InvalidAttributeValueException;

	boolean validateAppTGTPacket(String encAppTGTPacket, SecretKey kdcMasterKey) throws InvalidAttributeValueException;

	SecretKey getSecretKeyForServiceName(String serviceName)
			throws InvalidAttributeValueException;

	boolean validateTGTPacket(TGT tgt, String tgtExpiryTime);

}
