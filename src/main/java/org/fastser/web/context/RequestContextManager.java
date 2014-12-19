package org.fastser.web.context;



public class RequestContextManager {
	
	private static final ThreadLocal<RestRequest> REST_REQUEST = new ThreadLocal<RestRequest>();
	
	private static final ThreadLocal<RestResponse> REST_RESPONSE = new ThreadLocal<RestResponse>();
	

	public static RestRequest getRestRequest() {
		if(REST_REQUEST.get() == null){
			REST_REQUEST.set(new RestRequest());
		}
		return REST_REQUEST.get();
	}
	
	public static RestResponse getRestResponse() {
		if(REST_RESPONSE.get() == null){
			REST_RESPONSE.set(new RestResponse());
		}
		return REST_RESPONSE.get();
	}
	
	public static void clear(){
		REST_REQUEST.remove();
		REST_RESPONSE.remove();
	}

	
	

}
