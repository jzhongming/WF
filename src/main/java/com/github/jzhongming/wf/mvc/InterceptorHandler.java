package com.github.jzhongming.wf.mvc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.jzhongming.wf.mvc.action.MethodAction;
import com.github.jzhongming.wf.mvc.initial.sys.InterceptorInit;

public final class InterceptorHandler {

	private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

	public static ActionResult excuteGlobalBeforeInterceptors() {

		ActionResult result = null;
		BeatContext beat = BeatContext.current();
		for (WFInterceptor interceptor : InterceptorInit.getGlobalInterceptors()) {
			result = interceptor.before(beat);
			if (result != null) {
				return result;
			}
		}

		return result;
	}

	public static ActionResult excuteGlobalAfterInterceptors() {
		ActionResult result = null;
		BeatContext beat = BeatContext.current();
		for (WFInterceptor interceptor : InterceptorInit.getGlobalInterceptors()) {
			result = interceptor.after(beat);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	public static void excuteGlobalComplet() {
		final BeatContext beat = (BeatContext) BeatContext.current().clone();

		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				for (WFInterceptor interceptor : InterceptorInit.getGlobalInterceptors()) {
					interceptor.complet(beat);
				}
			}
		});
	}

	public static ActionResult excuteActionBeforeInterceptors(MethodAction action) {
		ActionResult result = null;
		BeatContext beat = BeatContext.current();
		for (WFInterceptor interceptor : action.getInterceptors()) {
			result = interceptor.before(beat);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	public static ActionResult excuteActionAfterInterceptors(MethodAction action) {
		ActionResult result = null;
		BeatContext beat = BeatContext.current();
		for (WFInterceptor interceptor : action.getInterceptors()) {
			result = interceptor.after(beat);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	public static void excuteActionComplet(final MethodAction action) {
		final BeatContext beat = (BeatContext) BeatContext.current().clone();

		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				for (WFInterceptor interceptor : action.getInterceptors()) {
					interceptor.complet(beat);
				}
			}
		});
	}

	public static void destroy() {
		EXECUTOR.shutdown();
	}
}
