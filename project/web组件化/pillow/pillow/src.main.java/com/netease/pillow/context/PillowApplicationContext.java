package com.netease.pillow.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;


/**
 * 相关服务
 * @author lizhaosheng
 * @version 2014-11-6 下午5:32:09
 */
public class PillowApplicationContext extends XmlWebApplicationContext {
	
	/**
	 * 返回的factory是一个继承ResourceLoader的factory，
	 * 主要是为了将getClassLoader()传递给mybatis的mapperScannerConfigurer（默认mapperScannerConfigurer使用的是WebAppClassLoader）;
	 * WebAppClassLoader无法加载jar包中的mapping文件
	 */
	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new PillowBeanFactory(getInternalParentBeanFactory(),getClassLoader());
	}
	
	/**************************************
	 * 以后可能用的上
	 **************************************/
//	private PathMatchingResourcePatternResolver resolver = null;
//
//	@Override
//	public Resource getResource(String location) {
//		if(resolver == null){
//			this.resolver = new PathMatchingResourcePatternResolver(getClassLoader());
//		}
//		return resolver.getResource(location);
//	}
//
//	@Override
//	public ClassLoader getClassLoader() {
//		return resolver.getClassLoader();
//	}
}

