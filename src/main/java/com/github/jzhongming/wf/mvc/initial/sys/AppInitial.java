package com.github.jzhongming.wf.mvc.initial.sys;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import com.github.jzhongming.wf.mvc.AppInit;
import com.github.jzhongming.wf.mvc.Config;
import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;
import com.github.jzhongming.wf.mvc.scan.DefaultClassScanner;
import com.google.common.collect.ImmutableSet;

public final class AppInitial {

	private static Set<Class<? extends AppInit>> inits;
	private static final Logger _WFLOG = LoggerFactory.getLogger(AppInitial.class);

	public static void initial() {

		inits = getInits();

		for (Class<?> init : inits) {
			try {
				Method initMethod = init.getMethod("init");
				initMethod.invoke(init.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	@SuppressWarnings("unchecked")
	private static Set<Class<? extends AppInit>> getInits() {
		_WFLOG.info("scan initial start...");
		Set<Class<?>> sets = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(), ".*\\.inits\\..*Init");
		ImmutableSet.Builder<Class<? extends AppInit>> builder = ImmutableSet.builder();
		for (Class<?> clazz : sets) {
			if (AppInit.class.isAssignableFrom(clazz) 
					&& !Modifier.isInterface(clazz.getModifiers()) 
					&& !Modifier.isAbstract(clazz.getModifiers()) 
					&& Modifier.isPublic(clazz.getModifiers())) {
				builder.add((Class<? extends AppInit>) clazz);
			}
		}
		_WFLOG.info("scan initial complete!");
		return builder.build();
	}

}
