package org.fastser.web.rest.handler;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ContextLoader;

public class RestHandlerMethodAdapter implements RestHandlerAdapter {
	
	private static final Class<?>[] type1 = new Class<?>[]{RestRequest.class, RestResponse.class};
	private static final Class<?>[] type2 = new Class<?>[]{RestResponse.class, RestRequest.class};


	@Override
	public Object handle(RestRequest request, RestResponse response, RestHandlerMethod handlerMethod) throws Exception {
		if(StringUtils.isNotEmpty(handlerMethod.getTable())){
			request.setTable(handlerMethod.getTable());
		}
		Object bean = ContextLoader.getCurrentWebApplicationContext().getBean(handlerMethod.getBeanName());
		
		Method method = ReflectionUtils.findMethod(bean.getClass(), handlerMethod.getMethodName(), type1);
		Object[] params = new Object[]{request, response};
		
		if(null == method){
			method = ReflectionUtils.findMethod(bean.getClass(), handlerMethod.getMethodName(), type2);
			params = new Object[]{response, request};
		}
		Object result = null;
		
		if(null != method.getReturnType()){
			result = ReflectionUtils.invokeMethod(method, bean, params);
		}else{
			ReflectionUtils.invokeMethod(method, bean, new Object[]{request, response});
		}
		
		return result;
	}

	

}
