package com.netease.pillow.listener;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.netease.pillow.loader.JarClassLoader;
import com.netease.pillow.loader.PillowClassLoader;
import com.netease.pillow.util.FileUtils;

/**
 * jar包类型组件监听器<br>
 * 查找当前项目下的组件
 * @author lizhaosheng
 * @version 2014-11-02 下午07:41:22
 */
public class JarLoaderListener extends AbstractLoaderListener{
	private static final Logger log = Logger.getLogger(JarLoaderListener.class);
	private static final String DEFAULT_JAR_LOCATION = "WEB-INF/lib/pillow";
	/**
	 * 配置文件路径变量
	 */
	private static String JAR_PILLOW_LOCATION_ATTRIBUE = "jarPillowBasePath";

	public List<PillowClassLoader> getClassLoaderList(ServletContextEvent event) {
		String basePath = event.getServletContext().getInitParameter(JAR_PILLOW_LOCATION_ATTRIBUE);

		if(basePath == null){
			basePath = DEFAULT_JAR_LOCATION;
		}
		List<File> jarList = findJars(basePath,event.getServletContext());
		if(jarList == null || jarList.isEmpty()){
			return null;
		}
		List<PillowClassLoader> list = new ArrayList<PillowClassLoader>();
		for(File file:jarList){
			try {
				String name = JarClassLoader.JAR_PROTOCOL + file.toURI() + JarClassLoader.JAR_SEP;
				PillowClassLoader cl = new JarClassLoader(new URL(name),this.getClass().getClassLoader());
				//set init param
				initLoaderParam(event,cl);
				
				list.add(cl);
			} catch (MalformedURLException e) {
				log.error("URL exception", e);
				continue;
			} catch (IOException e) {
				log.error("Error load jar with name: " + file.getAbsolutePath(), e);
				continue;
			}
		}
		return list;
	}
	
	/**
	 * 查找当前项目中，basePath目录及其子目录下的所有jar
	 * @param basePath
	 * @param servletContext
	 * @return
	 */
	public static List<File> findJars(String basePath, ServletContext servletContext) {
		if(basePath == null || servletContext == null){
			return null;
		}
		//project root path
		String rootPath=servletContext.getRealPath(File.separator);
		if(!rootPath.endsWith("/") && !rootPath.endsWith("\\")){
			rootPath = rootPath + File.separator;
		}
		if(basePath.startsWith("/") || basePath.startsWith("\\")){
			basePath = basePath.substring(1);
		}
		//jar所在目录
		String fullPath = rootPath + basePath;
		
		List<File> jarList = FileUtils.findFiles(fullPath,".*\\.jar",1,true);
		return jarList;
	}
}
