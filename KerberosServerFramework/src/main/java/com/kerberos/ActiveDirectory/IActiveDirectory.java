/**
 * 
 */
package com.kerberos.ActiveDirectory;

import java.io.IOException;
import java.util.Map;

import javax.naming.NamingException;

import com.kerberos.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;

/**
 * @author raunak
 *
 */
public interface IActiveDirectory {

	/**
	 * @param details
	 * @param directoryContext
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	boolean createEntryInGivenContext(EntryDetails details,
			String directoryContext) throws IOException, NamingException;

	/**
	 * @param userId
	 * @return
	 * @throws NamingException
	 * @throws IOException
	 */
	String findPasswordForUser(String userId) throws NamingException, IOException;

	/**
	 * @param serviceName
	 * @return
	 * @throws NamingException
	 * @throws IOException
	 */
	String findSecretKeyPassword(String serviceName) throws NamingException, IOException;

	/**
	 * @param bindContext
	 * @throws NamingException
	 * @throws IOException
	 */
	void listAllUsers(String bindContext) throws NamingException, IOException;

	/**
	 * @param details
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	boolean registerUser(EntryDetails details) throws IOException,
			NamingException;

	/**
	 * @param details
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	boolean registerApp(EntryDetails details) throws IOException,
			NamingException;

	/**
	 * @param loginName
	 * @return
	 * @throws NamingException
	 * @throws IOException
	 */
	String findPasswordForApp(String loginName) throws NamingException,
			IOException;

	/**
	 * @param uid
	 * @param keyType
	 * @throws IOException
	 * @throws NamingException
	 */
	void addSecretKey(String uid, SecretKeyType keyType) throws IOException,
			NamingException;

	/**
	 * @param uid
	 * @param retrieveAttributes
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	Map<String, String> getDataByUID(String uid, String[] retrieveAttributes)
			throws IOException, NamingException;

	/**
	 * @param appUID
	 * @param keyType
	 * @return
	 */
	String createSecretKeyCommonName(String appUID, SecretKeyType keyType);

}
