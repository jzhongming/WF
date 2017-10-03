package com.github.jzhongming.wf.mvc.initial.sys;

import com.github.jzhongming.wf.mvc.Config;
import com.github.jzhongming.wf.mvc.toolbox.xss.XssConverter;

public final class XssInit {

	public static void init() throws Exception {
		String xssPropertyPath = Config.getConfigFolder() + "/xss.properties";
		XssConverter.initProperty(xssPropertyPath);
	}
}
