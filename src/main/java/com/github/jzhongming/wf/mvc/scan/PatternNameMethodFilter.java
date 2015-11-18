package com.github.jzhongming.wf.mvc.scan;

public abstract class PatternNameMethodFilter extends DefaultMethodFilter {

	protected final String methodPattern;

	protected PatternNameMethodFilter(Class<?> clazz, String methodPattern) {
		super(clazz);
		this.methodPattern = methodPattern;
	}

}
