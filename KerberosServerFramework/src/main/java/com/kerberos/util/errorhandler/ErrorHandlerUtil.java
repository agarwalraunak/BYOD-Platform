/**
 * 
 */
package com.kerberos.util.errorhandler;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.kerberos.rest.representation.KerberosRequestRepresentation;

/**
 * @author raunak
 *
 */
@Component
public class ErrorHandlerUtil {
	
	private static Logger log = Logger.getLogger(ErrorHandlerUtil.class);
	
	
	/**
	 * @param responseClass
	 * @return
	 */
//	public KerberosRequestRepresentation generateErrorResponse(Class<? extends KerberosRequestRepresentation> responseClass, int responseCode, String errorMessage){
//		KerberosRequestRepresentation errorResponse = null;
//		try {
//			errorResponse = responseClass.getConstructor().newInstance();
//			errorResponse.setErrorCode(responseCode);
//			errorResponse.setErrorMessage(errorMessage);
//		} catch (InstantiationException | IllegalAccessException
//				| IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//			log.error(e);
//		}
//		log.debug("Returning from generateErrorResponse method");
//		return errorResponse;
//	}

}
