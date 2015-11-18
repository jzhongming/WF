package com.github.jzhongming.wf.mvc.initial.sys;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import com.github.jzhongming.wf.mvc.Config;
import com.github.jzhongming.wf.mvc.action.Action;
import com.github.jzhongming.wf.mvc.action.AntPathMatcher;
import com.github.jzhongming.wf.mvc.action.MethodAction;
import com.github.jzhongming.wf.mvc.action.ResourceAction;
import com.github.jzhongming.wf.mvc.annotation.GET;
import com.github.jzhongming.wf.mvc.annotation.POST;
import com.github.jzhongming.wf.mvc.annotation.Path;
import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;
import com.github.jzhongming.wf.mvc.scan.DefaultClassScanner;
import com.github.jzhongming.wf.mvc.scan.DefaultMethodScanner;
import com.github.jzhongming.wf.mvc.utils.AnnotationUtils;
import com.github.jzhongming.wf.mvc.utils.ClassUtils;
import com.github.jzhongming.wf.mvc.utils.Pair;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ActionInit {
	private static final Logger _WFLOG = LoggerFactory.getLogger(ActionInit.class);
	// 匹配方式的Action
	private static final Map<String, Set<MethodAction>> patternActions = Maps.newHashMap();
	// 固定路径Action
	private static final Map<String, Set<Action>> resourceActions = Maps.newHashMap();

	public static final Map<String, Set<MethodAction>> getPatternActions() {
		return patternActions;
	}

	public static final Set<Action> getResourceActions(final String path) {
		return resourceActions.get(path);
	}

	public static void init(ServletContext sc) throws Exception {
		List<MethodAction> methodActions = PatternActionInit.getPatternActions();
		for (MethodAction action : methodActions) {
			if (action.isPattern()) {
				if (!patternActions.containsKey(action.path())) {
					patternActions.put(action.path(), new HashSet<MethodAction>());
				}
				patternActions.get(action.path()).add(action);
			} else {
				if (!resourceActions.containsKey(action.path())) {
					resourceActions.put(action.path(), new HashSet<Action>());
				} else {
					_WFLOG.warn("Exist same Action path : " + action.path() + "\r\n > Controller : " + action.getController().getClass().getName() + "\r\n > Method : " + action.getMethod().getName());
				}
				resourceActions.get(action.path()).add(action);
			}
		}

		List<Action> resourceActionsList = ResourceActionInit.getResuourceActions(sc);

		for (Action action : resourceActionsList) {
			if (resourceActions.containsKey(action.path())) {// 方法注解路径与文件路径冲突
				_WFLOG.warn("Exist same Action path :" + action.path());
				continue;
			}

			Set<Action> actionList = Sets.newHashSet();
			actionList.add(action);
			resourceActions.put(action.path(), actionList);
		}
	}

}

class PatternActionInit {
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();
	private static final Map<String, Object> controllers = Maps.newHashMap();
	private static final Logger _WFLOG = LoggerFactory.getLogger(PatternActionInit.class);

	static List<MethodAction> getPatternActions() throws Exception {
		_WFLOG.info("scan controllers start ...");
		Set<Class<?>> controllerClasses = DefaultClassScanner.getInstance().getClassList(Config.getPackageSpace(), ".*\\.controllers\\..*Controller");
		// controllerClasses.addAll(classScaner.getClassListBySuper("com.bj58",
		// WFController.class));// 这里破坏了定义，是否需要加上？
		List<MethodAction> actions = Lists.newArrayList();
		for (Class<?> controllerClazz : controllerClasses) {
			actions.addAll(analyze(controllerClazz));// 抽取Controller中的Action
		}
		_WFLOG.info("scan controolers complete!");
		return ImmutableList.copyOf(actions);
	}

	private static List<MethodAction> analyze(Class<?> clazz) throws Exception {
		Path onControllerPath = AnnotationUtils.findAnnotation(clazz, Path.class); // 获得Controller上的Path注解
		String[] onControllerUrl = getPathurls(onControllerPath);
		List<Method> mList = DefaultMethodScanner.getInstance().getMethodListByAnnotation(clazz, Path.class);// 获得Path注解的方法
		List<MethodAction> actions = Lists.newArrayList();
		for (int i = 0; i < onControllerUrl.length; i++) {
			for (Method method : mList) {// 组装成Action
				actions.addAll(getMethodAction(onControllerUrl[i], clazz, method));
			}
		}

		return actions;
	}

