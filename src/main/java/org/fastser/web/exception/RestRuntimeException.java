package org.fastser.web.exception;

public class RestRuntimeException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * default constructor
	 */
	public RestRuntimeException() {
		super();
	}

	/**
	 * @param message
	 */
	public RestRuntimeException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public RestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public RestRuntimeException(Throwable cause) {
		super(cause);
	}
	
	

}