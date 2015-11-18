package com.github.jzhongming.wf.mvc.beat.server;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import com.github.jzhongming.wf.mvc.BeatContext;


public class SessionHandler {
	
	private final BeatContext beat;
	
	private final HttpSession session;

	public SessionHandler(BeatContext beat) {
		this.beat = beat;
		this.session = this.beat.getRequest().getSession();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#get(java.lang.String)
	 */
	public Object get(String name){
		return session.getAttribute(name);
		
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#getCreationTime()
	 */
	public Object getCreationTime(){
		return session.getCreationTime();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#getNames()
	 */
	public Enumeration<String> getNames(){
		return session.getAttributeNames();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#getId()
	 */
	public String getId(){
		return session.getId();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#getLastAccessedTime()
	 */
	public long getLastAccessedTime(){
		return session.getLastAccessedTime();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#getMaxInactiveInterval()
	 */
	public int getMaxInactiveInterval(){
		return session.getMaxInactiveInterval();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#invalidate()
	 */
	public void invalidate(){
		session.invalidate();
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#isNew()
	 */
	public boolean isNew(){
		return session.isNew();
	}
	
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#remove(java.lang.String)
	 */
	public void remove(String name){
		session.removeAttribute(name);
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#set(java.lang.String, java.lang.Object)
	 */
	public void set(String name, Object value){
		session.setAttribute(name, value);
	}
	
	/* (non-Javadoc)
	 * @see com.bj58.wf.mvc.server.Session#setMaxInactiveInterval(int)
	 */
	public void setMaxInactiveInterval(int value){
		session.setMaxInactiveInterval(value);
	}

	public void flush() {
	}
}
