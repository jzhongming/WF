package com.github.jzhongming.wf.mvc;

import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;

public abstract class WFController {

	/**
	 * 日志系统
	 */
	protected Logger _WFLOG = LoggerFactory.getLogger(this.getClass());

	protected BeatContext beat() {
		return BeatContext.current();
	}

}
