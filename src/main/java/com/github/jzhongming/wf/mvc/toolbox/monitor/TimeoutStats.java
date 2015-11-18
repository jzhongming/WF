package com.github.jzhongming.wf.mvc.toolbox.monitor;

import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.Config;
import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;
import com.github.jzhongming.wf.mvc.utils.UDPClient;

public class TimeoutStats {

	private TimeoutStats() {
		String logspaceurl = Config.getLogSpaceURL();
		if (null == logspaceurl || logspaceurl.isEmpty()) {
			return;
		}

		String[] param = logspaceurl.split(":");
		if (param.length != 2) {
			throw new IllegalArgumentException(logspaceurl + " is format error; eg. ip:prot");
		}
		udpClient = new UDPClient(logspaceurl);
	}

	public static TimeoutStats instance() {
		return SingletonHolder.instance;
	}

	static class SingletonHolder {
		static TimeoutStats instance = new TimeoutStats();
	}

	private final static Logger timeoutLog = LoggerFactory.getLogger("TIMEOUT");

	private final static Logger statsLog = LoggerFactory.getLogger("STATS");

	private final int interval = 1000 * 60; // 1 min

	private RequestStats lastRequestStats = null;

	private static final String appName = Config.getNamespace();

	private final int TIME_VALVE = 499;
	// 超时数据收集
	private static UDPClient udpClient;

	public void log(long runtime, RequestStats currentRequestStats) {

		if (lastRequestStats == null) {
			lastRequestStats = currentRequestStats;
			return;
		}

		RequestStats freezeLastRequestStats = this.lastRequestStats; // 冻结

		if (currentRequestStats.getCurrentTime() <= freezeLastRequestStats.getCurrentTime())
			return;

		// 1min, log stats
		if ((currentRequestStats.getCurrentTime() - this.lastRequestStats.getCurrentTime()) >= interval) {
			statsLog.info(statsLog(currentRequestStats, freezeLastRequestStats));
			this.lastRequestStats = currentRequestStats;
		}

		if (hasTimeout(runtime, currentRequestStats, freezeLastRequestStats)) {
			String timeOutMessage = timeoutLog(runtime, currentRequestStats, freezeLastRequestStats);
			timeoutLog.info(timeOutMessage);
			if (null != udpClient) {
				udpClient.send(appName + ", " + timeOutMessage);
			}
		}

	}

	// 大于两倍平均时间，或者超过500MS算超时
	protected boolean hasTimeout(long runtime, RequestStats currentRequestStats, RequestStats lastRequestStats) {

		if (runtime > TIME_VALVE) {
			return true;
		}

		long timeDifference = currentRequestStats.getTotalTime() - lastRequestStats.getTotalTime();
		long countDifference = currentRequestStats.getTotalCount() - lastRequestStats.getTotalCount();

		return runtime > ((timeDifference << 1) / countDifference); // 2倍平均时长

	}

	protected String timeoutLog(long runtime, RequestStats currentRequestStats, RequestStats lastRequestStats) {
		return String.format("time: %sms, concurrent: %s, url: %s.", runtime, currentRequestStats.getConcurrentRequest(), BeatContext.current().getRequest().getRequestURI());
	}

	protected String statsLog(RequestStats currentRequestStats, RequestStats lastRequestStats) {

		long totalTime = currentRequestStats.getTotalTime() - lastRequestStats.getTotalTime();
		int count = currentRequestStats.getTotalCount() - lastRequestStats.getTotalCount();

		long averageTime = totalTime / count;

		long interval = currentRequestStats.getCurrentTime() - lastRequestStats.getCurrentTime();
		float count1000 = count * 1000.00f;
		float qps = (count1000) / interval;
		float concurrent = (totalTime * 1.00f) / interval;

		return String.format("1Min average time: %s, qps: %.2f, concurrent: %.2f, total time: %sms, request count: %s.", averageTime, qps, concurrent, totalTime, count);
	}

}
