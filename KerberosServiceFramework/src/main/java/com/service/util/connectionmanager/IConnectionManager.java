package com.service.util.connectionmanager;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.service.exception.RestClientException;
import com.service.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.service.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

public interface IConnectionManager {

	/**
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param representation
	 * @param input
	 * @return
	 * @throws IOException
	 * @throws RestClientException
	 */
	Object generateRequest(String url, RequestMethod requestMethod,	ContentType contentType, Class<?> representation, String... input) throws IOException, RestClientException;
	/**
	 * @param object
	 * @return
	 */
	String generateJSONStringForObject(Object object);
	/**
	 * @param conn
	 * @param input
	 * @return
	 * @throws IOException
	 */
	HttpURLConnection writeDataToConnection(HttpURLConnection conn,	String... input) throws IOException;
	/**
	 * @param urlString
	 * @param contentType
	 * @param requestMethod
	 * @return
	 * @throws IOException
	 */
	HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod) throws IOException;

}
