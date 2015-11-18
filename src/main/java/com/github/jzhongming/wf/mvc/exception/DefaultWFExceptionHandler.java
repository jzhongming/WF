package com.github.jzhongming.wf.mvc.exception;

import javax.servlet.http.HttpServletResponse;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.action.StringActionResult;

class DefaultWFExceptionHandler implements WFExceptionHandler<Throwable> {

	static final WFExceptionHandler<Throwable> INSTANCE = new DefaultWFExceptionHandler();

	private DefaultWFExceptionHandler() {
	}

	@Override
	public ActionResult handleException(Throwable exception) {
		exception.printStackTrace();
		return new StringActionResult("WF exception : " + exception.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
