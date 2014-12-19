package org.fastser.web.rest.handler;


public class RestHandlerMethod {
	
	private String table;
	
	private final String beanName;

	private final String methodName;
	
	public RestHandlerMethod(String beanName, String methodName) {
		super();
		this.beanName = beanName;
		this.methodName = methodName;
	}
	


	public String getBeanName() {
		return beanName;
	}



	public String getMethodName() {
		return methodName;
	}
	

	public String getTable() {
		return table;
	}



	public void setTable(String table) {
		this.table = table;
	}



	@Override
	public String toString() {
		return beanName+"."+methodName;
	}

}
