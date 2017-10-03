package com.github.jzhongming.wf.mvc;

import java.util.Comparator;

import com.github.jzhongming.wf.mvc.annotation.Interceptor.Scope;
import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;

public abstract class WFInterceptor {
	
	protected Logger _WFLOG = LoggerFactory.getLogger(this.getClass());
	
	public abstract float order();
	
	public abstract ActionResult before(BeatContext beat);
	
	public abstract ActionResult after(BeatContext beat);
	
	public abstract void complet(BeatContext beat);
	
	public abstract Scope scope();
	
	public static final WFInteceptorSorter INTERCEPTOR_SORTER = new WFInteceptorSorter();
	
	/**
	 * 拦截器实例的排序比较器
	 */
	static final class WFInteceptorSorter implements Comparator<WFInterceptor>{
		
		WFInteceptorSorter() {}
		
		@Override
		public int compare(WFInterceptor o1, WFInterceptor o2) {
			return o1.order() > o2.order() ? 1 : 0;
		}
		
	}
}

