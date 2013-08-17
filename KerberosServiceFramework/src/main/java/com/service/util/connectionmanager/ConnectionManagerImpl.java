/**
 * 
 */
package com.service.util.connectionmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.service.error.ErrorResponse;
import com.service.exception.RestClientException;

/**
 * @author raunak
 *
 */
@Component
public class ConnectionManagerImpl implements IConnectionManager {
	
	private static Logger log = Logger.getLogger(ConnectionManagerImpl.class);
	
	public enum RequestMethod{
		POST_REQUEST_METHOD("POST"), GET_REQUEST_METHOD("GET");
		
		private String value;
		
		private RequestMethod(String value){
			this.value = value;
		}
		
		public String toString(){
			return value;
		}
	}
	
	public enum ContentType{
		APPLICATION_JSON("application/json"), TEXT_HTML("text/html");
		
		private String value;
		
		private ContentType(String value){
			this.value = value;
		}
		
		public String toString(){
			return value;
		}
	}
	
	/**
	 * Creates and returns a connection with the web service for the given URL
	 * @param urlString
	 * @return
	 * @throws IOException 
	 */
	@Override
	public HttpURLConnection createConnection(String urlString, ContentType contentType, RequestMethod requestMethod)
			throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(requestMethod.toString());
		conn.setRequestProperty("Content-Type", contentType.toString());
		return conn;
	}
	
	/**
	 * Writes JSON String Data to the given connection object
	 * @param conn
	 * @param input
	 * @return
	 * @throws IOException
	 */
	@Override
	public HttpURLConnection writeDataToConnection(HttpURLConnection conn, String... input) throws IOException {
		OutputStream os = conn.getOutputStream();
		for (String s : input)
			os.write(s.getBytes());
		os.flush();
		return conn;
	}

	/**
	 * Gets the data from the Connection and returns the object of the given class type
	 * @param conn
	 * @param representation
	 * @return
	 * @throws IOException
	 * @throws RestClientException 
	 */
	public Object fillGsonWithResponse(HttpURLConnection conn, Class<?> representation) throws IOException, RestClientException{
		
		log.debug("Entering fillGsonWithResponse method");
		
		if (conn == null){
			log.error("Invalid Input parameter to fillGsonWithResponse found");
			return null;
		}
		
		Gson gson = new Gson();
		if (conn.getResponseCode() != 200){
			
			BufferedReader breader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			String error = breader.readLine();
			StringBuilder errorBuilder = new StringBuilder();
			while(error != null){
				errorBuilder.append(error);
				error = breader.readLine();
			}
			error = errorBuilder.toString();
			log.error("Error Code"+conn.getResponseCode()+"\t"+error);
			ErrorResponse response = gson.fromJson(error, ErrorResponse.class);
			throw new RestClientException(response);
		}
		InputStream is = conn.getInputStream();

		InputStreamReader isreader = new InputStreamReader(is);

		Object response = gson.fromJson(isreader, representation);
		is.close();
		
		log.debug("Returning from fillGsonWithResponse method");
		
		return response;
	}
	
	/**
	 * @param url
	 * @param requestMethod
	 * @param contentType
	 * @param representation
	 * @param input
	 * @return Object response
	 * @throws IOException
	 * @throws RestClientException 
	 */
	@Override
	public Object generateRequest(String url, RequestMethod requestMethod, ContentType contentType, Class<?> representation, String... input) throws IOException, RestClientException{
		
		log.debug("Entering generateRequest method");
		
		if (url == null || url.isEmpty() || requestMethod == null || contentType == null || representation == null){
			log.error("Invalid Input parameter to generateRequest found");
			return null;
		}
		
		HttpURLConnection conn = createConnection(url, contentType, requestMethod);
		
		if (input != null && input.length > 0)
			conn = writeDataToConnection(conn, input);
		
		Object response = fillGsonWithResponse(conn, representation);
		
		log.debug("Returning from generateRequest method");
		
		return response;
	}
	
	@Override
	public String generateJSONStringForObject(Object object){
		
		log.debug("Entering generateJSONStringForObject");
		
		if (object == null){
			log.error("Invalid value for input parameter in generateJSONStringForObject");
			return null;
		}
		
		log.debug("Returning from generateJSONStringForObject");
		
		return new Gson().toJson(object);
	}

}
