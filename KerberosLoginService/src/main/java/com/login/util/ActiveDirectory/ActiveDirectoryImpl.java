package com.login.util.ActiveDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.login.util.hashing.HashUtilImpl.HashingTechqniue;
import com.login.util.hashing.IHashUtil;
/**
 * @author raunak
 * 
 */
@Component
public class ActiveDirectoryImpl implements IActiveDirectory {

	private final String APPLICATION_DIRECTORY_CONTEXT;
	private final String USER_DIRECTORY_CONTEXT;
	private final String KEYS_DIRECTORY_CONTEXT;
	private @Autowired IHashUtil iHashUtil;
	private static Logger log = Logger.getLogger(ActiveDirectoryImpl.class);
	public enum SecretKeyType{
		SESSION_MANAGEMENT_KEY("SESSION_MANAGEMENT_KEY"), SERVICE_KEY("SERVICE_KEY"),
		KDC_MASTER_KEY("KDC_MASTER_KEY"), KEY_SERVER("KEY_SERVER");
		
		private String value;
		
		SecretKeyType(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}

	
	public ActiveDirectoryImpl(){
		Properties properties = new Properties();
		InputStream inputStream = IActiveDirectory.class.getClassLoader().getResourceAsStream("ApacheConfig.properties");
		try {
			properties.load(inputStream);
			APPLICATION_DIRECTORY_CONTEXT = (String)properties.get("APP_DIRECTORY_CONTEXT");
			USER_DIRECTORY_CONTEXT = (String)properties.get("USER_DIRECTORY_CONTEXT");
			KEYS_DIRECTORY_CONTEXT = (String)properties.get("KEYS_DIRECTORY_CONTEXT");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Initialization for ApacheDSUtil Failed. Error reading configuration File");
		}
	}
	
	
	/**
	 * @return the aPPLICATION_DIRECTORY_CONTEXT
	 */
	@Override
	public String getAPPLICATION_DIRECTORY_CONTEXT() {
		return APPLICATION_DIRECTORY_CONTEXT;
	}


	/**
	 * @return the uSER_DIRECTORY_CONTEXT
	 */
	@Override
	public String getUSER_DIRECTORY_CONTEXT() {
		return USER_DIRECTORY_CONTEXT;
	}


	/**
	 * @return the kEYS_DIRECTORY_CONTEXT
	 */
	@Override
	public String getKEYS_DIRECTORY_CONTEXT() {
		return KEYS_DIRECTORY_CONTEXT;
	}


	/**
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	public DirContext openDirectoryContext() throws IOException, NamingException {
		
		log.debug("Creating connection with the library");
		
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ApacheConfig.properties"); 
		Properties properties = new Properties();
		DirContext ctx = null;
		properties.load(inputStream);
		ctx = new InitialDirContext(properties);
		return ctx;
	}
	
	/**
	 * @param ctx
	 * @throws NamingException
	 */
	public void closeDirectoryContext(DirContext ctx) throws NamingException{
		
		log.debug("Closing the Directory Context");
		
		ctx.close();
	}

	@Override
	public void listAllUsers(String directoryContext) throws NamingException, IOException {
		
		log.debug("Entring list all users method");
		
		DirContext ctx = openDirectoryContext();
		SearchControls searchControls = new SearchControls();
		String[] attributeFilter = { "uid", "cn", "userPassword" };
		searchControls.setReturningAttributes(attributeFilter);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		String filter = "(&(sn=*))";

		NamingEnumeration results = ctx.search(directoryContext, filter, searchControls);
		while (results.hasMore()) {
			SearchResult sr = (SearchResult) results.next();
			Attributes attrs = sr.getAttributes();

			Attribute attr = attrs.get("uid");
			String pass = new String((byte[]) attrs.get("userPassword").get());
		}
	}
	
	@Override
	public boolean registerApp(EntryDetails details) throws IOException, NamingException{
		return createEntryInGivenContext(details, APPLICATION_DIRECTORY_CONTEXT);
	}
	
	@Override
	public boolean registerUser(EntryDetails details) throws IOException, NamingException{
		return createEntryInGivenContext(details, USER_DIRECTORY_CONTEXT);
	}

	public Attributes createBindingAttributes(EntryDetails details){
		Attributes attrs = new BasicAttributes(true);
		attrs.put(new BasicAttribute("objectClass", "inetOrgPerson"));
		Attribute cn = new BasicAttribute("cn");
		Attribute sn = new BasicAttribute("sn");
		Attribute uid = new BasicAttribute("uid");
		Attribute street = new BasicAttribute("street");
		Attribute userPassword = new BasicAttribute("userPassword");
		

		uid.add(details.getUid());
		cn.add(details.getCommonName());
		userPassword.add(details.getUserPassword());
		sn.add(details.getSurName());
		try {
			street.add(iHashUtil.bytetoString(iHashUtil.getHashWithSalt(details.getUserPassword(), HashingTechqniue.SSHA256, iHashUtil.stringToByte(details.getUid()))));
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
			return null;
		}
		
		attrs.put(uid);
		attrs.put(sn);
		attrs.put(cn);
		attrs.put(userPassword);
		attrs.put(street);
		
		return attrs;
	}
	
