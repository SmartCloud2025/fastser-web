package org.fastser.web.rest.handler;

import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;

public interface RestHandlerAdapter {
	
	Object handle(RestRequest request, RestResponse response, RestHandlerMethod handlerMethod) throws Exception;

}
