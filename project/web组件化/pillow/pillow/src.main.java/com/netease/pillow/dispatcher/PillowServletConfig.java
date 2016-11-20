package com.netease.pillow.dispatcher;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.netease.pillow.PillowDomain;

/**
 * servlet的配置
 * @author lizhaosheng
 * @version 2014-10-23 上午10:02:44
 */
public class PillowServletConfig implements ServletConfig{

	/**
	 * 组件上下文领域
	 */
	private PillowDomain domain;
	
	/**
	 * 通用组件配置信息
	 */
	private ServletConfig servletConfig;
	
	/**
	 * 所有初始化参数名
	 */
	private Enumeration<String> paramNames = null;
	
	public PillowServletConfig(PillowDomain domain, ServletConfig servletConfig){
		this.domain = domain;
		this.servletConfig = servletConfig;
	}
	
	@Override
	public String getInitParameter(String arg0) {
		return domain.getInitContextParameter(arg0);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Enumeration<String> getInitParameterNames() {
		if(paramNames == null){
			Iterator<String> it = domain.getInitServletParameterMap().keySet().iterator();
			Vector<String> all = new Vector<String>(); 
			while(it.hasNext()){
				String node = it.next();
				all.add(node);
			}
			paramNames = all.elements();  
		}
		return paramNames;
	}

	@Override
	public ServletContext getServletContext() {
		return servletConfig.getServletContext();
	}

	@Override
	public String getServletName() {
		return domain.getPillowName();
	}
	
}