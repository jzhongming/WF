package com.github.jzhongming.wf.mvc.beat.server;

import com.github.jzhongming.wf.mvc.BeatContext;

public class ServerInfo {

	private final BeatContext beat;
	
	private SessionHandler session = null;
	
	public ServerInfo(BeatContext beat) {
		this.beat = beat;
	}
	
	/**
	 * session处理
	 * @return
	 */
	public SessionHandler getSessions(){
		
		if(session == null)
			session = new SessionHandler(beat);
		
		return session;
	}
	
	
}
