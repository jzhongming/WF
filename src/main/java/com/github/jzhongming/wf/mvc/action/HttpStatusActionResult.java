package com.github.jzhongming.wf.mvc.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Constant;

public class HttpStatusActionResult implements ActionResult {

	private int status;
	
	public HttpStatusActionResult(int status){
		this.status = status;
	}
	
	@Override
	public void render() {
		BeatContext beat = BeatContext.current();
		HttpServletRequest request = beat.getRequest();
		HttpServletResponse response = beat.getResponse();
		try {
			request.getRequestDispatcher("/resources/" + status + Constant.PAGESUFFIX).forward(request, response);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
