package com.github.jzhongming.wf.mvc.exception;

import java.util.Map;

import com.google.common.collect.Maps;

public class WFException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private static final Map<Class<?>, WFExceptionHandler<Throwable>> HANDLERS = Maps.newConcurrentMap();

	public static <T extends Throwable> void setHandler(Class<T> clazz, WFExceptionHandler<Throwable> handler) {
		HANDLERS.put(clazz, handler);
	}

	public static <T extends Throwable> WFExceptionHandler<Throwable> getHandler(Class<T> clazz) {
		WFExceptionHandler<Throwable> handler = HANDLERS.get(clazz);

		if (handler == null)
			return getDefaultExceptionHandler();

		return handler;
	}

	public static WFExceptionHandler<Throwable> getDefaultExceptionHandler() {
		return DefaultWFExceptionHandler.INSTANCE;
	}

	public WFException(Throwable e) {
		super(e);
	}

}
