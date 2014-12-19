package org.fastser.web.acl.token;

public interface AccessTokenHandler {
	
	boolean checkAccessToken(String accessToken);

}
