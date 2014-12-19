package org.fastser.web.resource;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.fastser.web.exception.AuthorizationException;
import org.fastser.web.exception.BaseException;
import org.fastser.web.exception.MethodNotFoundException;
import org.fastser.web.exception.ValidateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseResource {
	
	private static final Logger LOG = Logger.getLogger(BaseResource.class);
	
	
	@ExceptionHandler({ValidateException.class})
	protected ResponseEntity<Object> handleValidateException(ValidateException e){
		LOG.error("Validate error", e);
		return new ResponseEntity<Object>(buildErrorMsg(e), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({Exception.class})
	protected ResponseEntity<Object> handleException(Exception e){
		LOG.error("Server error", e);
		return new ResponseEntity<Object>(buildErrorMsg(e), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler({AuthorizationException.class})
	protected ResponseEntity<Object> handleAuthorizationException(AuthorizationException e){
		LOG.error("Authorization error", e);
		return new ResponseEntity<Object>(buildErrorMsg(e), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler({MethodNotFoundException.class})
	protected ResponseEntity<Object> handleAuthorizationException(MethodNotFoundException e){
		LOG.error("Not found", e);
		return new ResponseEntity<Object>(buildErrorMsg(e), HttpStatus.NOT_FOUND);
	}
	
   
    /**
     * get project base path
     * @param request
     * @return
     */
	protected String getBasePath(HttpServletRequest request) {
		//String path = request.getContextPath();
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	}
    
    
	
	
	private Map<String, Object> buildErrorMsg(Exception ex){
		Map<String, Object> msg = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(ex.getMessage())){
			if(ex instanceof BaseException){
				BaseException bex = (BaseException)ex;
				msg.put("code", bex.getNumber());
			}
			msg.put("error", ex.getMessage());
		}else{
			msg.put("error", ex.toString());
		}
		return msg;
	}
	
	

}
