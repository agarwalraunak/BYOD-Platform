/**
 * 
 */
package com.login.util.encryption;

import java.util.Map;

import javax.crypto.SecretKey;

/**
 * @author raunak
 *
 */
public interface IEncryptionUtil {

	/**
	 * @param encryptionKey
	 * @return
	 */
	SecretKey generateSecretKey(String encryptionKey);
	
	/**
	 * @param key
	 * @param input
	 * @return
	 */
	String[] encrypt(SecretKey key, String... input);
	
	/**
	 * @param key
	 * @param encryptedString
	 * @return
	 */
	String[] decrypt(SecretKey key, String... encryptedString);
	
	/**
	 * @param attributes
	 * @return
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
