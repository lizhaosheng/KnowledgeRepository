package com.netease.pillow.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组件配置信息对象
 * @author lizhaosheng
 * @version 2014-10-13 下午3:58:36
 */
public class PillowConfig {
	/**
	 * 组件名
	 */
	private String pillowName;

	/**
	 * 是否可以被子节点继承
	 */
	private boolean canExtends = false;
	
	/**
	 * 是否使用通用策略
	 */
	private boolean useCommon = true;
	
	/**
	 * 父节点名称
	 */
	private String parentName;
	
	/**
	 * 父节点
	 */
	private PillowConfig parent;
	
	/**
	 * 上下文环境的初始化参数
	 */
	private Map<String,String> initContextParameterMap = new HashMap<String,String>();

	/**
	 * 组件dispatcher的初始化参数
	 */
	private Map<String,String> initServletParameterMap = new HashMap<String,String>();
	
	/**
	 * url映射
	 */
	private String urlMapping;
	
	/**
	 * 子组件，即被依赖组件
	 */
	private List<PillowConfig> childList;

	public String getPillowName() {
		return pillowName;
	}

	public void setPillowName(String pillowName) {
		this.pillowName = pillowName;
	}

	/**
	 * @return the canExtends
	 */
	public boolean isCanExtends() {
		return canExtends;
	}

	/**
	 * @param canExtends the canExtends to set
	 */
	public void setCanExtends(boolean canExtends) {
		this.canExtends = canExtends;
	}

	/**
	 * @return the useCommon
	 */
	public boolean isUseCommon() {
		return useCommon;
	}

	/**
	 * @param useCommon the useCommon to set
	 */
	public void setUseCommon(boolean useCommon) {
		this.useCommon = useCommon;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public PillowConfig getParent() {
		return parent;
	}

	public void setParent(PillowConfig parent) {
		this.parent = parent;
	}

	/**
	 * @return the childList
	 */
	public List<PillowConfig> getChildList() {
		return childList;
	}

	/**
	 * @param childList the childList to set
	 */
	public void setChildList(List<PillowConfig> childList) {
		this.childList = childList;
	}

	/**
	 * @return the initContextParameterMap
	 */
	public Map<String,String> getInitContextParameterMap() {
		return initContextParameterMap;
	}

	/**
	 * @param initContextParameterMap the initContextParameterMap to set
	 */
	public void setInitContextParameterMap(Map<String,String> initContextParameterMap) {
		this.initContextParameterMap = initContextParameterMap;
	}

	/**
	 * @return the initServletParameterMap
	 */
	public Map<String,String> getInitServletParameterMap() {
		return initServletParameterMap;
	}

	/**
	 * @param initServletParameterMap the initServletParameterMap to set
	 */
	public void setInitServletParameterMap(Map<String,String> initServletParameterMap) {
		this.initServletParameterMap = initServletParameterMap;
	}

	/**
	 * @return the urlMapping
	 */
	public String getUrlMapping() {
		return urlMapping;
	}

	/**
	 * @param urlMapping the urlMapping to set
	 */
	public void setUrlMapping(String urlMapping) {
		this.urlMapping = urlMapping;
	}

	public String getInitContextParameter(String key) {
		return getInitContextParameterMap().get(key);
	}

	public String getInitServletParameter(String key) {
		return getInitServletParameterMap().get(key);
	}
	
}
