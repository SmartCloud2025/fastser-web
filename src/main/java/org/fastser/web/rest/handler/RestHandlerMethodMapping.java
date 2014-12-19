package org.fastser.web.rest.handler;

import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_DELETE;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_GET;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_GET_LIST;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_INSERT;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_UPDATE;
import static org.fastser.web.constant.RestConstant.XML_FILE_PATH;
import static org.fastser.web.constant.RestConstant.XML_MAP_KEY;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.fastser.util.XmlUtil;
import org.fastser.web.context.RestRequest;
import org.fastser.web.exception.MethodNotFoundException;
import org.fastser.web.message.Messages;
import org.fastser.web.rest.GenericRestful;
import org.fastser.web.rest.RestDataUtils;
import org.fastser.web.rest.annotation.RestAnotationMetadata;
import org.fastser.web.rest.annotation.RestAnotationType;
import org.fastser.web.rest.annotation.RestMethod;
import org.fastser.web.rest.annotation.RestMethodMappingInfo;

public class RestHandlerMethodMapping{
	
	private static final Logger LOG = Logger.getLogger(RestHandlerMethodMapping.class);
	
	private static RestHandlerMethodMapping instance = new RestHandlerMethodMapping();
	
	private final Map<RestMethodMappingInfo, RestHandlerMethod> handlerMethods = new LinkedHashMap<RestMethodMappingInfo, RestHandlerMethod>();
	
	private final List<RestAnotationMetadata> restMethodMappings = new ArrayList<RestAnotationMetadata>();
	
	private final List<RestAnotationMetadata> restValidatorMappings = new ArrayList<RestAnotationMetadata>();
	
	private final Map<String, RestHandlerMethod> genericRestfulMethod = new HashMap<String, RestHandlerMethod>();
	
	private final List<RestAnotationMetadata> defaultRestMappings = new ArrayList<RestAnotationMetadata>();
	
	private GenericRestful genericRestful = null;

	private RestHandlerMethodMapping(){}
	
	static{
		initRestMappingInfoFromXmlFile();
	}

	/**
	 * Register a handler method and its unique mapping.
	 * @param handler the bean name of the handler or the handler instance
	 * @param method the method to register
	 * @param mapping the mapping conditions associated with the handler method
	 * @throws IllegalStateException if another method was already registered
	 * under the same mapping
	 */
	public static void registerHandlerMethod(RestMethodMappingInfo mapping, RestHandlerMethod handlerMethod) {
		RestHandlerMethod oldHandlerMethod = instance.handlerMethods.get(mapping);
		if (oldHandlerMethod != null && !oldHandlerMethod.equals(handlerMethod)) {
			throw new IllegalStateException("Ambiguous mapping found. Cannot map '" + handlerMethod.getBeanName()
					+ "' bean method \n" + handlerMethod + "\nto " + mapping + ": There is already '"
					+ oldHandlerMethod.getBeanName() + "' bean method\n" + oldHandlerMethod + " mapped.");
		}

		instance.handlerMethods.put(mapping, handlerMethod);
		if (LOG.isInfoEnabled()) {
			LOG.info("Mapped \"" + mapping + "\" onto " + handlerMethod);
		}
	}
	
	public static void registerRestMethodMapping(RestAnotationMetadata mapping) {
		instance.restMethodMappings.add(mapping);
		if (LOG.isInfoEnabled()) {
			LOG.info("Add rest method mapping\"" + mapping);
		}
	}
	
	public static void registerRestValidatorMapping(RestAnotationMetadata mapping) {
		instance.restValidatorMappings.add(mapping);
		if (LOG.isInfoEnabled()) {
			LOG.info("Add rest validator mapping\"" + mapping);
		}
	}
	
	public static void registerGenericRestful(Object genericRestful) {
		instance.genericRestful = (GenericRestful)genericRestful;
		if (LOG.isInfoEnabled()) {
			LOG.info("register rest generic restful service");
		}
	}
	
