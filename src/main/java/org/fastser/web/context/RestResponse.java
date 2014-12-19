package org.fastser.web.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fastser.dal.descriptor.QueryResult;
import org.fastser.dal.utils.JsonUtils;

public class RestResponse extends RestContext<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6732312763880543546L;
	
	
	private static final String RESULT_KEY = "_result"; 
	public static final String SYSTEM_HANDLE_RESULT_KEY = "_system_result";
	
	
	public void putResult(Object result){
		if(null != result){
			if(result instanceof QueryResult){
				QueryResult queryResult = (QueryResult)result;
				Object rt = queryResult.getList();
				if(null == rt){
					rt = queryResult.get();
				}
				this.put(RESULT_KEY, rt);
				if(null != queryResult.getPage()){
					this.putPage(Page.valueOf(queryResult.getPage()));
				}
			}else{
				if(result instanceof List){
					List<Object> listTemp = (List<Object>)result;
					List<Object> listRt = new ArrayList<Object>();
					for(Object obj:listTemp){
						listRt.add(JsonUtils.objToMap(obj));
					}
					this.put(RESULT_KEY, listRt);
				}else{
					this.put(RESULT_KEY, JsonUtils.objToMap(result));
				}
			}
		}
	}
	
	public void putPage(Page page){
		if(null != page){
			this.put(RestRequest.PAGE_RESULT_KEY, page);
		}
	}
	
	public Object getResult(){
		if(this.containsKey(RESULT_KEY)){
			return this.get(RESULT_KEY);
		}else{
			return this.get(SYSTEM_HANDLE_RESULT_KEY);
		}
	}
	
	public List<Map<String, Object>> getResultAsList(){
		return (List<Map<String, Object>>)getResult();
	}
	
	public Map<String, Object> getResultAsMap(){
		return (Map<String, Object>)getResult();
	}
	
	public void onlyDisplayFields(String[] hiddenFields){
		onlyDisplayFields(Arrays.asList(hiddenFields));
	}
	
	public void onlyDisplayFields(List<String> displayFields){
		if(displayFields != null && displayFields.size() > 0){
			Object rt = getResult();
			if(null != rt){
				if(rt instanceof Map){
					Map<String, Object> result = (Map<String, Object>)rt;
					result = displyField4MapResult(displayFields, result, null);
		    		putResult(result);
				}else if(rt instanceof List){
					List<Map<String, Object>> resultList = (List<Map<String, Object>>)rt;
					resultList = displayField4ListResult(displayFields, resultList, null);
		    		putResult(resultList);
				}else{
					putResult(rt);
				}
			}
		}
	}
	
	public void hiddenDisplayFields(String[] hiddenFields){
		hiddenDisplayFields(Arrays.asList(hiddenFields));
	}
	
	public void hiddenDisplayFields(List<String> hiddenFields){
		if(hiddenFields != null && hiddenFields.size() > 0){
			Object rt = getResult();
			if(null != rt){
				if(rt instanceof Map){
					Map<String, Object> result = (Map<String, Object>)rt;
					for(String field:hiddenFields){
						result = hiddenMapResult(field, result);
					}
		    		putResult(result);
				}else if(rt instanceof List){
					List<Map<String, Object>> resultList = (List<Map<String, Object>>)rt;
					for(String field:hiddenFields){
						resultList = hiddenListResult(field, resultList);
					}
		    		putResult(resultList);
				}else{
					putResult(rt);
				}
			}
		}
	}
	
	private Map<String, Object> displyField4MapResult(List<String> desplyFields, Map<String, Object> result, String parentKey) {
		int size = result.size();
		boolean[] flag = new boolean[size];
		String[] allFields = new String[size];
		int index = 0;
		for(Map.Entry<String, Object> entry:result.entrySet()){
			String key = entry.getKey();
			Object val = entry.getValue();
			if(val instanceof Map){
				Map<String, Object> temp = (Map<String, Object>)entry.getValue();
				temp = displyField4MapResult(desplyFields, temp, StringUtils.isEmpty(parentKey)?entry.getKey():parentKey+"."+entry.getKey());
				if(temp.isEmpty()){
					result.remove(entry.getKey());
				}else{
					result.put(entry.getKey(), temp);
				}
			}else if(val instanceof List){
				List<Map<String, Object>> temp = (List<Map<String, Object>>)entry.getValue();
				temp = displayField4ListResult(desplyFields, temp, StringUtils.isEmpty(parentKey)?entry.getKey():parentKey+"."+entry.getKey());
				if(temp.isEmpty()){
					result.remove(entry.getKey());
				}else{
					result.put(entry.getKey(), temp);
				}
			}else{
				allFields[index] = key;
				for(String dspKey : desplyFields){
					if(dspKey.indexOf(".") != -1){
						if(StringUtils.isNotEmpty(parentKey)){
							if(dspKey.equals(parentKey+"."+key)){
								flag[index] = true;
							}else if(parentKey.equals(dspKey)){
								flag[index] = true;
							}else{
								String[] dis = dspKey.split("\\.");
								String[] fds = parentKey.split("\\.");
								int len = dis.length - 1;
								if(fds.length >= len){
									boolean match = true;
									for(int i=0;i<len;i++){
										if(match){
											if("*".equals(dis[i])){
												match = true;
											}else if(dis[i].equals(fds[i])){
												match = true;
											}else{
												match = false;
											}
										}
									}
									if(match){
										if("*".equals(dis[len])){
											match = true;
										}else if(dis[len].equals(key)){
											match = true;
										}else{
											match = false;
										}
									}
									if(match){
										flag[index] = true;
									}
								}
							}
						}
					}else{
						if(dspKey.equals(key)){
							flag[index] = true;
						}
					}
				}
				index++;
			}
		}
		for(int i=0;i<size;i++){
			if(flag[i] == false){
				result.remove(allFields[i]);
			}
		}
		return result;
	}
	
	private List<Map<String, Object>> displayField4ListResult(List<String> displayFields, List<Map<String, Object>> resultList, String parentKey) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map : resultList){
			map = displyField4MapResult(displayFields, map, parentKey);
			if(!map.isEmpty()){
				list.add(map);
			}
		}
		return list;
	}
	
	private List<Map<String, Object>> hiddenListResult(String hideenField, List<Map<String, Object>> resultList) {
		for(Map<String, Object> map:resultList){
			hiddenField4MapResult(hideenField, map);
		}
		return resultList;
	}
	
	
	
	private Map<String, Object> hiddenMapResult(String hideenField, Map<String, Object> result) {
		if(hideenField.indexOf(".") != -1){
			String[] fds = hideenField.split("\\.");
			int size = fds.length - 1;
			Map<String, Object> value = result;
			for(int i=0;i<size;i++){
				if(value.containsKey(fds[i])){
					Object obj = value.get(fds[i]);
					if(i == size -1){
						if(obj instanceof Map){
							Map<String, Object> temp = (Map<String, Object>)obj;
							temp = hiddenField4MapResult(fds[size], temp);
							if(temp.isEmpty()){
								value.remove(fds[i]);
							}else{
								value.put(fds[i], temp);
							}
						}else if(obj instanceof List){
							List<Map<String, Object>> temp = (List<Map<String, Object>>)obj;
							temp = hiddenField4ListResult(fds[size], temp);
							if(temp.isEmpty()){
								value.remove(fds[i]);
							}else{
								value.put(fds[i], temp);
							}
						}
					}else{
						value = (Map<String, Object>) obj;
					}
				}
			}
		}else{
			result = hiddenField4MapResult(hideenField, result);
		}
		return result;
	}
	
	private Map<String, Object> hiddenField4MapResult(String hideenField, Map<String, Object> result) {
		if(result.containsKey(hideenField)){
			result.remove(hideenField);
		}
		return result;
	}

	private List<Map<String, Object>> hiddenField4ListResult(String hideenField, List<Map<String, Object>> resultList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> map : resultList){
			map = hiddenField4MapResult(hideenField, map);
			if(!map.isEmpty()){
				list.add(map);
			}
		}
		return list;
	}
	
	public String getString(String key){
		return super.getString(key);
	}
	
	public Integer getInteger(String key){
		return super.getInteger(key);
	}
	
	public int getInt(String key){
		return super.getInt(key);
	}
	
	public Double getDouble(String key){
		return super.getDouble(key);
	}
	
	public Float getFloat(String key){
		return super.getFloat(key);
	}
	
	public Boolean getBoolean(String key){
		return super.getBoolean(key);
	}
	
	public Date getDate(String key){
		return super.getDate(key);
	}
	
	

}
