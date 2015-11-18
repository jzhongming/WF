package com.github.jzhongming.wf.mvc.initial.sys;

import java.net.URL;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.util.LoaderUtil;

public final class LoggerInit {

	private static String configFileName = "logger.xml";

	public static void init(ServletContext sc) throws Exception {
		Log4jInit.init(sc);
	}

	public static class Log4jInit {

		public static void init(ServletContext sc) throws Exception {
			Collection<URL> configs = LoaderUtil.findResources(configFileName);
			if (!configs.isEmpty()) {
				URL url = configs.iterator().next();
				LoggerContext loggerContext = (LoggerContext) LogManager.getContext(true);
				loggerContext.setConfigLocation(url.toURI());
				loggerContext.start();
				System.out.println("Log4j2 init complete!");
			}
		}

	}

}
