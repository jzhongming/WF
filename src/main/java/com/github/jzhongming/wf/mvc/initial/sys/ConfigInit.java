package com.github.jzhongming.wf.mvc.initial.sys;

import java.io.File;
import java.io.InputStream;
import java.util.PropertyResourceBundle;

import static com.github.jzhongming.wf.mvc.Constant.*;

public final class ConfigInit {

	private static String NAMESPACE;

	private static String DISK;

	private static String MODE;

	private static String CLUSTER;

	private static String PACKAGESPACE;

	private static String LOGSPACEURL;

	public static void init() throws Exception {

		try {

			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			InputStream inputStream = cl.getResourceAsStream(PATH_SPACE_PROPERTIES);
			PropertyResourceBundle pp = new PropertyResourceBundle(inputStream);

			NAMESPACE = pp.containsKey(NAME_NAMESPACE) ? pp.getString(NAME_NAMESPACE) : "";
			if (NAMESPACE == null || "".equals(NAMESPACE.trim())) {
				throw new Exception("Does not specify a value for the namespace");
			}

			PACKAGESPACE = pp.containsKey(NAME_PACKAGESPACE) ? pp.getString(NAME_PACKAGESPACE) : "";
			if (PACKAGESPACE == null || "".equals(NAMESPACE.trim())) {
				throw new Exception("Do not specify a value for the packagespace");
			}

			LOGSPACEURL = pp.containsKey(NAME_LOGSPACE) ? pp.getString(NAME_LOGSPACE) : "";

			File file = new File(System.getProperty(PATH_USER_DIR));
			String path = file.getAbsolutePath().replace('\\', '/');
			DISK = path.substring(0, path.indexOf('/'));

			CLUSTER = System.getProperty(NAME_CLUSTERNAME);
			MODE = (CLUSTER == null || CLUSTER.trim().isEmpty()) ? NAME_OFFLINE : NAME_ONLINE;
			printConfiguration();
		} catch (Exception e) {
			System.err.println("META-INF in the classpath folder to ensure that there is 'space.properties' configuration file, "
					+ "and specifies the value namespace or vm parameters contain WF.clustername");
			throw new Exception("Config init failed!", e);
		}
	}

	private static void printConfiguration() {
		System.out.println("WF MODE: " + MODE + "\r\nWF CLUSTER:" + CLUSTER + "\r\nWF CONFIG_FOLDER:" + DISK + "\r\nWF PACKAGE_SPACE:" + PACKAGESPACE + "\r\nWF LOGSPACE_URL:" + LOGSPACEURL);
	}

	public static String getDisk() {
		return DISK;
	}

	public static boolean isONLINE() {
		return MODE.equals("ONLINE");
	}

	public static boolean isOFFLINE() {
		return !isONLINE();
	}

	public static String getNamespace() {
		return NAMESPACE;
	}

	public static String getMode() {
		return MODE;
	}

	public static String getCluster() {
		return CLUSTER;
	}

	public static String getPackageSpace() {
		return PACKAGESPACE;
	}

	public static String getLogSpaceURL() {
		return LOGSPACEURL;
	}
}
