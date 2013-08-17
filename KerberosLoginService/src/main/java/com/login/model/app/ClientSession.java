package com.login.model.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import com.login.model.Session;

public class ClientSession extends Session{
	
	private String clientIP;
	private List<Request> requestList;
	private String loginName;
	private List<Date> authenticators;
	private Date expiryTime;
	
	public ClientSession() {
		requestList = new ArrayList<>();
		authenticators = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, 2);
		expiryTime = calendar.getTime();
	}
	
	/**
	 * @return the clientIP
	 */
	public String getClientIP() {
		return clientIP;
	}
	/**
	 * @param clientIP the clientIP to set
	 */
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	/**
	 * @return the requestList
	 */
	public List<Request> getRequestList() {
		return requestList;
	}
	/**
	 * @param requestList the requestList to set
	 */
	public void setRequestList(List<Request> requestList) {
		this.requestList = requestList;
	}
	
	/**
	 * @param request
	 */
	public void addRequest(Request request){
		requestList.add(request);
	}
	
	/**
	 * @param path
	 * @param requestAuthenticator
	 * @return
	 */
	public Request createRequest(String path, Date requestAuthenticator){
		
		Request request = new Request();
		request.setPath(path);
		request.setRequestAuthenticator(requestAuthenticator);
		
		requestList.add(request);
		
		return request;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	/**
	 * @param authenticator
	 * @throws InvalidAttributeValueException
	 */
	public void addAuthenticator(Date authenticator) {
		
		if(authenticator == null){
			return;
		}
		authenticators.add(authenticator);
	}
	
	/**
	 * @param authenticator
	 * @return
	 * @throws InvalidAttributeValueException
	 */
	public boolean validateAuthenticator(Date authenticator) {
		
		if(authenticator == null){
			return false;
		}
		
		Date lastAuthenticator;
		if (authenticators.size() > 0)
			lastAuthenticator = authenticators.get(authenticators.size()-1);
		else
			return true;
		
		if (((new Date().getTime() - authenticator.getTime()) > 5000 * 60) && authenticator.getTime() - lastAuthenticator.getTime() != 60000) {
			return false;
		}
		return true;
	}

	/**
	 * @return the expiryTime
	 */
	public Date getExpiryTime() {
		return expiryTime;
	}

	/**
	 * @param expiryTime the expiryTime to set
	 */
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
	
}
