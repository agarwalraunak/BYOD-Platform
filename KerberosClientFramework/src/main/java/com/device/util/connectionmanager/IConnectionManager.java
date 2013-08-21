package com.device.util.connectionmanager;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.device.exception.RestClientException;
import com.device.util.connectionmanager.ConnectionManagerImpl.ContentType;
import com.device.util.connectionmanager.ConnectionManagerImpl.RequestMethod;

/**
 * This interface provides the utility required for creating <strong>Http Connection</strong> and then, generate Request 
 * and process Response.
 * 
 * 
 * @author raunak
 *
 */
public interface IConnectionManager {

	/**
	 * This method generates <code>HttpServletRequest</code> and returns the processed response
	 * @param <code>String</code> URL to be invoked
	 * @param <code>RequestMethod</code> Request Method for the request
	 * @param <code>ContentType</code> Content Type for the request
	 * @param <code>Class</code> of the Response to be stored in
	 * @param <code>String...</code> input Request parameters
	 * @return <code>Object</code> of Type passed in or null if the input parameter are invalid
	 * @throws IOException
	 * <p>In case there some errors encountered while creating the connection</p>
	 * @throws RestClientException
	 * <p>If the response is not 200. It binds the error in this exception</p>
	 */
	Object generateRequest(String url, RequestMethod requestMethod,	ContentType contentType, Class<?> representation, String... input) throws IOException, RestClientException;
	
	/**
	 * Returns a serialized JSON <code>String</code> for the given object   
	 * @param
	 * <code>Object</code> to be serialized in to Json String
	 * @return <code>String</code> representation of the object or null if the argument is invalid
	 */
	String generateJSONStringForObject(Object object);
	
	/**
	 * This method writes the input data to the <code>HttpURLConnection</code>
	 * @param <code>HttpURLConnection</code>
	 * @param <code>String...</code>
	 * @return HttpURLConnection or null if the input is invalid
	 * @throws IOException
	 * <p>In case if there are any errors while writing the data on the connection</p>
	 */
	HttpURLConnection writeDataToConnection(HttpURLConnection conn,	String... input) throws IOException;
	
	/**
	 * Creates a <code>HttpURLConnection</code> for the given attributes
	 * @param <code>String</code> URL to be invoked
	 * @param <code>ContentType</code> Content Type for the request
	 * @param <code>RequestMethod</code> Request Method for the request
	 * @return HttpURLConnection or null id the input is invalid
	 * @throws IOException
	 */
	HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod) throws IOException;

}
