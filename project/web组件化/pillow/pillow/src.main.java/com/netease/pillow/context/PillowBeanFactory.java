package com.netease.pillow.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * 组件工厂，之所以要重写是因为默认的DefaultListableBeanFactory的资源加载器是不会从jar中加载文件的，需要替换为我们自己的加载器
 * @author lizhaosheng
 * @version 2014-11-6 下午7:07:00
 */
public class PillowBeanFactory extends DefaultListableBeanFactory implements ResourceLoader{

	private PathMatchingResourcePatternResolver resolver = null;
	
	public PillowBeanFactory(BeanFactory internalParentBeanFactory, ClassLoader loader) {
		super(internalParentBeanFactory);
		this.resolver = new PathMatchingResourcePatternResolver(loader);
	}

	@Override
	public Resource getResource(String location) {
		return resolver.getResource(location);
	}

	@Override
	public ClassLoader getClassLoader() {
		return resolver.getClassLoader();
	}

}
