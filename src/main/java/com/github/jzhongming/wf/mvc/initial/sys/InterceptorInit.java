package com.github.jzhongming.wf.mvc.initial.sys;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.github.jzhongming.wf.mvc.Config;
import com.github.jzhongming.wf.mvc.WFInterceptor;
import com.github.jzhongming.wf.mvc.annotation.Interceptor.Scope;
import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;
import com.github.jzhongming.wf.mvc.scan.DefaultClassScanner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public final class InterceptorInit {

	private static List<WFInterceptor> globalInterceptors;
	private static final Logger _WFLOG = LoggerFactory.getLogger(InterceptorInit.class);

	public static List<WFInterceptor> getGlobalInterceptors() {
		return globalInterceptors;
	}

	public static void init() throws Exception {
		buildGlobalInterceptors();
	}
	// 初始化全局拦截器在Bootstrap的doFilter中实现拦截
	private static void buildGlobalInterceptors() throws Exception {

		Set<Class<? extends WFInterceptor>> interceptorsClasses = getIntercepter();
		List<WFInterceptor> interceptors = Lists.newArrayList();

		for (Class<? extends WFInterceptor> clazz : interceptorsClasses) {
			WFInterceptor interceptor = null;
			try {
				interceptor = clazz.newInstance();
				if (Scope.GLOBAL == interceptor.scope()) {
					interceptors.add(interceptor);
				}
			} catch (Exception e) {
				_WFLOG.error("Build global interceptor failed, Interceptor: " + clazz.getName(), e);
				throw new Exception("Build global interceptor failed!", e);
			}
		}

		// 根据order进行排序
		Collections.sort(interceptors, WFInterceptor.INTERCEPTOR_SORTER);
		for (WFInterceptor interceptor : interceptors) {
			_WFLOG.info("Load Global Interceptor : " + interceptor.getClass().getName());
		}

		globalInterceptors = ImmutableList.copyOf(interceptors);

	}

	@SuppressWarnings("unchecked")
	private static Set<Class<? extends WFInterceptor>> getIntercepter() throws Exception {
		_WFLOG.info("scan interceptors start...");
		Set<Class<?>> clazzSet = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(), ".*\\.interceptors\\..*Interceptor");
		ImmutableSet.Builder<Class<? extends WFInterceptor>> builder = ImmutableSet.builder();
		for (Class<?> clazz : clazzSet) {
			if (WFInterceptor.class.isAssignableFrom(clazz) && !Modifier.isInterface(clazz.getModifiers()) && !Modifier.isAbstract(clazz.getModifiers()) && Modifier.isPublic(clazz.getModifiers())) {
				builder.add((Class<? extends WFInterceptor>) clazz);
			}
		}
		_WFLOG.info("scan interceptors complete!");
		return builder.build();
	}

}
