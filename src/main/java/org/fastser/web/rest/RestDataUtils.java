package org.fastser.web.rest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fastser.web.constant.RestConstant;
import org.fastser.web.context.Page;
import org.fastser.web.context.RequestContextManager;
import org.fastser.web.context.RestContext;
import org.fastser.web.context.RestRequest;
import org.fastser.web.rest.annotation.RestAnotationMetadata;
import org.fastser.web.rest.annotation.RestMethod;
import org.fastser.web.rest.annotation.RestMethodMappingInfo;
import org.fastser.web.rest.handler.RestHandlerMethod;
import org.springframework.web.context.request.WebRequest;

public class RestDataUtils implements RestConstant{
	
	public static String buildRestUrl(RestMethod method, String parent, String restName, String version) {
		return method + ":/" + parent + "/" + restName + "/" + version;
	}
	
	public static RestRequest initRestRequest(WebRequest webRequest, String parent, String restName, String version, RestMethod method, String id, String entry){
		RestRequest restRequest = RequestContextManager.getRestRequest();
		RestContext<String, Object> parameter = null;
		restRequest.put(WEB_REQUEST, webRequest);
		if(null != webRequest.getParameterMap()){
			parameter = convertWebRequest2Map(webRequest, restRequest);
		}
		restRequest.put(PARENT, parent);
		restRequest.put(REST_NAME, restName);
		restRequest.put(METHOD, method);
		restRequest.put(VERSION, version);
		restRequest.put(METHOD_NAME, entry);
		if(StringUtils.isNotEmpty(id)){
			if(parameter == null){
				parameter = new RestContext<String, Object>();
			}
			parameter.put(OBJECT_ID, Integer.valueOf(id));
		}
		if(null != parameter){
			restRequest.put(PARAMETER, parameter);
		}
		return restRequest;
	}
	
	public static RestRequest initRestRequest(WebRequest webRequest, Map<String, Object> parameterMap, String parent, String restName, String version, RestMethod method, String id, String entry){
		RestRequest restRequest = RequestContextManager.getRestRequest();
		RestContext<String, Object> parameter = null;
		restRequest.put(WEB_REQUEST, webRequest);
		if(null != parameterMap){
			parameter = new RestContext<String, Object>();
			parameter.putAll(parameterMap);
		}
		restRequest.put(PARENT, parent);
		restRequest.put(REST_NAME, restName);
		restRequest.put(METHOD, method);
		restRequest.put(VERSION, version);
		restRequest.put(METHOD_NAME, entry);
		if(StringUtils.isNotEmpty(id)){
			if(parameter == null){
				parameter = new RestContext<String, Object>();
			}
			parameter.put(OBJECT_ID, Integer.valueOf(id));
		}
		if(null != parameter){
			restRequest.put(PARAMETER, parameter);
		}
		return restRequest;
	}
	
	public static RestContext<String, Object> convertWebRequest2Map(WebRequest webRequest, RestRequest restRequest) {
		RestContext<String, Object> parameter = new RestContext<String, Object>();
		int index = 0, size = 0;
        Iterator<String> iter = webRequest.getParameterNames();
        while(iter.hasNext()){
            String name = iter.next();
            String value = webRequest.getParameter(name);
            if(StringUtils.isNotBlank(value)){
            	if(RESP_CALLBACK.equalsIgnoreCase(name)){
                	continue;
                }else if(REQU_PARAMETER_NAME_FIELDS.equalsIgnoreCase(name)){ 
                	restRequest.put(REQU_PARAMETER_NAME_FIELDS, value);
    			}else if(REQU_PARAMETER_NAME_FORMAT.equalsIgnoreCase(name)){
    				restRequest.put(REQU_PARAMETER_NAME_FORMAT, value);
    			}else if(REQU_PARAMETER_NAME_PAGE_INDEX.equalsIgnoreCase(name)){
        			index = Integer.parseInt(value);
        		}else if(REQU_PARAMETER_NAME_PAGE_SIZE.equalsIgnoreCase(name)){ 
        			size = Integer.parseInt(value);
        		}else{
    				parameter.put(name, value);
    			}
            }
        }
        if(index >= 0){
        	if(index == 0){
    			index = DEFAULT_PAGE_INDEX;
    		}
            if(size == 0){
    			size = DEFAULT_PAGE_SIZE;
    		}
    		Page page = new Page(index, size);
    		restRequest.put(RestRequest.PAGE_RESULT_KEY, page);
        }
        
        return parameter;
    }
	
