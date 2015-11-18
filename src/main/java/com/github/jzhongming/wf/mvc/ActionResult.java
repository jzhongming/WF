package com.github.jzhongming.wf.mvc;

/**
 * 所有Action的返回结果
 */
public interface ActionResult {

	/**
	 * 用于生成显示页面
	 *
	 * @param beatContext
	 */
	public void render();

}
