package com.netease.pillow.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;

import com.netease.pillow.loader.PillowClassLoader;

/**
 * 类路径类型监听器
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public class ClasspathLoaderListener extends AbstractLoaderListener{

	@Override
	public List<PillowClassLoader> getClassLoaderList(ServletContextEvent event) {
		PillowClassLoader cl = new PillowClassLoader(null,this.getClass().getClassLoader());
		List<PillowClassLoader> list = new ArrayList<PillowClassLoader>();
		list.add(cl);
		return list;
	}

}
