/**
 * 
 */
package com.device.util.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

/**
 * @author raunak
 *
 */
@Component
public class HashUtilImpl implements IHashUtil{
	
	public enum HashingTechqniue{
		SSHA256("SHA-256"), MD5("MD5");
		
		private String value;
		
		private HashingTechqniue(String value){
			this.value = value;
		}
		
		public String toString(){
			return value;
		}
	}


	/**
	 * @param input
	 * @param technique
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@Override
	public byte[] getHash(String input, String technique) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(technique);
		digest.reset();
		byte[] hashedBytes = digest.digest(stringToByte(input));
		return hashedBytes;
	}

	/**
	 * @param input
	 * @return
	 */
	@Override
	public byte[] stringToByte(String input) {
		if (Base64.isBase64(input)) {
			return Base64.decodeBase64(input);

		} else {
			return Base64.encodeBase64(input.getBytes());
		}
	}

	/**
	 * @param input
	 * @return
	 */
	@Override
	public String bytetoString(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	@Override
	public byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * @param input
	 * @param technique
	 * @param salt
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@Override
	public byte[] getHashWithSalt(String input, HashingTechqniue technique, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(technique.value);
		digest.reset();
		digest.update(salt);
		byte[] hashedBytes = digest.digest(stringToByte(input));
		return hashedBytes;
	}
	
	@Override
	public String getSessionKey() {
		return bytetoString(generateSalt());
	}
}
