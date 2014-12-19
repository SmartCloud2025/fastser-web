package org.fastser.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.fastser.web.acl.token.AccessTokenHandler;
import org.fastser.web.context.RequestContextManager;
import org.fastser.web.message.Messages;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AccessTokenInterceptor extends HandlerInterceptorAdapter {
	
	public static final String REQ_ACCESS_TOKEN = "access_token";

    private static final Logger LOGGER = Logger.getLogger(AccessTokenInterceptor.class);
    
    private AccessTokenHandler accessTokenHandler;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	// access token
    	String accessToken = request.getHeader(REQ_ACCESS_TOKEN);
    	if(StringUtils.isNotEmpty(accessToken)){
    		if(null != accessTokenHandler){
    			if(accessTokenHandler.checkAccessToken(accessToken)){
    				//RequestContextManager.initAccessToken(accessToken);
    				LOGGER.debug(Messages.getString("trace.1"));
    			}
    		}
		}
        return true;
    }

	public void setAccessTokenHandler(AccessTokenHandler accessTokenHandler) {
		this.accessTokenHandler = accessTokenHandler;
	}
    
    
}
