package com.github.jzhongming.wf.mvc.action;

import java.util.Set;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Dispatcher.HttpMethod;
import com.google.common.collect.ImmutableSet;

/**
 * 对静态文件处理，把所有静态文件名保存在set中，如何精确匹配，表明当前请求就是静态文件
 * 
 */

public class ResourceAction implements Action {

	private final String path;
	private Set<HttpMethod> supportMethods = HttpMethod.suportHttpMethods();

	public ResourceAction(String path) {
		this.path = path;
		initHttpMethods();
	}

	private void initHttpMethods() {
		supportMethods.add(HttpMethod.GET);
		supportMethods = ImmutableSet.copyOf(supportMethods);
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public ActionResult invoke() {
		return new ResourceActionResult();
	}

	@Override
	public boolean matchHttpMethod() {
		String requestMethod = BeatContext.current().getRequest().getMethod();
		String currentMethod = HttpMethod.parse(requestMethod);
		Boolean result = false;
		try {
			HttpMethod httpMethod = HttpMethod.valueOf(currentMethod);
			result = supportMethods.contains(httpMethod);
		} catch (Exception e) {
			result = false;
		}

		return result;
	}

}
