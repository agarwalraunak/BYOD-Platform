/**
 * 
 */
package com.kerberos.device.util.hashing;

import java.security.NoSuchAlgorithmException;

import com.kerberos.device.util.hashing.HashUtilImpl.HashingTechqniue;

/**
 * @author raunak
 *
 */
public interface IHashUtil {

	
	/**
	 * @param input
	 * @param technique
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] getHash(String input, String technique) throws NoSuchAlgorithmException;
	
	/**
	 * @param input
	 * @return
	 */
	public byte[] stringToByte(String input);
	
	/**
	 * @param input
	 * @return
	 */
	public String bytetoString(byte[] input);
	
	/**
	 * @return
	 */
	public byte[] generateSalt();
	
	/**
	 * @return
	 */
	String getSessionKey();
	
	/**
	 * @param input
	 * @param technique
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	byte[] getHashWithSalt(String input, HashingTechqniue technique, byte[] salt)
			throws NoSuchAlgorithmException;
}
