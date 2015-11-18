//package com.github.jzhongming.wf.mvc.initial.sys;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.Reader;
//import java.util.Properties;
//
//import com.github.jzhongming.wf.mvc.Config;
//import com.google.common.base.Strings;
//import com.google.common.io.Closeables;
//
//public class CopyOfLoggerInit {
//
//	private static String configFileName = "wflog.properties";
//
//	public static void init() throws Exception {
//		Log4jInit log4jInit = new Log4jInit();
//		log4jInit.init();
//	}
//
//	public static class Log4jInit {
//
//		public void init() throws Exception {
//
//			Properties properties = getConfigLogProperties();
//
//			if (properties == null)
//				properties = defaultProperties();
//
//			PropertyConfigurator.configure(properties);
//
//		}
//
//		protected Properties getConfigLogProperties() throws Exception {
//
//			File configFile = new File(Config.getConfigFolder(), configFileName);
//
//			if (!configFile.exists())
//				return null;
//
//			Properties properties = new Properties();
//			Reader reader = null;
//			try {
//				reader = new FileReader(configFile);
//				properties.load(reader);
//			} catch (Exception e) {
//				System.err.println("fail to init log config file.");
//				throw new Exception("fail to init log config file.",e);
//			} finally {
//				Closeables.closeQuietly(reader);
//			}
//
//			if (!properties.containsKey("log4j.appender.file.File"))
//				properties.put("log4j.appender.file.File", defaultLogFile());
//
//			return properties;
//
//		}
//
//		protected Properties defaultProperties() {
//
//			Properties properties = new Properties();
//
//			properties.put("log4j.rootLogger", "INFO, file, stdout");
//			
//			properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
//			properties.put("log4j.appender.stdout.Target", "System.out");
//			properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
//			properties.put("log4j.appender.stdout.layout.ConversionPattern", "%m%n");
//			
//			properties.put("log4j.appender.file", "org.apache.log4j.DailyRollingFileAppender");
//			properties.put("log4j.appender.file.File", defaultLogFile());
//			properties.put("log4j.appender.file.DatePattern", "'.'yyyy-MM-dd");
//			properties.put("log4j.appender.file.Append", "true");
//			properties.put("log4j.appender.file.Threshold", "INFO");
//			properties.put("log4j.appender.file.layout", "org.apache.log4j.PatternLayout");
//			properties.put("log4j.appender.file.layout.ConversionPattern", "%d{ABSOLUTE} %5p %c{1}:%L - %m%n");
//
//			return properties;
//		}
//
//		private static String defaultLogFile() {
//
//			File logFolder = new File(Config.getLogPath());
//
//			String projectId = Config.getNamespace();
//
//			if (Strings.isNullOrEmpty(projectId))
//				projectId = "first";
//
//			File logFile = new File(logFolder, projectId + ".log");
//
//			return logFile.getAbsolutePath();
//		}
//	}
//}
