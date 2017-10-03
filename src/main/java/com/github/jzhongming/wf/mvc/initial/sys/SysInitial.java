package com.github.jzhongming.wf.mvc.initial.sys;

import javax.servlet.ServletContext;

public final class SysInitial {

	public static void initial(ServletContext sc) {
		try {
			ConfigInit.init();

			LoggerInit.init(sc); 

			ActionInit.init(sc);

			InterceptorInit.init();
			
			VelocityInit.init(sc);

			XssInit.init();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("SysInitial failed!!!");
			System.exit(0);
		}

	}

}
