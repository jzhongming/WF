package com.github.jzhongming.wf.mvc;

import java.util.Map;

import com.google.common.collect.Maps;

public class Model {

	private final Map<String, Object> modelMap = Maps.newHashMap();

	protected Model() {

	}

	/**
	 * 增加一个属性
	 * 
	 * @param attributeName
	 *            属性名称
	 * @param attributeValue
	 *            属性值
	 */
	public Model add(String attributeName, Object attributeValue) {
		modelMap.put(attributeName, attributeValue);
		return this;
	}

	/**
	 * 根据属性名得到属性值
	 * 
	 * @param attributeName
	 *            属性名称
	 * @return 对应的属性值
	 */
	public Object get(String attributeName) {
		return modelMap.get(attributeName);
	}

	/**
	 * Return the model map. Never returns <code>null</code>. To be called by
	 * application code for modifying the model.
	 */
	public Map<String, Object> getModel() {
		return modelMap;
	}

	/**
	 * 批量增加属性
	 * 
	 * @param attributes
	 */
	public Model addAll(Map<String, Object> attributes) {
		modelMap.putAll(attributes);
		return this;
	}

	/**
	 * 判断是否包含属性名
	 * 
	 * @param attributeName
	 *            需要查找的属性
	 * @return
	 */
	public boolean contains(String attributeName) {
		return modelMap.containsKey(attributeName);
	}

	/**
	 * 合并属性
	 * 
	 * @param attributes
	 */
	public Model merge(Map<String, Object> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				if (!modelMap.containsKey(key)) {
					modelMap.put(key, attributes.get(key));
				}
			}
		}
		return this;
	}

}