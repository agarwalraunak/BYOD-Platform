package com.login.util.connectionmanager;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.login.exception.RestClientException;
import com.login.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.login.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

public interface IConnectionManager {

	/**
	 * @param url: String URL to be called
	 * @param requestMethod: Request Method for the request
	 * @param contentType: Content Type for the request
	 * @param representation: Return Type of the request
	 * @param input: String... Request parameters
	 * @return Object of Type passed in or null if the input parameter are invalid
	 * @throws IOException
	 * @throws RestClientException: If the response is not 200. It binds the error in this exception
	 */
	Object generateRequest(String url, RequestMethod requestMethod,	ContentType contentType, Class<?> representation, String... input) throws IOException, RestClientException;
	/**
	 * @param object
	 * @return JSON String representation of the object or null if the argument is invalid
	 */
	String generateJSONStringForObject(Object object);
	/**
	 * @param conn: HttpURLConnection
	 * @param input: String...
	 * @return HttpURLConnection or null if the input is invalid
	 * @throws IOException
	 */
	HttpURLConnection writeDataToConnection(HttpURLConnection conn,	String... input) throws IOException;
	/**
	 * @param urlString
	 * @param contentType
	 * @param requestMethod
	 * @return HttpURLConnection or null id the input is invalid
	 * @throws IOException
	 */
	HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod) throws IOException;

}
