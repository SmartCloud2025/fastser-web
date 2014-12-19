package org.fastser.web.context;


import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

public class RestContext<K, V> extends HashMap<K, V> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7241505484480779583L;

	protected String getString(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof String){
				return (String)v;
			}else{
				return String.valueOf(v);
			}
		}
		return null;
	}
	
	protected Integer getInteger(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof Integer){
				return (Integer)v;
			}else{
				String value = getString(key);
				if(StringUtils.isNotEmpty(value)){
					return Integer.valueOf(value);
				}
			}
		}
		return null;
	}
	
	protected int getInt(K key){
		Integer value = getInteger(key);
		if(null != value){
			return value;
		}
		return 0;
	}
	
	protected Double getDouble(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof Double){
				return (Double)v;
			}else{
				String value = getString(key);
				if(StringUtils.isNotEmpty(value)){
					return Double.valueOf(value);
				}
			}
		}
		return null;
	}
	
	protected Float getFloat(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof Float){
				return (Float)v;
			}else{
				String value = getString(key);
				if(StringUtils.isNotEmpty(value)){
					return Float.valueOf(value);
				}
			}
		}
		return null;
	}
	
	protected Boolean getBoolean(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof Boolean){
				return (Boolean)v;
			}else{
				String value = getString(key);
				if(StringUtils.isNotEmpty(value)){
					return Boolean.valueOf(value);
				}
			}
		}
		return null;
	}
	
	protected Date getDate(K key){
		V v = super.get(key);
		if(null != v){
			if(v instanceof Date){
				return (Date)v;
			}else{
				String value = getString(key);
				if(StringUtils.isNotEmpty(value)){
					//需要实现
					return null;
				}
			}
		}
		return null;
	}
	

}
