package com.github.jzhongming.wf.mvc;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jzhongming.wf.mvc.exception.WFException;
import com.github.jzhongming.wf.mvc.exception.WFExceptionHandler;
import com.github.jzhongming.wf.mvc.initial.sys.AppInitial;
import com.github.jzhongming.wf.mvc.initial.sys.SysInitial;
import com.github.jzhongming.wf.mvc.toolbox.monitor.ActionTimeMonitor;
import com.github.jzhongming.wf.mvc.toolbox.monitor.DefaultActionTimeMonitor;

/**
 * 利用Filter对请求进行拦截，并完成处理的整体流程。
 *
 */
@WebFilter(urlPatterns = { "/*" }, asyncSupported = true)
public class WFBootstrap implements Filter {

	private Dispatcher dispatcher;

	private static final AtomicBoolean hasInit = new AtomicBoolean(false);

	private static final ActionTimeMonitor timeMonitor = new DefaultActionTimeMonitor();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (hasInit.get())
			return;

		synchronized (WFBootstrap.class) {

			if (hasInit.get())
				return;

			hasInit.set(true);

			SysInitial.initial(filterConfig.getServletContext());

			AppInitial.initial();

			dispatcher = new Dispatcher();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) request;

		HttpServletResponse httpResp = (HttpServletResponse) response;

		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding(Constant.ENCODING);

		BeatContext beat = BeatContext.register(httpReq, httpResp);
		ActionResult result = null;
		timeMonitor.start();
		try {

			result = InterceptorHandler.excuteGlobalBeforeInterceptors();
			if (result != null) {
				result.render();
				return;
			}

			result = dispatcher.service(beat);

			ActionResult afterResult = InterceptorHandler.excuteGlobalAfterInterceptors();
			if (afterResult != null) {
				result = afterResult;
			}

		} catch (Throwable e) {
			// 处理异常问题
			WFExceptionHandler<Throwable> handler = WFException.getHandler(e.getClass());
			result = handler.handleException(e);
		} finally {
			timeMonitor.post();
			if (result != null) {
				result.render();
			}

			try {
				InterceptorHandler.excuteGlobalComplet();
			} catch (Throwable e) {
				WFExceptionHandler<Throwable> handler = WFException.getHandler(e.getClass());
				handler.handleException(e);
			}
			BeatContext.clear();
		}
	}

	@Override
	public void destroy() {
		// 关闭拦截器线程
		InterceptorHandler.destroy();
		System.out.println("销毁拦截器");
	}

}
