package org.fastser.web.rest.annotation;

import java.util.HashMap;
import java.util.Map;

public enum RestMethod {
	
	GET("GET"), HEAD("HEAD"), POST("POST"), PUT("PUT"), DELETE("DELETE"), OPTIONS("OPTIONS");
	
	public final String VALUE;
	
	RestMethod(String value) {
	    this.VALUE = value.toUpperCase();
	}
	
	private static Map<String,RestMethod> lookup = new HashMap<String,RestMethod>();

	static {
		for (RestMethod method : RestMethod.values()) {
			lookup.put(method.VALUE, method);
		}
	}
	
	public static RestMethod forValue(String value) {
		return lookup.get(value.toUpperCase());
	}

	public String value() {
		return VALUE;
	}
	
}
