package org.fastser.web.rest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestMapping {
	
	/**
	 * The path or name of the rest api.
	 */
	String value() default "";
	
	/**
	 * The HTTP request methods type:
	 * GET, POST, HEAD, OPTIONS, PUT, DELETE.
	 */
	String[] method() default {};
	
	String[] version() default {};
	
	String table() default "";
	
	/**
	 * default handle method, value:
	 * get, list, put, post, delete
	 * @return
	 */
	String beforeName() default "";

}
