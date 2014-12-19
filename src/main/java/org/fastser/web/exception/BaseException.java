package org.fastser.web.exception;

import org.apache.commons.lang3.StringUtils;

public class BaseException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    protected int number;

    /**
     * default constructor
     */
    public BaseException() {
        super();
    }

    /**
     * @param message
     */
    public BaseException(String message) {
        super(resolveMessage(message));
        String num = resolveNumber(message);
        if (StringUtils.isNotEmpty(num)) {
            this.number = Integer.parseInt(num);
        }
    }

    /**
     * @param message
     * @param cause
     */
    public BaseException(String message, Throwable cause) {
        super(resolveMessage(message), cause);
        String num = resolveNumber(message);
        if (StringUtils.isNotEmpty(num)) {
            this.number = Integer.parseInt(num);
        }
    }

    /**
     * @param cause
     */
    public BaseException(Throwable cause) {
        super(cause);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    static String resolveMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            if (message.indexOf("-") != -1) {
                String[] msgs = message.split("-");
                return msgs[1];
            } else {
                return message;
            }
        }
        return null;
    }

    static String resolveNumber(String message) {
        if (StringUtils.isNotEmpty(message)) {
            if (message.indexOf("-") != -1) {
                String[] msgs = message.split("-");
                return msgs[0];
            } else {
                return null;
            }
        }
        return null;
    }

}