	private static List<MethodAction> getMethodAction(String pathUrl, Class<?> clazz, Method method) throws Exception {
		Path pathAnnotation = AnnotationUtils.findAnnotation(method, Path.class);
		String[] onMethodUri = pathAnnotation.value();
		List<MethodAction> actions = Lists.newArrayList();

		for (String methodUri : onMethodUri) {
			String pathPattern = combinePathPattern(pathUrl, methodUri); // 组装URL,//这里组合会有重复URL
			_WFLOG.debug("methodUri >>> " + pathPattern + " >> " + method.getName());
			ImmutableList<String> paramNames = ImmutableList.copyOf(ClassUtils.getMethodParamNames(clazz, method));
			List<Class<?>> paramTypes = ImmutableList.copyOf(method.getParameterTypes());

			Set<Annotation> annotationsOfClass = Sets.newHashSet(clazz.getAnnotations()); // Controller类上的注解
			Set<Annotation> annotationsOfMethod = Sets.newHashSet(method.getAnnotations()); // 方法上的注解
			Builder<Annotation> builder = ImmutableSet.builder();
			builder.addAll(annotationsOfClass).addAll(annotationsOfMethod);
			Set<Annotation> annotations = builder.build(); // 这里查找用户自定义拦截器注解

			boolean[] httpSupport = pickUpHttpMethod(method);// 设置请求类型
			Object controller = controllers.get(clazz.getPackage() + clazz.getName());
			if (controller == null) {
				controller = clazz.newInstance();
				controllers.put(clazz.getPackage() + clazz.getName(), controller);
			}
			actions.add(new MethodAction(controller, method, pathPattern, httpSupport[0], httpSupport[1], paramNames, paramTypes, annotations));
		}

		return actions;
	}

	/**
	 * path ==> /path
	 * 
	 * @param pathUrl
	 * @return
	 */
	private static String prefixPathPattern(String pathUrl) {
		if (!pathUrl.isEmpty() && pathUrl.charAt(0) != '/')
			pathUrl = '/' + pathUrl;
		
		return pathUrl;
	}

	/**
	 * path/ ==> path path///// ==> path
	 * 
	 * @param pathUrl
	 * @return
	 */
	private static String suffixPathPattern(String pathUrl) {
		while (!pathUrl.isEmpty() && pathUrl.endsWith("/")) {
			pathUrl = pathUrl.substring(0, pathUrl.length() - 1);
		}
		return pathUrl;
	}

	/**
	 * 连接两个URIPath
	 * 
	 * @param typePath
	 * @param methodPath
	 * @return
	 */
	private static String combinePathPattern(String typePath, String methodPath) {
		if(typePath.equals("/")) {
			typePath = "";
		}
		String uri = pathMatcher.combine(suffixPathPattern(typePath), prefixPathPattern(methodPath));
		return uri.equals("/") ? "/" : suffixPathPattern(uri);
	}

	private static boolean[] pickUpHttpMethod(Method method) {

		GET getAnnotaion = AnnotationUtils.findAnnotation(method, GET.class);
		POST posttAnnotaion = AnnotationUtils.findAnnotation(method, POST.class);

		if (getAnnotaion == null && posttAnnotaion != null)
			return new boolean[] { false, true };

		if (getAnnotaion != null && posttAnnotaion == null)
			return new boolean[] { true, false };

		return new boolean[] { true, true };
	}

	private static String[] getPathurls(Path path) {
		String[] pathUrls = (path == null) ? new String[] { "/" } : path.value();
		for (String pathUrl : pathUrls) {
			pathUrl = prefixPathPattern(pathUrl);
		}

		return pathUrls;
	}

}

class ResourceActionInit {

	/**
	 * 静态文件名set
	 */
	private static List<String> staticFiles = Lists.newArrayList();

	/**
	 * 不允许访问的文件或文件夹
	 */
	private static final Set<String> forbitPath = ImmutableSet.of("");

	static List<Action> getResuourceActions(ServletContext servletContext) {

		String resourceFolder = servletContext.getRealPath("/") + "/resources";
		final File staticResourcesFolder = new File(resourceFolder);

		try {
			staticFiles = findFiles(staticResourcesFolder, forbitPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Action> staticFileActions = Lists.newArrayList();
		for (String staticFile : staticFiles) {
			staticFileActions.add(new ResourceAction(staticFile));
		}

		return staticFileActions;
	}

	static List<String> findFiles(File directory, Set<String> forbitPath) throws Exception {

		List<String> staticFiles = Lists.newArrayList();

		Deque<Pair<File, String>> dirs = Lists.newLinkedList();

		dirs.add(Pair.build(directory, "/"));

		while (dirs.size() > 0) {
			Pair<File, String> pop = dirs.pop();

			File[] files = pop.getKey().listFiles();

			if (files != null) {
				for (File file : files) {
					String name = pop.getValue() + file.getName();

					if (forbitPath.contains(name))
						continue;

					if (file.isFile()) {
						staticFiles.add(name);
					} else {
						dirs.push(Pair.build(file, name + '/'));
					}
				}
			}
		}

		return staticFiles;
	}
}
