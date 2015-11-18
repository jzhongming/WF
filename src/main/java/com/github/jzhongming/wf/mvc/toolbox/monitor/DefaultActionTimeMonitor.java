package com.github.jzhongming.wf.mvc.toolbox.monitor;

public class DefaultActionTimeMonitor implements ActionTimeMonitor {

	private static final RequestCounter requestCounter = RequestCounter.instance();
	private static final TimeoutStats timeoutStats = TimeoutStats.instance();
	private static final ThreadLocal<Long> T_LOCAL = new ThreadLocal<Long>();

	public DefaultActionTimeMonitor() {
	}

	@Override
	public void start() {
		requestCounter.increment();
		T_LOCAL.set(System.currentTimeMillis());
	}

	@Override
	public void post() {
		try {
			long interval = System.currentTimeMillis() - T_LOCAL.get();
			RequestStats requestStats = requestCounter.decrement(interval);
			timeoutStats.log(interval, requestStats);
		} finally {
			T_LOCAL.remove();
		}

	}
}
