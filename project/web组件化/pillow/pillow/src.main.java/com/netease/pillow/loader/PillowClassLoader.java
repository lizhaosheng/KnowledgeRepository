package com.netease.pillow.loader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netease.pillow.PillowDomain;

/**
 * 
 * @author lizhaosheng
 * @version 2014-11-3 下午5:05:13
 */
public class PillowClassLoader extends URLClassLoader{
	/**
	 * 父级组件的加载器（非父加载器）
	 */
	protected PillowClassLoader parentPillowClassLoader;
	/**
	 * 加载路径
	 */
	private URL basePath = null;
	/**
	 * 参数映射
	 */
	private Map paramMap = null;
	/**
	 * 参数映射
	 */
	private List<PillowDomain> domainList = null;
	

	public PillowClassLoader(URL url, ClassLoader parentClassLoader) {
        super(new URL[0], parentClassLoader);
        this.basePath = url;
    }
	
	/**************************************子类需要定制化方法**********************************/
    public void addToClassPath(){
    	super.addURL(basePath);
    }
    
    public void removeFromClassPath(){
    	
    }
    public PillowClassLoader getContextClassLoader(String pillowName){
    	return this;
    }
    public void setLoadDomainList(List<PillowDomain> temp) {
    	domainList = temp;
	}
    public void addLoadDomain(PillowDomain domain) {
    	if(domainList == null){
    		domainList = new ArrayList<PillowDomain>();
    	}
    	domainList.add(domain);
	}
    public Object getParam(Object key) {
		if(paramMap == null){
			return null;
		}
		return paramMap.get(key);
	}
	
	public void putParam(Object key, Object value) {
		if(paramMap == null){
			paramMap = new HashMap();
		}
		paramMap.put(key, value);
	}
    
    /**************************************getter and setter**********************************/
    /**
	 * @return the basePath
	 */
	public URL getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(URL basePath) {
		this.basePath = basePath;
	}

	/**
	 * @return the parentPillowClassLoader
	 */
	public PillowClassLoader getParentPillowClassLoader() {
		return parentPillowClassLoader;
	}

	/**
	 * @param parentPillowClassLoader the parentPillowClassLoader to set
	 */
	public void setParentPillowClassLoader(PillowClassLoader parentPillowClassLoader) {
		this.parentPillowClassLoader = parentPillowClassLoader;
	}

	public Map getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map paramMap) {
		this.paramMap = paramMap;
	}

}
