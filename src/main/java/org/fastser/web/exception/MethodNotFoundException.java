package org.fastser.web.exception;

public class MethodNotFoundException extends BaseException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * default constructor
	 */
	public MethodNotFoundException() {
		super();
	}

	/**
	 * @param message
	 */
	public MethodNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MethodNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public MethodNotFoundException(Throwable cause) {
		super(cause);
	}

}