	public static RestHandlerMethod buildRestHandlerMethod(String bean, String method, String table){
		if(null != bean && null != method){
			RestHandlerMethod restHandlerMethod = new RestHandlerMethod(bean, method);
			restHandlerMethod.setTable(table);
			return restHandlerMethod;
		}
		return null;
	}
	
	public static RestMethodMappingInfo buildRestMethodMappingInfo(String className, String methodName){
		if(StringUtils.isNotEmpty(className) && StringUtils.isNotEmpty(methodName)){
			RestMethodMappingInfo restMethodMappingInfo = new RestMethodMappingInfo();
			restMethodMappingInfo.setClassName(className);
			restMethodMappingInfo.setMethodName(methodName);
			return restMethodMappingInfo;
		}
		return null;
	}
	
	public static String typeAnnotationAttributesResolver(Map<String, Object> value){
		String parent = null;
		if(!value.isEmpty()){
			parent = getStringAttribute(REST_NAME, value);
		}
		return parent;
	}
	
	public static RestAnotationMetadata methodAnnotationAttributesXmlResolver(Object obj){
		Map<String, Object> value = (Map<String, Object>) obj;
		RestAnotationMetadata info =  new RestAnotationMetadata();
		if(!value.isEmpty()){
			info.setRestName(getArrayAttribute(XML_REST_NAME, value));
			info.setParent(getStringAttribute(PARENT, value));
			info.setVersion(getArrayAttribute(VERSION, value));
			info.setMethod(getRestMethodAttribute(METHOD, value));
			info.setTable(getStringAttribute(TABLE, value));
		}
		return info;
	}

	
	public static RestAnotationMetadata methodAnnotationAttributesResolver(Map<String, Object> value){
		return methodAnnotationAttributesResolver(value, null);
	}
	
	public static RestAnotationMetadata methodAnnotationAttributesResolver(Map<String, Object> value, RestAnotationMetadata info){
		if(null == info){
			info =  new RestAnotationMetadata();
		}
		if(!value.isEmpty()){
			String beforeName = getStringAttribute(BEFORE_NAME, value);
			if(StringUtils.isNotEmpty(beforeName)){
				info.setBeforeName("_"+beforeName);
			}
			info.setRestName(getArrayAttribute(REST_NAME, value));
			info.setVersion(getArrayAttribute(VERSION, value));
			info.setMethod(getRestMethodAttribute(METHOD, value));
			String table = getStringAttribute(TABLE, value);
			if(StringUtils.isNotEmpty(table)){
				info.setTable(table);
			}else{
				info.setTable(getStringAttribute(REST_NAME, value));
			}
		}
		return info;
	}
	
	public static Map<String, Object> hiddenRequestField(Map<String, Object> map, List<String> hiddenFields){
    	if(hiddenFields != null && map != null){
    		for(String field : hiddenFields){
    			if(map.containsKey(field)){
    				map.remove(field);
    			}
    		}
    	}
    	return map;
    }

	private static String getStringAttribute(String key, Map<String, Object> value){
		if(value.containsKey(key)){
			Object val = value.get(key);
			if(val instanceof String){
				String valStr = String.valueOf(val);
				if(StringUtils.isNotEmpty(valStr)){
					return valStr;
				}
			}else{
				return String.valueOf(val);
			}
		}
		return null;
	}
	
	private static RestMethod[] getRestMethodAttribute(String key, Map<String, Object> value){
		String[] array = null;
		RestMethod[] methods = null;
		if(value.containsKey(key)){
			Object val = value.get(key);
			if(val instanceof String){
				String valStr = String.valueOf(val);
				if(StringUtils.isNotEmpty(valStr)){
					array = valStr.split(",");
				}
			}else{
				array = (String[])val;
			}
		}
		if(null != array){
			int size = array.length;
			methods = new RestMethod[size];
			for(int i=0;i<size;i++){
				methods[i] = RestMethod.forValue(array[i]);
			}
		}
		return methods;
	}
	
	private static String[] getArrayAttribute(String key, Map<String, Object> value){
		if(value.containsKey(key)){
			Object val = value.get(key);
			if(val instanceof String){
				String valStr = String.valueOf(val);
				if(StringUtils.isNotEmpty(valStr)){
					return valStr.split(",");
				}
			}else{
				return (String[])val;
			}
		}
		return null;
	}
	
	private static int getIntAttribute(String key, Map<String, Object> value){
		if(value.containsKey(key)){
			Object val = value.get(key);
			if(val instanceof String){
				String valStr = String.valueOf(val);
				if(StringUtils.isNotEmpty(valStr)){
					return Integer.valueOf(valStr);
				}
			}else{
				return (Integer)val;
			}
		}
		return 0;
	}

}
