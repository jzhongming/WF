package com.github.jzhongming.wf.mvc.log;

public class Logger {

	private final org.apache.logging.log4j.Logger logger;

	public Logger(org.apache.logging.log4j.Logger logger2) {
		this.logger = logger2;
	}

	public void debug(String msg) {
		logger.debug(msg);
	}

	public void debug(String format, Object arg) {
		logger.debug(format, arg);
	}

	public void debug(String msg, Throwable t) {
		logger.debug(msg, t);
	}

	public void info(String msg) {
		logger.info(msg);
	}

	public void info(String format, Object arg) {
		logger.info(format, arg);
	}

	public void info(String msg, Throwable t) {
		logger.info(msg, t);
	}

	public void error(String msg) {
		logger.error(msg);
	}

	public void error(String msg, Throwable t) {
		logger.error(msg, t);
	}

	public void error(String format, Object arg) {

		logger.error(format, arg);
	}

	public String toString() {

		return logger.toString();
	}

	public boolean isDebugEnabled() {

		return logger.isDebugEnabled();
	}

	public boolean isInfoEnabled() {

		return logger.isInfoEnabled();
	}

	public boolean isWarnEnabled() {

		return logger.isWarnEnabled();
	}

	public void warn(String msg) {

		logger.warn(msg);
	}

	public void warn(String msg, Throwable t) {

		logger.warn(msg, t);
	}

	public boolean isErrorEnabled() {

		return logger.isErrorEnabled();
	}

	public String getName() {

		return logger.getName();
	}

	public void warn(String format, Object arg) {
		logger.warn(format, arg);
	}
}
