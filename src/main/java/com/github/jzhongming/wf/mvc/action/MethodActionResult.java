package com.github.jzhongming.wf.mvc.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.github.jzhongming.wf.mvc.ActionResult;
import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Constant;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;

public class MethodActionResult implements ActionResult {

	private final String suffix = Constant.PAGESUFFIX;

	private final String viewName;

	public MethodActionResult(String viewName) {
		this.viewName = viewName;
	}

	@Override
	public void render() {

		BeatContext beat = BeatContext.current();

		HttpServletResponse response = beat.getResponse();
		response.setContentType("text/html;charset=\"" + Constant.ENCODING + "\"");
		response.setCharacterEncoding(Constant.ENCODING);

		Context context = new VelocityContext(beat.getModel().getModel());
		VelocityWriter vw = null;
		try {
			vw = new VelocityWriter(response.getWriter());
			String path = "views" + "\\" + viewName + suffix;
			Template template = Velocity.getTemplate(path);
			template.merge(context, vw);
			vw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			vw.recycle(null);
		}
	}

	public static ActionResult view(String viewName) {
		return new MethodActionResult(viewName);
	}
}
