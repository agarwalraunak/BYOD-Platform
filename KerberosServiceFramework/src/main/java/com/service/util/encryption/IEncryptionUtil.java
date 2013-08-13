/**
 * 
 */
package com.service.util.encryption;

import java.util.Map;

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
	SecretKey generateSecretKey(String encryptionKey);
	
	/**
	 * @param key
	 * @param input
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	String[] encrypt(SecretKey key, String... input);
	
	/**
	 * @param key
	 * @param encryptedString
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	String[] decrypt(SecretKey key, String... encryptedString);
	
	/**
	 * @param attributes
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	boolean validateDecryptedAttributes(String... attributes);

	/**
	 * @param keyBytes
	 * @return
	 */
	SecretKey generateSecretKeyFromBytes(byte[] keyBytes);

	/**
	 * @param key
	 * @param dataMap
	 * @return
	 */
	Map<String, String> encrypt(SecretKey key, Map<String, String> dataMap);

	/**
	 * @param key
	 * @param encData
	 * @return
	 */
	Map<String, String> decrypt(SecretKey key, Map<String, String> encData);

}
