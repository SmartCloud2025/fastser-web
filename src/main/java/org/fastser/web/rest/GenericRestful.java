package org.fastser.web.rest;

import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;

public interface GenericRestful {
	
	void _list(RestRequest request, RestResponse response);
	
	void _get(RestRequest request, RestResponse response);
    
	void _insert(RestRequest request, RestResponse response);
	
	void _update(RestRequest request, RestResponse response);
	
	void _delete(RestRequest request, RestResponse response);
    
}
