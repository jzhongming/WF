package com.github.jzhongming.wf.mvc.exception;

import com.github.jzhongming.wf.mvc.ActionResult;

public interface WFExceptionHandler<T extends Throwable> {
	
	public ActionResult handleException(T exception);

}

