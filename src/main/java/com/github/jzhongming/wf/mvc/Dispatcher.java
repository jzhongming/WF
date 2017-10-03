package com.github.jzhongming.wf.mvc;

import java.util.Map;
import java.util.Set;

import com.github.jzhongming.wf.mvc.action.Action;
import com.github.jzhongming.wf.mvc.action.AntPathMatcher;
import com.github.jzhongming.wf.mvc.action.HttpStatusActionResult;
import com.github.jzhongming.wf.mvc.action.MethodAction;
import com.github.jzhongming.wf.mvc.initial.sys.ActionInit;
import com.github.jzhongming.wf.mvc.utils.PathUtils;
import com.google.common.collect.Sets;

public class Dispatcher {

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	public ActionResult service(BeatContext beat) {
		
		Action action = findAction(beat);
		return (null == action) ? new HttpStatusActionResult(404) : action.invoke();
	}

	// 这里有优化空间
	private Action findAction(BeatContext beat) {

		String uri = beat.getRequest().getRequestURI();
		String contextPath = beat.getRequest().getContextPath();
		String relativeUrl = uri.substring(contextPath.length());
		String simplyPath = PathUtils.simplyWithoutSuffix(relativeUrl);
		String bagPath = PathUtils.simplyWithoutSuffix(simplyPath);
		
//		System.out.println("------------------------------------------");
//		System.out.println("uri:" + uri);
//		System.out.println("contextPath:" + contextPath);
//		System.out.println("relativeUrl:" + relativeUrl);
//		System.out.println("simplyPath:" + simplyPath);
//		System.out.println("bagPath:" + bagPath);
//		System.out.println("------------------------------------------");
		
		Set<Action> exactActionMap = ActionInit.getResourceActions(bagPath);
		if (exactActionMap != null) {
			for (Action action : exactActionMap) {
				if (action.matchHttpMethod()) {
					return action;
				}
			}
		}

		Map<String, Set<MethodAction>> ministyActions = ActionInit.getPatternActions();
		if (ministyActions != null) {
			for (String path : ministyActions.keySet()) {
				boolean match = pathMatcher.doMatch(path, bagPath, true, null);
				if (match) {
					Set<MethodAction> actions = ministyActions.get(path);
					for (MethodAction action : actions) {
						if (action.matchHttpMethod()) {
							return action;
						}
					}
				}
			}
		}

		return null;
	}

	public enum HttpMethod {

		GET,

		POST,

		HEAD;

		public static String parse(String method) {
			if (method == null || method.isEmpty())
				return null;

			return method.toUpperCase();
		}

		public static Set<HttpMethod> suportHttpMethods() {
			Set<HttpMethod> methods = Sets.newHashSet();
			methods.add(HEAD);
			return methods;
		}

	}

}
