package org.fastser.web.context;

import static org.fastser.web.constant.RestConstant.ACCESS_TOKEN;
import static org.fastser.web.constant.RestConstant.METHOD;
import static org.fastser.web.constant.RestConstant.METHOD_NAME;
import static org.fastser.web.constant.RestConstant.PARAMETER;
import static org.fastser.web.constant.RestConstant.PARENT;
import static org.fastser.web.constant.RestConstant.REQU_PARAMETER_NAME_FORMAT;
import static org.fastser.web.constant.RestConstant.REST_NAME;
import static org.fastser.web.constant.RestConstant.TABLE;
import static org.fastser.web.constant.RestConstant.VERSION;
import static org.fastser.web.constant.RestConstant.WEB_REQUEST;

import java.util.Date;
import java.util.Map;

import org.fastser.web.rest.annotation.RestMethod;
import org.springframework.web.context.request.WebRequest;

public class RestRequest extends RestContext<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -711930199748960878L;
	
	public static final String PAGE_RESULT_KEY = "_page";
	public static final String PAGE_RESULT_SET_FLAG = "_page_set_flag";
	
	public String getAccessToken(){
		return getString(ACCESS_TOKEN);
	}
	
	public WebRequest getWebRequest(){
		return (WebRequest)get(WEB_REQUEST);
	}
	
	public String getRestPath(){
		StringBuffer sb = new StringBuffer();
		sb.append(getMethod()).append(" /").append(getParent())
		  .append("/").append(getRestName()).append("/").append(getVersion());
		return sb.toString();
	}
	
	public boolean hasPage(){
		return this.containsKey(PAGE_RESULT_KEY);
	}
	
	public Page getPage(){
		if(hasPage()){
			return (Page)this.get(PAGE_RESULT_KEY);
		}
		return null;
	}
	
	public void setRecordTotal(int total){
		if(hasPage() && total > 0){
			Page page = (Page)this.get(PAGE_RESULT_KEY);
			page.setRecordTotal(total);
			RestContext<String, Object> parametr = getRestParameter();
			parametr.put("pageIndex", page.getPageIndex());
			parametr.put("pageSize", page.getPageSize());
			this.put(PAGE_RESULT_KEY, page);
			this.put(PARAMETER, parametr);
			this.put(PAGE_RESULT_SET_FLAG, true);
		}
	}
	
	public String getParent(){
		return getString(PARENT);
	}
	
	public String getVersion(){
		return getString(VERSION);
	}
	
	public String getRestName(){
		return getString(REST_NAME);
	}
	
	public String getMethodName(){
		return getString(METHOD_NAME);
	}
	
	public String getTable(){
		return getString(TABLE);
	}
	
	public String getFormat(){
		return getString(REQU_PARAMETER_NAME_FORMAT);
	}
	
	public void setTable(String table){
		this.put(TABLE, table);
	}
	
	public int getCacheTimeOut(){
		return -2;
	}
	
	public RestMethod getMethod(){
		return (RestMethod)get(METHOD);
	}
	
	public Map<String, Object> getParameters(){
		return getRestParameter();
	}
	
	@SuppressWarnings("unchecked")
	private RestContext<String, Object> getRestParameter(){
		return (RestContext<String, Object>)super.get(PARAMETER);
	}
	
	public String getParameterString(String key){
		return getRestParameter().getString(key);
	}
	
	public Integer getParameterInteger(String key){
		return getRestParameter().getInteger(key);
	}
	
	public int getParameterInt(String key){
		return getRestParameter().getInt(key);
	}
	
	public Double getParameterDouble(String key){
		return getRestParameter().getDouble(key);
	}
	
	public Float getParameterFloat(String key){
		return getRestParameter().getFloat(key);
	}
	
	public Boolean getParameterBoolean(String key){
		return getRestParameter().getBoolean(key);
	}
	
	public Date getParameterDate(String key){
		return getRestParameter().getDate(key);
	}
	

}