	public static List<RestHandlerMethod> getMethodHandler(RestRequest request) {
		RestMethod method = request.getMethod();
		String parent = request.getParent();
		String restName = request.getRestName(); 
		String version = request.getVersion();
		String methodName = request.getMethodName();
		// load generic restful method
		if(null != instance.genericRestful){
			if(instance.genericRestfulMethod.isEmpty()){
				loadDefaultMethod();
			}
		}
		List<RestHandlerMethod> handlers = new ArrayList<RestHandlerMethod>();
		List<RestAnotationMetadata> list = new ArrayList<RestAnotationMetadata>();
		//key:before node name, value:current node
		Map<String, RestAnotationMetadata> temp = new HashMap<String, RestAnotationMetadata>();
		// sort list
		RestAnotationMetadata firstNode = null;
		for(RestAnotationMetadata info:instance.restMethodMappings){
			if(info.match(method, parent, restName, version)){
				if(StringUtils.isEmpty(info.getBeforeName()) || GENERIC_RESOURCE_METHOD_DELETE.equals(info.getBeforeName())
						|| GENERIC_RESOURCE_METHOD_GET.equals(info.getBeforeName()) || GENERIC_RESOURCE_METHOD_GET_LIST.equals(info.getBeforeName())
						|| GENERIC_RESOURCE_METHOD_INSERT.equals(info.getBeforeName()) || GENERIC_RESOURCE_METHOD_UPDATE.equals(info.getBeforeName())){
					firstNode = info;
					if(StringUtils.isNotEmpty(info.getBeforeName())){
						RestHandlerMethod restHandlerMethod = instance.genericRestfulMethod.get(info.getBeforeName());
						if(null != restHandlerMethod){
							restHandlerMethod.setTable(info.getTable());
							handlers.add(restHandlerMethod);
							LOG.debug("Find generic rest handler method " + restHandlerMethod);
						}else{
							LOG.debug("Default handler method not find, name" + methodName);
						}
					}
				}else{
					temp.put(info.getBeforeName(), info);
				}
			}
		}
		if(null != firstNode){
			list.add(firstNode);
		}
		if(temp.size() > 0 && null != firstNode){
			RestAnotationMetadata nextNode = null;
			for(int i=0;i<temp.size()+1;i++){
				nextNode = temp.get(firstNode.getRestName());
				if(null == nextNode){
					break;
				}
				list.add(nextNode);
			}
		}
		
		for(RestAnotationMetadata info:list){
			handlers.add(instance.handlerMethods.get(info.getMethodMappingInfo()));
			LOG.debug("Find handler method " + instance.handlerMethods.get(info.getMethodMappingInfo()));
		}
		if(null != instance.genericRestful){
			if(instance.genericRestfulMethod.isEmpty()){
				loadDefaultMethod();
			}
			if(handlers.isEmpty()){
				RestAnotationMetadata info = getDefaultRestMapping(method, parent, restName, version);
				if(null != info){
					RestHandlerMethod restHandlerMethod = instance.genericRestfulMethod.get(methodName);
					if(null != restHandlerMethod){
						restHandlerMethod.setTable(info.getTable());
						handlers.add(restHandlerMethod);
					}else{
						LOG.debug("Default handler method not find, name" + methodName);
					}
				}
			}
		}
		if(handlers.isEmpty()){
			throw new MethodNotFoundException(Messages.getString("RuntimeError.1"));
		}
		return handlers;
		
	}
	
	public static List<RestHandlerMethod> getValidatorHandler(RestRequest request) {
		RestMethod method = request.getMethod();
		String parent = request.getParent();
		String restName = request.getRestName(); 
		String version = request.getVersion();
		List<RestHandlerMethod> handlers = new ArrayList<RestHandlerMethod>();
		List<RestAnotationMetadata> list = new ArrayList<RestAnotationMetadata>();
		//key:before node name, value:current node
		Map<String, RestAnotationMetadata> temp = new HashMap<String, RestAnotationMetadata>();
		// sort list
		RestAnotationMetadata firstNode = null;
		for(RestAnotationMetadata info:instance.restValidatorMappings){
			if(info.match(method, parent, restName, version)){
				if(StringUtils.isNotEmpty(info.getBeforeName())){
					temp.put(info.getBeforeName(), info);
				}
				if(StringUtils.isEmpty(info.getBeforeName())){
					firstNode = info;
				}
			}
		}
		if(null != firstNode){
			list.add(firstNode);
		}
		if(temp.size() > 0 && null != firstNode){
			RestAnotationMetadata nextNode = null;
			String methodName = firstNode.getMethodMappingInfo().getMethodName();
			while(true){
				nextNode = temp.get("_"+methodName);
				if(null == nextNode){
					break;
				}
				list.add(nextNode);
				methodName = nextNode.getMethodMappingInfo().getMethodName();
			}
		}
		
		for(RestAnotationMetadata info:list){
			handlers.add(instance.handlerMethods.get(info.getMethodMappingInfo()));
			LOG.debug("Find handler method " + instance.handlerMethods.get(info.getMethodMappingInfo()));
		}
		
		return handlers;
	}
	
	public static void initRestMappingInfoFromXmlFile(){
		List<Map<String, Object>> list = XmlUtil.getMapFromXmlFile(XML_FILE_PATH);
		if(list.isEmpty()){
			LOG.debug("Xml file content is null");
		}else{
			for(Map<String, Object> map:list){
				RestAnotationMetadata restMappingInfo = RestDataUtils.methodAnnotationAttributesXmlResolver(map.get(XML_MAP_KEY));
				restMappingInfo.setType(RestAnotationType.MAPPING);
				instance.defaultRestMappings.add(restMappingInfo);
			}
			LOG.debug("Xml file content:" + list);
		}
	}
	
	private static void loadDefaultMethod(){
		if(instance.genericRestfulMethod.isEmpty() && null != instance.genericRestful){
			try {
				Method[] methods = instance.genericRestful.getClass().getMethods();
				if(null != methods){
					for(Method method:methods){
						instance.genericRestfulMethod.put(method.getName(), new RestHandlerMethod("genericRestful", method.getName()));
					}
				}
			} catch (SecurityException e) {
				LOG.error("load default handle method error", e);
			}
		}
	}
	
	private static RestAnotationMetadata getDefaultRestMapping(RestMethod method, String parent, String restName, String version){
		RestAnotationMetadata restMappingInfo = null;
		if(!instance.defaultRestMappings.isEmpty()){
			for(RestAnotationMetadata info:instance.defaultRestMappings){
				if(info.match(method, parent, restName, version)){
					restMappingInfo = info;
					break;
				}
			}
		}
		return restMappingInfo;
	}
	
	
	
	
	

}
