package com.github.jzhongming.wf.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.jzhongming.wf.mvc.action.Action;
import com.github.jzhongming.wf.mvc.beat.client.ClientInfo;
import com.github.jzhongming.wf.mvc.beat.server.ServerInfo;

/**
 * 管理一个客户端请求的生命周期
 */
public class BeatContext implements Cloneable {

	private static final ThreadLocal<BeatContext> STORE = new ThreadLocal<BeatContext>();

	private HttpServletRequest request;

	private HttpServletResponse response;

	private Model model;

	private ClientInfo clientInfo;

	private ServerInfo serverInfo;

	private Action action;

	private BeatContext() {
	}

	private BeatContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.clientInfo = new ClientInfo(this);
		this.serverInfo = new ServerInfo(this);
		this.model = new Model();
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	static BeatContext register(HttpServletRequest request, HttpServletResponse response) {
		BeatContext beat = new BeatContext(WFHttpServletRequestWrapper.wrapper(request), response);
		STORE.set(beat);
		return beat;
	}

	static void clear() {
		STORE.remove();
	}

	public static BeatContext current() {
		return STORE.get();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Model getModel() {
		return model;
	}

	public ClientInfo getClient() {
		return clientInfo;
	}

	public ServerInfo getServer() {
		return serverInfo;
	}

	@Override
	public Object clone() {
		BeatContext currentBeat = current();
		BeatContext copyBeat = new BeatContext();
		copyBeat.request = currentBeat.request;
		copyBeat.response = null;
		copyBeat.clientInfo = currentBeat.clientInfo;
		copyBeat.serverInfo = currentBeat.serverInfo;
		copyBeat.model = currentBeat.model;
		return copyBeat;
	}
}