package com.login.util.connectionmanager;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.management.InvalidAttributeValueException;

import com.login.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.login.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

public interface IConnectionManager {

	Object generateRequest(String url, RequestMethod requestMethod,	ContentType contentType, Class<?> representation, String... input) throws IOException, InvalidAttributeValueException;
	String generateJSONStringForObject(Object object) throws InvalidAttributeValueException;
	HttpURLConnection writeDataToConnection(HttpURLConnection conn,	String... input) throws IOException;
	HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod) throws IOException;

}
