/**
 * 
 */
package com.login.util.ActiveDirectory;

import java.io.IOException;
import java.util.Map;

import javax.naming.NamingException;

import com.login.util.ActiveDirectory.ActiveDirectoryImpl.SecretKeyType;

/**
 * @author raunak
 *
 */
public interface IActiveDirectory {
	
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
	 * @param appUID
	 * @param keyType
	 * @return
	 */
	String createSecretKeyCommonName(String appUID, SecretKeyType keyType);

	/**
	 * @param uid
	 * @param directoryContext
	 * @param retrieveAttributes
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	Map<String, String> getDataByUID(String uid, String directoryContext,
			String[] retrieveAttributes) throws IOException, NamingException;

	/**
	 * @param userID
	 * @param cn
	 * @param sn
	 * @param appID
	 * @param data
	 * @throws IOException
	 * @throws NamingException
	 */
	void addUserToApplication(String userID, String cn, String sn,
			String appID, Map<String, String> data) throws IOException,
			NamingException;

	String getKEYS_DIRECTORY_CONTEXT();

	String getUSER_DIRECTORY_CONTEXT();

	String getAPPLICATION_DIRECTORY_CONTEXT();

	/**
	 * @param serviceUID
	 * @param userUID
	 * @param attributes
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	Map<String, String> getUserInfoForService(String serviceUID,
			String userUID, String[] attributes) throws IOException,
			NamingException;

}
