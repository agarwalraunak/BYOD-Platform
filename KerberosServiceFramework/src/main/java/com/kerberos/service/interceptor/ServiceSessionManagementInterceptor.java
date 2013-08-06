/**
 * 
 */
package com.kerberos.service.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.kerberos.service.models.KerberosSessionManager;

/**
 * @author HIE Prototype Dev Team
 *
 */
public final class ServiceSessionManagementInterceptor implements Filter{

	private @Autowired KerberosSessionManager kerberosSessionManager;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,	FilterChain chain) throws IOException, ServletException {

		//	chain.doFilter(request, response);	
		HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);

	     try {
	    	 chain.doFilter(request, response);
//	         chain.doFilter(new FilterRequest(request), responseCopier);
	         responseCopier.flushBuffer();
	     } finally {
	         byte[] copy = responseCopier.getCopy();
	         System.out.println(new String(copy, response.getCharacterEncoding())); // Do your logging job here. This is just a basic example.
	     }
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	
	static class FilterRequest extends HttpServletRequestWrapper{

		private @Autowired KerberosSessionManager kerberosSessionManager;
		
		public FilterRequest(ServletRequest request) {
			super((HttpServletRequest) request);
		}
		
		public String getParameter(String paramName) {
    		String value = super.getParameter(paramName);
    		if ("dangerousParamName".equals(paramName)) {

    		}
    		return value;
    	}

    	public String[] getParameterValues(String paramName) {
    		String values[] = super.getParameterValues(paramName);
    		if ("dangerousParamName".equals(paramName)) {
    			for (int index = 0; index < values.length; index++) {

    			}
    		}
    		return values;
    	}
		
	}
	
}
