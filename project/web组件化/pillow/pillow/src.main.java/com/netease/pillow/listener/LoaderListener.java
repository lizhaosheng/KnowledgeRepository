package com.netease.pillow.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.netease.pillow.loader.PillowClassLoader;

/**
 * 默认监听器，空监听器不进行任何组件的初始化
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public interface LoaderListener extends ServletContextListener{

	/**
	 * 获取组件的类加载器
	 * @param event
	 * @return
	 */
	List<PillowClassLoader> getClassLoaderList(ServletContextEvent event);
	/**
	 * 获取组件类加载器参数
	 * @param event
	 * @param loader
	 */
	void initLoaderParam(ServletContextEvent event,PillowClassLoader loader);
}
