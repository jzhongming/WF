package com.github.jzhongming.wf.mvc.action;

import java.io.IOException;
import java.io.PrintWriter;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Constant;

public class StringActionResult implements ActionResult {

	private final String str;
	private final int httpErrorCode;

	public StringActionResult(String str) {
		this(str, 0);
	}

	public StringActionResult(String str, int httpErrorCode) {
		this.str = str;
		this.httpErrorCode = httpErrorCode;
	}

	@Override
	public void render() {
		BeatContext beat = BeatContext.current();
		beat.getResponse().setHeader("Pragma", "No-cache");
		beat.getResponse().setHeader("Cache-Control", "no-cache");
		beat.getResponse().setHeader("Content-Type", "text/html; charset=" + Constant.ENCODING);
		beat.getResponse().setCharacterEncoding(Constant.ENCODING);
		beat.getResponse().setDateHeader("Expires", -1);
		if (0 == httpErrorCode) {
			PrintWriter pw = null;
			try {
				pw = beat.getResponse().getWriter();
				pw.write(str);
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (pw != null) {
					pw.close();
				}
			}
		} else {
			try {
				beat.getResponse().sendError(httpErrorCode, str);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

}
