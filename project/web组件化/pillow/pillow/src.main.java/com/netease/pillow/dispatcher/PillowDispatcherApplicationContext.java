package com.netease.pillow.dispatcher;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.support.XmlWebApplicationContext;


/**
 * 组件分发器的上下文
 * @author lizhaosheng
 * @version 2014-11-6 下午5:32:09
 */
public class PillowDispatcherApplicationContext extends XmlWebApplicationContext {
	
	private AbstractApplicationContext commonApplicationContext;
	/**
	 * 若不指定策略，是否使用通用策略
	 */
	private boolean isCommonEnable = true;
	
	/**
	 * 因为若组件未配置handlerMapping实例，组件的上下文使用getBean无法获取到通用处理器中提供的通用处理bean，因此在组件
	 * beanFactory初始化完成后，将通用处理器beanFactory中的所有bean都加入到组件的beanFactory中,同名排除；
	 * 
	 */
	
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory){
		super.postProcessBeanFactory(beanFactory);
		if(isCommonEnable){
			DefaultListableBeanFactory bf = (DefaultListableBeanFactory)beanFactory;
			String[] beanNames = commonApplicationContext.getBeanFactory().getBeanDefinitionNames();
			for(String name:beanNames){
				if(bf.containsBean(name)){
					continue;
				}
				BeanDefinition definition = commonApplicationContext.getBeanFactory().getBeanDefinition(name);
				bf.registerBeanDefinition(name, definition);
			}
		}
	}

	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		// TODO Auto-generated method stub
		return super.getResourcePatternResolver();
	}

	/**
	 * @param commonApplicationContext the commonApplicationContext to set
	 */
	public void setCommonApplicationContext(AbstractApplicationContext commonApplicationContext) {
		this.commonApplicationContext = commonApplicationContext;
	}

	/**
	 * @return the isCommonEnable
	 */
	public boolean isCommonEnable() {
		return isCommonEnable;
	}

	/**
	 * @param isCommonEnable the isCommonEnable to set
	 */
	public void setCommonEnable(boolean isCommonEnable) {
		this.isCommonEnable = isCommonEnable;
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

