package org.fastser.web.resource;

import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_DELETE;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_GET;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_GET_LIST;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_INSERT;
import static org.fastser.web.constant.RestConstant.GENERIC_RESOURCE_METHOD_UPDATE;
import static org.fastser.web.constant.RestConstant.REQU_PARAMETER_NAME_FIELDS;
import static org.fastser.web.constant.RestConstant.REQU_PARAMETER_NAME_FORMAT;
import static org.fastser.web.constant.RestConstant.RESP_PAGE_KEY;
import static org.fastser.web.constant.RestConstant.RESP_DATA_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.fastser.dal.cache.CacheManager;
import org.fastser.web.context.RequestContextManager;
import org.fastser.web.context.RestRequest;
import org.fastser.web.context.RestResponse;
import org.fastser.web.rest.RestDataUtils;
import org.fastser.web.rest.annotation.RestMethod;
import org.fastser.web.rest.handler.RestHandlerMethodExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * 通用rest接口
 * 
 * @author ywj
 * 
 */
@Controller
@RequestMapping("/{parent}/{restName}/{version}")
public class GenericResource extends BaseResource {

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = { "application/json;charset=UTF-8" })
    public @ResponseBody
    ResponseEntity<Object> getList(@PathVariable("parent") String parent, @PathVariable("restName") String restName,
            @PathVariable("version") String version, WebRequest webRequest) throws Exception{
    	RestRequest request = RestDataUtils.initRestRequest(webRequest, parent, restName, version, RestMethod.GET, null, GENERIC_RESOURCE_METHOD_GET_LIST);
    	RestResponse response =  handleRequest(request);
    	return writeResponse(response);
    }
    

    @RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Object> insert(@PathVariable("parent") String parent,
            @PathVariable("restName") String restName, @PathVariable("version") String version,
            @RequestBody Object parameters, WebRequest webRequest) throws Exception {
        if (parameters == null) {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
        RestResponse response = null;
        if (parameters instanceof Map) {
            Map<String, Object> requestMap = (Map<String, Object>) parameters;
            response = singlePost(parent, restName, version, webRequest, requestMap);
        } else if (parameters instanceof List) {
            List<Map<String, Object>> requestList = (List<Map<String, Object>>) parameters;
            for (Map<String, Object> map : requestList) {
                try {
                    singlePost(parent, restName, version, webRequest, map);
                } catch (Exception e) {
                }
            }
        }

        return writeResponse(response);
    }

    

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = { "application/json;charset=UTF-8" })
    public @ResponseBody
    ResponseEntity<Object> get(@PathVariable("parent") String parent, @PathVariable("restName") String restName,
            @PathVariable("version") String version, @PathVariable("id") String id, WebRequest webRequest)
            throws Exception {
    	RestRequest request = RestDataUtils.initRestRequest(webRequest, parent, restName, version, RestMethod.GET, id, GENERIC_RESOURCE_METHOD_GET);
    	RestResponse response =  handleRequest(request);
    	return writeResponse(response);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<Object> update(@PathVariable("parent") String parent,
            @PathVariable("restName") String restName, @PathVariable("version") String version,
            @PathVariable("id") String id, @RequestBody Map<String, Object> parameters, WebRequest webRequest)
            throws Exception {
    	RestRequest request = RestDataUtils.initRestRequest(webRequest, parameters, parent, restName, version, RestMethod.PUT, id, GENERIC_RESOURCE_METHOD_UPDATE);
    	RestResponse response =  handleRequest(request);
    	return writeResponse(response);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable("parent") String parent,
            @PathVariable("restName") String restName, @PathVariable("version") String version,
            @PathVariable("id") String id, WebRequest webRequest) throws Exception {
    	RestRequest request = RestDataUtils.initRestRequest(webRequest, parent, restName, version, RestMethod.PUT, id, GENERIC_RESOURCE_METHOD_DELETE);
    	RestResponse response =  handleRequest(request);
    	return writeResponse(response);
    }

    /**
     * 
     * @param bucket
     * @param restName
     * @param version
     * @param webRequest
     * @param requestMap
     * @return
     * @throws NoSuchMethodException
     * @throws ScriptException
     */
    private RestResponse singlePost(String parent, String restName, String version,
            WebRequest webRequest, Map<String, Object> requestMap) throws Exception {
    	RestRequest request = RestDataUtils.initRestRequest(webRequest, requestMap, parent, restName, version, RestMethod.POST, null, GENERIC_RESOURCE_METHOD_INSERT);
    	RestResponse response = handleRequest(request);
    	return response;
    }
    
    private RestResponse handleRequest(RestRequest request)throws Exception {
		RestResponse response = RequestContextManager.getRestResponse();
		if(request.containsKey(REQU_PARAMETER_NAME_FIELDS)){
			response.put(REQU_PARAMETER_NAME_FIELDS, request.get(REQU_PARAMETER_NAME_FIELDS));
		}
		if(request.containsKey(REQU_PARAMETER_NAME_FORMAT)){
			response.put(REQU_PARAMETER_NAME_FORMAT, request.get(REQU_PARAMETER_NAME_FORMAT));
		}
		response = RestHandlerMethodExecution.postHandle(request, response);
		request.clear();
		return response;
	}

	private ResponseEntity<Object> writeResponse(RestResponse response)throws Exception {
		String fields = response.getString(REQU_PARAMETER_NAME_FIELDS);
		if(StringUtils.isNotEmpty(fields)){
			response.onlyDisplayFields(fields.split(","));
		}
		Object result = response.getResult();
		if(response.containsKey(REQU_PARAMETER_NAME_FORMAT) && response.containsKey(RestRequest.PAGE_RESULT_KEY)){
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put(RESP_PAGE_KEY, response.get(RestRequest.PAGE_RESULT_KEY));
			resultMap.put(RESP_DATA_KEY, response.getResult());
			result = resultMap;
		}
		RequestContextManager.clear();
        if (result == null) {
            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
	}


}
