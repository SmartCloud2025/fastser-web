package org.fastser.web.rest.handler;

import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;

public interface RestHandlerInterceptor {
	
	/**
	 * match path
	 * @param path
	 * @return
	 */
	String path();

	boolean preHandle(RestRequest request, RestResponse response)
	    throws Exception;


	void afterCompletion(RestRequest request, RestResponse response)
			throws Exception;


}
