package com.github.jzhongming.wf.mvc;

import java.util.HashMap;
import java.util.Map;

import com.github.jzhongming.wf.mvc.initial.sys.ConfigInit;
import static com.github.jzhongming.wf.mvc.Constant.*;

public class Config {

	private static Map<String, String> properties = new HashMap<String, String>();

	/**
	 * 获取根路径
	 * 
	 * @return
	 */
	public static String getRootPath() {
		String rootPath;

		if (NAME_ONLINE.equals(ConfigInit.getMode()))
			rootPath = ConfigInit.getDisk() + "/opt/web/" + ConfigInit.getCluster();

		else
			rootPath = ConfigInit.getDisk() + "/opt/wf/" + ConfigInit.getNamespace();

		return rootPath;
	}

	public static String getPackageSpace() {
		return ConfigInit.getPackageSpace();
	}

	public static String getLogSpaceURL() {
		return ConfigInit.getLogSpaceURL();
	}

	/**
	 * 获取命名空间
	 * 
	 * @return
	 */
	public static String getNamespace() {
		return ConfigInit.getNamespace();
	}

	/**
	 * 获取配置路径
	 * 
	 * @return
	 */
	public static String getConfigFolder() {

		String configFolder;

		if (NAME_ONLINE.equals(ConfigInit.getMode()))
			configFolder = getRootPath() + "/wf/conf/" + ConfigInit.getNamespace() + "/";
		else
			configFolder = getRootPath() + "/conf/";

		return configFolder;
	}

	/**
	 * 获取WF日志路径
	 * 
	 * @return
	 */
	public static String getLogPath() {

		String logPath;
		if (NAME_ONLINE.equals(ConfigInit.getMode()))
			logPath = getRootPath() + "/wf/logs/" + ConfigInit.getNamespace() + "/";
		else
			logPath = getRootPath() + "/logs/";

		return logPath;
	}

	public static String getProperty(String key) {
		return properties.get(key);
	}

	public static void setProperty(String key, String value) throws Exception {

		if (properties.get(key) != null)
			throw new Exception("Property key exist: " + key);

		properties.put(key, value);

	}

}
