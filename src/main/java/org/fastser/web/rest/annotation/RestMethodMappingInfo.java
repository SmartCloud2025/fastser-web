package org.fastser.web.rest.annotation;

import org.apache.commons.lang3.StringUtils;


public class RestMethodMappingInfo{
	
	private String className;
	
	private String methodName;
	
	public RestMethodMappingInfo() {
	}

	public RestMethodMappingInfo(String className, String methodName) {
		super();
		this.className = className;
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && obj instanceof RestMethodMappingInfo) {
			RestMethodMappingInfo other = (RestMethodMappingInfo) obj;
			boolean result = true;
			if(StringUtils.isNotEmpty(this.className)){
				result = result && this.className.equals(other.getClassName());
			}
			if(StringUtils.isNotEmpty(this.methodName)){
				result = result && this.methodName.equals(other.getMethodName());
			}
			return result;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 0;
		if (result == 0) {
			result = this.className.hashCode();
			result = 31 * result + this.methodName.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{className=");
		builder.append(this.className);
		builder.append(",methodName=").append(this.methodName);
		builder.append('}');
		return builder.toString();
	}


}
