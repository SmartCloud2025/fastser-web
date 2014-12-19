package org.fastser.web.rest.annotation;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.fastser.web.rest.annotation.RestAnotationType;
import org.fastser.web.rest.annotation.RestMethod;
import org.fastser.web.rest.annotation.RestMethodMappingInfo;


public class RestAnotationMetadata implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1279200027440930879L;

	private RestAnotationType type;
	
	private String parent;
	
	private String[] restName;
	
	private RestMethod[] method;
	
	private String[] version;
	
	private String table;
	
	private String beforeName;
	
	private RestMethodMappingInfo methodMappingInfo;
	
	public RestAnotationMetadata() {
	}

	public RestAnotationMetadata(RestAnotationType type, String parent, String[] restName) {
		this.type = type;
		this.parent = parent;
		this.restName = restName;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String[] getRestName() {
		return restName;
	}

	public void setRestName(String[] restName) {
		this.restName = restName;
	}

	public String getBeforeName() {
		return beforeName;
	}

	public void setBeforeName(String beforeName) {
		this.beforeName = beforeName;
	}

	public RestAnotationType getType() {
		return type;
	}

	public void setType(RestAnotationType type) {
		this.type = type;
	}
	
	public RestMethod[] getMethod() {
		return method;
	}

	public void setMethod(RestMethod[] method) {
		this.method = method;
	}

	public String[] getVersion() {
		return version;
	}

	public void setVersion(String[] version) {
		this.version = version;
	}

	public RestMethodMappingInfo getMethodMappingInfo() {
		return methodMappingInfo;
	}

	public void setMethodMappingInfo(RestMethodMappingInfo methodMappingInfo) {
		this.methodMappingInfo = methodMappingInfo;
	}
	
	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public boolean match(RestMethod restMethod, String parent, String restName, String version) {
		if (null == restMethod || StringUtils.isEmpty(parent) || StringUtils.isEmpty(restName) || StringUtils.isEmpty(version)) {
			return false;
		}
		boolean result = true;
		if(null != method){
			boolean temp = false;
			for(RestMethod rm:this.method){
				if(rm.equals(restMethod)){
					temp = true;
					break;
				}
			}
			result = temp;
		}
		if(result){
			result = this.parent.equals(parent);
		}
		if(result && null != restName){
			boolean temp = false;
			for(String ver:this.restName){
				if(ver.equals(restName)){
					temp = true;
					break;
				}
			}
			result = temp;
		}
		if(result && null != version){
			boolean temp = false;
			for(String ver:this.version){
				if(ver.equals(version)){
					temp = true;
					break;
				}
			}
			result = temp;
		}
		return result;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{type=");
		builder.append(this.type);
		builder.append(",parent=").append(this.parent);
		builder.append(",restName=[");
		if(null != this.method){
			for(String md : this.restName){
				builder.append(md).append(",");
			}
		}
		builder.append("],method=[");
		if(null != this.method){
			for(RestMethod md : this.method){
				builder.append(md).append(",");
			}
		}
		builder.append("],version=[");
		if(null != this.version){
			for(String md : this.version){
				builder.append(md).append(",");
			}
		}
		builder.append("],table=").append(this.table);
		builder.append(",beforeName=").append(this.beforeName);
		builder.append(",methodMappingInfo=[").append(this.methodMappingInfo);
		builder.append("]}");
		return builder.toString();
	}


}
