package com.github.jzhongming.wf.mvc.action;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Dispatcher.HttpMethod;
import com.github.jzhongming.wf.mvc.InterceptorHandler;
import com.github.jzhongming.wf.mvc.RequestBinder;
import com.github.jzhongming.wf.mvc.WFInterceptor;
import com.github.jzhongming.wf.mvc.annotation.Interceptor;
import com.github.jzhongming.wf.mvc.exception.WFException;
import com.github.jzhongming.wf.mvc.utils.AnnotationUtils;
import com.github.jzhongming.wf.mvc.utils.PrimitiveConverter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * 
 */
public class MethodAction implements Action {

	protected Object controller;

	protected Method method;

	protected String pathPattern;

	/**
	 * 方法上所有参数名，按顺序排列
	 */
	protected List<String> paramNames;

	/**
	 * 方法上所有参数类型，按顺序排列
	 */
	protected List<Class<?>> paramTypes;

	/**
	 * 所有annotation，包括并覆盖controller上的annotation，
	 */
	protected Set<Annotation> annotations;

	/**
	 * 是否是模版匹配
	 */
	protected boolean isPattern;

	private List<WFInterceptor> interceptors = Lists.newArrayList();

	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static final PrimitiveConverter converter = PrimitiveConverter.INSTANCE;

	private Set<HttpMethod> supportMethods = HttpMethod.suportHttpMethods();

	public MethodAction(Object controller, Method method, String pathPattern, boolean isGet, boolean isPost, List<String> paramNames, List<Class<?>> paramTypes, Set<Annotation> annotations)
			throws Exception {

		this.controller = controller;
		this.method = method;
		this.pathPattern = pathPattern;
		this.paramNames = paramNames;
		this.paramTypes = paramTypes;
		this.annotations = annotations;
		this.isPattern = pathMatcher.isPattern(pathPattern) || paramTypes.size() > 0;
		this.interceptors = generateActionInterceptors();
		initHttpMethods(isGet, isPost);
	}

	private void initHttpMethods(boolean isGet, boolean isPost) {
		if (isGet) {
			supportMethods.add(HttpMethod.GET);
		}
		if (isPost) {
			supportMethods.add(HttpMethod.POST);
		}
		supportMethods = ImmutableSet.copyOf(supportMethods);
	}

	// 查找拦截器注解
	private List<WFInterceptor> generateActionInterceptors() throws Exception {
		for (Annotation annotation : annotations) {
			try {
				parseWFInterceptor(annotation);
			} catch (Exception e) {
				throw new Exception("Generate action interceptor failed.", e);
			}
		}
		// 根据Order值排序拦截器
		Collections.sort(interceptors, WFInterceptor.INTERCEPTOR_SORTER);

		return ImmutableList.copyOf(interceptors);
	}

	public Method getMethod() {
		return method;
	}
	
	public Object getController() {
		return controller;
	}

	private void parseWFInterceptor(Annotation annotation) throws Exception {

		Interceptor interAnnotation = (Interceptor) ((annotation.annotationType() == Interceptor.class) ? annotation : null);
		if (interAnnotation == null) {
			interAnnotation = AnnotationUtils.findAnnotation(annotation.getClass(), Interceptor.class);
		}
		if (interAnnotation == null) {
			return;
		}

		WFInterceptor mvcInterceptor = interAnnotation.value().newInstance();
		interceptors.add(mvcInterceptor);
	}

	@Override
	public String path() {
		return pathPattern;
	}

	public boolean isPattern() {
		return isPattern;
	}

	public List<WFInterceptor> getInterceptors() {

		return interceptors;
	}

	// 数据绑定
	private Object[] matchValues() {
		if (null == paramTypes || paramTypes.isEmpty()) {
			return null;
		}

		Object[] params = new Object[paramTypes.size()];

		BeatContext beat = BeatContext.current();
		String uri = beat.getRequest().getRequestURI();
		Map<String, String> urlParams = pathMatcher.extractUriTemplateVariables(this.pathPattern, uri);
		for (int i = 0; i < paramNames.size(); i++) {
			String paramName = paramNames.get(i);
			Class<?> clazz = paramTypes.get(i);
			String v = urlParams.get(paramName);
			// 普通类型直接bind
			if (v != null) {
				if (converter.canConvert(clazz)) {
					params[i] = converter.convert(clazz, v);
					continue;
				}
			}

			if (converter.canConvert(clazz))
				continue;

			params[i] = RequestBinder.bindAndValidate(clazz);
		}
		return params;
	}

	@Override
	public ActionResult invoke() {
		Object result = null;
		Object preResult = null;

		preResult = InterceptorHandler.excuteActionBeforeInterceptors(this);
		if (preResult != null) {
			return (ActionResult) preResult;
		}

		try {
			result = method.invoke(controller, matchValues());
		} catch (IllegalArgumentException e) {
			throw new WFException(e);
		} catch (IllegalAccessException e) {
			throw new WFException(e);
		} catch (InvocationTargetException e) {
			throw new WFException(e);
		}

		ActionResult afterResult = InterceptorHandler.excuteActionAfterInterceptors(this);
		if (afterResult != null) {
			result = afterResult;
		}

		InterceptorHandler.excuteActionComplet(this);
		return (ActionResult) result;
	}

	@Override
	public boolean matchHttpMethod() {
		String requestMethod = BeatContext.current().getRequest().getMethod();
		String currentMethod = HttpMethod.parse(requestMethod);
		HttpMethod httpMethod = null;
		try {
			httpMethod = HttpMethod.valueOf(currentMethod);
		} catch (Exception e) {
			return false;
		}

		return supportMethods.contains(httpMethod);
	}

}
