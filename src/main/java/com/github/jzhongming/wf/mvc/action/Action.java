package com.github.jzhongming.wf.mvc.action;

import com.github.jzhongming.wf.mvc.ActionResult;

public interface Action {

	public String path();

	public ActionResult invoke();

	public boolean matchHttpMethod(); // 验证POST GET
}