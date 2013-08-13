/**
 * 
 */
package com.kerberos.util.keystoreutil;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;

/**
 * @author raunak
 *
 */
public interface JavaKeyStoreUtil {

	SecretKey getKey(String Alias, char[] password)
			throws NoSuchAlgorithmException, UnrecoverableEntryException,
			KeyStoreException, CertificateException, IOException;

	void storeKey(SecretKey mySecretKey, String Alias, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, IOException;

	void listAllKeys(KeyStore ks);

}