	/**
	 * @param ctx
	 * @param personDetails
	 * @return true if the person was added successfully! Else False
	 * @throws NamingException 
	 * @throws IOException 
	 */
	public boolean createEntryInGivenContext(EntryDetails details, String directoryContext) throws IOException, NamingException {
		
		DirContext ctx = openDirectoryContext();
		
		Attributes attrs = createBindingAttributes(details);
		
		try {
			((InitialDirContext) ctx).bind("uid=" + details.getUid() + ","+ directoryContext, ctx, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			closeDirectoryContext(ctx);
			return false;
		}
		
		closeDirectoryContext(ctx);
		return true;
	}

	/**
	 * @param ctx
	 * @param loginName
	 * @return the password for the given user
	 * @throws NamingException
	 * @throws IOException 
	 */
	@Override
	public String findPasswordForApp(String loginName) throws NamingException, IOException {

		String filterExpression = "(&(objectClass=*)(uid={0}))";
		Object[] filterArguments = { loginName };
		String returningAttrs[] = { "street" };
		
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(returningAttrs);

		return findPassword(APPLICATION_DIRECTORY_CONTEXT, returningAttrs, filterExpression, filterArguments, constraints);
	}
	
	/**
	 * @param ctx
	 * @param userId
	 * @return the password for the given user
	 * @throws NamingException
	 * @throws IOException 
	 */
	@Override
	public String findPasswordForUser(String userId) throws NamingException, IOException {

		String filterExpression = "(&(objectClass=*)(uid={0}))";
		Object[] filterArguments = { userId };
		String returningAttrs[] = { "street" };
		
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(returningAttrs);

		return findPassword(USER_DIRECTORY_CONTEXT, returningAttrs, filterExpression, filterArguments, constraints);
	}
	
	/**
	 * @param ctx
	 * @param serviceName
	 * @return the secretkey password for the given serviceName. Note, secretkey password is stored in the UserPassword field of the Apache DS
	 * @throws NamingException
	 * @throws IOException 
	 */
	@Override
	public String findSecretKeyPassword(String serviceName) throws NamingException, IOException {
		
		String filterExpression = "(&(objectClass=*)(cn={0}))";
		Object[] filterArguments = { serviceName };
		String returningAttributes[] = { "userPassword" };
		
		SearchControls constraints = new javax.naming.directory.SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		constraints.setReturningAttributes(returningAttributes);
		
		return findPassword(KEYS_DIRECTORY_CONTEXT, returningAttributes, filterExpression, filterArguments, constraints);
	}
	
	/**
	 * @param directoryContext
	 * @param returningAttributes
	 * @param filterExpression
	 * @param filterArguments
	 * @param constraints
	 * @return
	 * @throws IOException
	 * @throws NamingException
	 */
	public String findPassword(String directoryContext, String[] returningAttributes, String filterExpression, Object[] filterArguments, SearchControls constraints) throws IOException, NamingException{
		
		String password = null;
		DirContext ctx = openDirectoryContext();
		NamingEnumeration<SearchResult> searchResult = ctx.search(directoryContext, filterExpression, filterArguments, constraints);
		if (searchResult == null || !searchResult.hasMore()){
			return null;
		}
		SearchResult item = searchResult.next();
		for (String att : returningAttributes) {
			Attributes itemAttr = item.getAttributes();
			if (att.equals("userPassword")) {
				password = (new String((byte[]) itemAttr.get(att).get())).substring(6);
			}
			else if (att.equals("street")) {
				password = (String)itemAttr.get(att).get();
			}
		}
		return password;
	}
	
	@Override
	public void addSecretKey(String uid, SecretKeyType keyType) throws IOException, NamingException {
		if (uid == null || keyType == null){
			throw new RuntimeException(this.getClass().getName()+": Input parameter can not be null in addSecretKey");
		}
		
		//Get the App password stored in the Apache DS 
		String secretKeyPassword = iHashUtil.getSessionKey();
		String surname = keyType.getValue();
		String commonName = createSecretKeyCommonName(uid, keyType);

		DirContext ctx = openDirectoryContext();
		Attributes attrs = new BasicAttributes(true);
		attrs.put(new BasicAttribute("objectClass", "inetOrgPerson"));
		Attribute cn = new BasicAttribute("cn");
		Attribute sn = new BasicAttribute("sn");
		Attribute userPassword = new BasicAttribute("userPassword");
		sn.add(surname);
		cn.add(commonName);
		userPassword.add(secretKeyPassword);

		attrs.put(cn);
		attrs.put(sn);
		attrs.put(userPassword);

		((InitialDirContext) ctx).bind(
			new StringBuilder("cn=").append(commonName).append(",").append(KEYS_DIRECTORY_CONTEXT).toString(), ctx, attrs);
	}
	
	@Override
	public Map<String, String> getDataByUID(String uid, String directoryContext, String[] retrieveAttributes) throws IOException, NamingException{

		DirContext ctx = openDirectoryContext();
		String filterExpr = "(&(objectClass=inetOrgPerson)(uid={0}))";	//Expression to filter the result 
		Object[] filterArgs = {uid};								//Injecting the argument inside the filter expression
		SearchControls control = new SearchControls();
		control.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		control.setReturningAttributes(retrieveAttributes);				//Limiting the content of the result for the given attributes
		NamingEnumeration namingEnum = null;
		
		try {
			Map<String, String> resultMap = new HashMap<String, String>();
			namingEnum = ctx.search(directoryContext, filterExpr, filterArgs, control);	//Search
			while (namingEnum.hasMore()) {
				 SearchResult result = (SearchResult) namingEnum.next();
				 Attributes srAttrs = result.getAttributes();
				 for(String key : retrieveAttributes){
					 if(!key.equals("userPassword")){
						 resultMap.put(key, srAttrs.get(key).get().toString());
					 }
					 else{
						 //Converting the userPassword byte array into a formatted string 
						 String crudeUserPassword = new String((byte[])srAttrs.get(key).get());
						 String userPassword = new StringBuilder(crudeUserPassword).replace(0, 6, "").toString();
						 resultMap.put(key, userPassword);
					 }
				 }
			 }
			closeDirectoryContext(ctx);
			return resultMap;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	@Override
	public String createSecretKeyCommonName(String appUID, SecretKeyType keyType){
		return new StringBuilder(appUID).append(" ").append(keyType.getValue()).toString();
	}
	
	public void createUsersOUEntryInApp(String appID) throws IOException, NamingException{
		
		//Check if the entry for the App UID exists in the Directory 
		//If not throw an exception
		if (getDataByUID(appID, APPLICATION_DIRECTORY_CONTEXT, new String[]{"uid"}) == null){
		//TODO: Throw app does not exist exception
		}
		
		Attributes attrs = new BasicAttributes(true);
		attrs.put(new BasicAttribute("objectClass", "organizationalUnit"));
		DirContext ctx = openDirectoryContext();
		try {
			((InitialDirContext) ctx).bind("ou=users,"+"uid="+appID + ","+ APPLICATION_DIRECTORY_CONTEXT, ctx, attrs);
		} catch (NamingException e) {
			e.printStackTrace();
			closeDirectoryContext(ctx);
		}
		
		closeDirectoryContext(ctx);
	}
	
	public boolean checkIfOUUserExists(String appID) throws NamingException, IOException{
		
		DirContext ctx = openDirectoryContext();
		String filterExpr = "(&(objectClass=organizationalUnit)(ou=users))";	//Expression to filter the result 
		SearchControls control = new SearchControls();
		control.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		NamingEnumeration namingEnum = null;
		
		Map<String, String> resultMap = new HashMap<String, String>();
		namingEnum = ctx.search("uid="+appID + ","+ APPLICATION_DIRECTORY_CONTEXT, filterExpr, null, control);	//Search
		closeDirectoryContext(ctx);
		if (namingEnum.hasMore()){
			return true;
		}
		
		return false;
	}
	
	@Override
	public void addUserToApplication(String userID, String cn, String sn, String appID, Map<String, String> data) throws IOException, NamingException{
		
		//Check if the entry for the App UID exists in the Directory 
		//If not throw an exception
		if (getDataByUID(appID, APPLICATION_DIRECTORY_CONTEXT, new String[]{"uid"}) == null){
			//TODO: Throw app does not exist exception
		}
		
		//Check if the sub-hierarchy for users exists inside the App Entry
		if (!checkIfOUUserExists(appID)){
			createUsersOUEntryInApp(appID);
		}
		
		Attributes attrs = new BasicAttributes(true);
		attrs.put(new BasicAttribute("objectClass", "inetOrgPerson"));
		Attribute uid = new BasicAttribute("uid", userID);
		Attribute cna = new BasicAttribute("cn", cn);
		Attribute sna = new BasicAttribute("sn", sn);
		attrs.put(uid);
		attrs.put(cna);
		attrs.put(sna);
		
		Iterator<String> iterator = data.keySet().iterator();
		String key = null;
		while(iterator.hasNext()){
			key = iterator.next();
			attrs.put(new BasicAttribute(key, data.get(key)));
		}
		
		DirContext ctx = openDirectoryContext();
		
		((InitialDirContext) ctx).bind("uid=" + userID+","+"ou=users"+","+"uid="+appID + ","+ APPLICATION_DIRECTORY_CONTEXT, ctx, attrs);
			closeDirectoryContext(ctx);
		closeDirectoryContext(ctx);
	}
	
	@Override
	public Map<String, String> getUserInfoForService(String serviceUID, String userUID, String[] attributes) throws IOException, NamingException{
		
		return getDataByUID(userUID, "ou=users,uid="+serviceUID+","+APPLICATION_DIRECTORY_CONTEXT, attributes);
	}
}
