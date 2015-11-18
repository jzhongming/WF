package com.github.jzhongming.wf.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.jzhongming.wf.mvc.WFInterceptor;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Interceptor {
	
    Class<? extends WFInterceptor> value();

    Scope scope() default Scope.ACTION;
	
	public enum Scope {
		/**
		 * 作用于全局
		 */
		GLOBAL,
		
		/**
		 * 作用于某个Action
		 */
		ACTION;
	}
	
}