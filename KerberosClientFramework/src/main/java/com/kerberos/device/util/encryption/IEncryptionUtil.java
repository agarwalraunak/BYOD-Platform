/**
 * 
 */
package com.kerberos.device.util.encryption;

import javax.crypto.SecretKey;
import javax.management.InvalidAttributeValueException;

/**
 * @author raunak
 *
 */
public interface IEncryptionUtil {

	/**
	 * @param encryptionKey
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	SecretKey generateSecretKey(String encryptionKey) throws InvalidAttributeValueException;
	
	/**
	 * @param key
	 * @param input
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	String[] encrypt(SecretKey key, String... input) throws InvalidAttributeValueException;
	
	/**
	 * @param key
	 * @param encryptedString
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	String[] decrypt(SecretKey key, String... encryptedString) throws InvalidAttributeValueException;
	
	/**
	 * @param attributes
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	boolean validateDecryptedAttributes(String... attributes) throws InvalidAttributeValueException;

	/**
	 * @param keyBytes
	 * @return
	 */
	SecretKey generateSecretKeyFromBytes(byte[] keyBytes);

}
