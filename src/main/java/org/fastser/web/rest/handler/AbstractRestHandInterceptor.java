package org.fastser.web.rest.handler;

public abstract class AbstractRestHandInterceptor implements RestHandlerInterceptor{
	
	protected static void registerInterceptor(RestHandlerInterceptor interceptor){
		RestHandlerMethodExecution.registerHandlerInterceptor(interceptor);
	}

}
