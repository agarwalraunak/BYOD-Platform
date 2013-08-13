package com.service.util.connectionmanager;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

public interface IConnectionManager {

	Object generateRequest(String url, RequestMethod requestMethod,	ContentType contentType, Class<?> representation, String... input) throws IOException;
	String generateJSONStringForObject(Object object);
	HttpURLConnection writeDataToConnection(HttpURLConnection conn,	String... input) throws IOException;
	HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod) throws IOException;

}
