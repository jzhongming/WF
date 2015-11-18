package com.github.jzhongming.wf.mvc.beat.client;

import javax.servlet.http.HttpServletRequest;

import com.github.jzhongming.wf.mvc.BeatContext;
import com.github.jzhongming.wf.mvc.WFHttpServletRequestWrapper;

public class ClientInfo {

	private final BeatContext beat;

	private CookieHandler cookie = null;

	public ClientInfo(BeatContext beat) {
		this.beat = beat;
	}

	public CookieHandler getCookies() {
		if (cookie == null) {
			cookie = new CookieHandler(beat);
		}

		return cookie;
	}

	public WFHttpServletRequestWrapper getUploads() {
		HttpServletRequest request = beat.getRequest();
		return (request instanceof WFHttpServletRequestWrapper) ? (WFHttpServletRequestWrapper) request : null;
	}

	public boolean isUpload() {
		return getUploads() == null ? false : true;
	}

}
