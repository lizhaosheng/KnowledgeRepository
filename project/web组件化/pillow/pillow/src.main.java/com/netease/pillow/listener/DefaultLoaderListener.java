package com.netease.pillow.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;

import com.netease.pillow.loader.PillowClassLoader;

/**
 * 默认监听器，空监听器不进行任何组件的初始化
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public class DefaultLoaderListener extends AbstractLoaderListener{

	@Override
	public List<PillowClassLoader> getClassLoaderList(ServletContextEvent event) {
		return null;
	}

}
