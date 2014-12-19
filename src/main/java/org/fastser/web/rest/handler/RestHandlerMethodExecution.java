package org.fastser.web.rest.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;
import org.fastser.web.exception.RestRuntimeException;
import org.fastser.web.exception.ValidateException;
import org.fastser.web.message.Messages;

public class RestHandlerMethodExecution {
	
	private static final Logger LOG = Logger.getLogger(RestHandlerMethodExecution.class);
	
	private static RestHandlerMethodExecution instance = new RestHandlerMethodExecution();
	
	private final RestHandlerAdapter handlerMethodAdapter = new RestHandlerMethodAdapter();
	private final RestHandlerAdapter handlerValidatorAdapter = new RestHandlerValidatorAdapter();
	
	private final List<RestHandlerInterceptor> adaptedInterceptors = new ArrayList<RestHandlerInterceptor>();
	
	private RestHandlerMethodExecution(){}
	
	public static void registerHandlerInterceptor(RestHandlerInterceptor interceptor){
		if(null != interceptor){
			instance.adaptedInterceptors.add(interceptor);
			LOG.debug("register rest interceptor " + interceptor.getClass());
		}
	}
	
	private static boolean applyPreHandle(RestRequest request, RestResponse response) throws Exception {
		boolean result = true;
		if(!instance.adaptedInterceptors.isEmpty()){
			for(RestHandlerInterceptor interceptor:instance.adaptedInterceptors){
				if(matchPath(request, interceptor.path())){
					result = interceptor.preHandle(request, response);
					if(!result){
						LOG.debug("Method preHandle of interceptor returned false, class:" + interceptor.getClass());
						break;
					}
				}
			}
		}
		return result;
	}

	/**
	 * Apply postHandle methods of registered interceptors.
	 */
	public static RestResponse postHandle(RestRequest request, RestResponse response) throws Exception {
		if(!applyPreHandle(request, response)){
			return null;
		}
		List<RestHandlerMethod> validators = RestHandlerMethodMapping.getValidatorHandler(request);
		
		if(null != validators){
			boolean validateFlag = true;
			for(RestHandlerMethod method:validators){
				Object result = instance.handlerValidatorAdapter.handle(request, response, method);
				if(null != result){
					validateFlag = (boolean) result;
				}
				if(!validateFlag){
					throw new ValidateException(Messages.getString("ValidateError.2", request.getRestPath(), method.toString()));
				}
			}
		}
		
		Object result = null;
		List<RestHandlerMethod> handlers = RestHandlerMethodMapping.getMethodHandler(request);
		if(null != validators){
			for(RestHandlerMethod method:handlers){
				result = instance.handlerMethodAdapter.handle(request, response, method);
				if(null != result){
					response.putResult(result);
					break;
				}
			}
		}
		
		triggerAfterCompletion(request, response);
		
		return response;
	}

	/**
	 * Trigger afterCompletion callbacks on the mapped HandlerInterceptors.
	 * Will just invoke afterCompletion for all interceptors whose preHandle invocation
	 * has successfully completed and returned true.
	 */
	private static void triggerAfterCompletion(RestRequest request, RestResponse response)
			throws Exception {
		if(!instance.adaptedInterceptors.isEmpty()){
			for(RestHandlerInterceptor interceptor:instance.adaptedInterceptors){
				if(matchPath(request, interceptor.path())){
					interceptor.afterCompletion(request, response);
				}
			}
		}
	}
	
	private static final String ANY_MATCH_SIGN = "**";
	
	private static boolean matchPath(RestRequest request, String path){
		if(StringUtils.isEmpty(path)){
			LOG.debug("Interceptor path is null");
			return false;
		}
		boolean parentMatch = false, restNameMatch = false;
		String[] paths = path.split("/");
		if(paths.length != 3){
			throw new RestRuntimeException(Messages.getString("RuntimeError.2"));
		}
		if(ANY_MATCH_SIGN.equals(paths[1])){
			parentMatch = true;
		}
		if(ANY_MATCH_SIGN.equals(paths[2])){
			restNameMatch = true;
		}
		
		if(!parentMatch){
			parentMatch = matchPathPattern(paths[1], request.getParent());
		}
		
		if(!restNameMatch){
			restNameMatch = matchPathPattern(paths[2], request.getRestName());
		}
		
		if(true && parentMatch && restNameMatch){
			return true;
		}
		return false;
	}

	private static boolean matchPathPattern(String pattern, String value) {
		boolean result = false;
		int index = pattern.indexOf(ANY_MATCH_SIGN);
		if(index == -1){
			result = pattern.equals(value);
		}else{
			result = pattern.contains(value);
		}
		return result;
	}

}
