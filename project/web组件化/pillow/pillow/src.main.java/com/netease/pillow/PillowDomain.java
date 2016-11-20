package com.netease.pillow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.netease.pillow.dispatcher.PillowDispatcherServlet;
import com.netease.pillow.loader.PillowClassLoader;
import com.netease.pillow.xml.PillowConfig;

/**
 * 即组件的上下文，因为有一个PillowContextLoader表示加载组件spring上下文，为了避免理解混乱，所以叫domain
 * @author lizhaosheng
 * @version 2014-10-30 下午6:40:19
 */
public class PillowDomain {
	
	/**
	 * 组件配置信息对象
	 */
	private PillowConfig config = null;
	/**
	 * 组件spring上下文
	 */
	private ApplicationContext context = null;
	/**
	 * 组件spring前端控制器
	 */
	private PillowDispatcherServlet servlet = null;
	/**
	 * 组件的配置文件加载器，配置文件仅指pillow-*.xml
	 */
	private PillowClassLoader configClassLoader = null;
	/**
	 * 组件的上下文类加载器，包括spring context和dispatcher
	 */
	private PillowClassLoader contextClassLoader = null;
	/**
	 * 父级组件
	 */
	private PillowDomain parent = null;
	/**
	 * 子级组件
	 */
	private List<PillowDomain> children = null;
	
	/**
	 * 获取组件名
	 * @return
	 */
	public String getPillowName() {
		return config.getPillowName();
	}

	/**
	 * 获取父级组件名
	 * @return
	 */
	public String getParentName() {
		return config.getParentName();
	}

	/**
	 * 添加一个子节点
	 * @param domain
	 */
	public void addChild(PillowDomain domain) {
		if(children == null){
			children = new ArrayList<PillowDomain>();
		}
		children.add(domain);
	}
	
	/**
	 * 获取组件spring上下文初始化参数
	 * @param key
	 * @return
	 */
	public String getInitContextParameter(String key) {
		return config.getInitContextParameter(key);
	}
	
	/**
	 * 获取组件dispatcher的初始化参数
	 * @param key
	 * @return
	 */
	public String getInitServletParameter(String key) {
		return config.getInitServletParameter(key);
	}
	
	/**
	 * 获取dispatcher的初始化参数映射
	 * @return
	 */
	public  Map<String,String> getInitServletParameterMap() {
		// TODO Auto-generated method stub
		return config.getInitServletParameterMap();
	}
	
	/**
	 * 获取url映射
	 * @return
	 */
	public String getUrlMapping() {
		return config.getUrlMapping();
	}
	
	/**
	 * 是否能被子级继承
	 * @return
	 */
	public boolean isCanExtends(){
		return config.isCanExtends();
	}
	
	/**
	 * 是否使用通用策略
	 * @return
	 */
	public boolean isUseCommon() {
		return config.isUseCommon();
	}
	
	/*************************************setter and getter************************************************/
	public void setParent(PillowDomain parent) {
		this.parent = parent;
	}

	public PillowConfig getPillowConfig() {
		return config;
	}
	
	public void setPillowConfig(PillowConfig config) {
		this.config = config;
	}

	public void setChildren(List<PillowDomain> list) {
		this.children = list;
	}

	public List<PillowDomain> getChildren() {
		return children;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public PillowDomain getParent() {
		return parent;
	}

	public PillowDispatcherServlet getDispatcherServlet() {
		return servlet;
	}

	public void setDispatcherServlet(PillowDispatcherServlet servlet) {
		this.servlet = servlet;
	}

	/**
	 * @return the configClassLoader
	 */
	public PillowClassLoader getConfigClassLoader() {
		return configClassLoader;
	}

	/**
	 * @param configClassLoader the configClassLoader to set
	 */
	public void setConfigClassLoader(PillowClassLoader configClassLoader) {
		this.configClassLoader = configClassLoader;
	}

	/**
	 * @return the contextClassLoader
	 */
	public PillowClassLoader getContextClassLoader() {
		return contextClassLoader;
	}

	/**
	 * @param contextClassLoader the contextClassLoader to set
	 */
	public void setContextClassLoader(PillowClassLoader contextClassLoader) {
		this.contextClassLoader = contextClassLoader;
	}

	public void clear() {
		config = null;
		context = null;
		servlet = null;
		configClassLoader = null;
		contextClassLoader = null;
		parent = null;
		children = null;
	}

}
