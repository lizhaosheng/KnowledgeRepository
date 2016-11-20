package com.netease.pillow.listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.netease.pillow.PillowManager;
import com.netease.pillow.context.PillowLoader;
import com.netease.pillow.loader.PillowClassLoader;
import com.netease.pillow.xml.PillowConfigtLoader;


/**
 * 组件化监听器,用于启动平台组件化特性。
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public abstract class AbstractLoaderListener implements LoaderListener {

	private static final Logger log = Logger.getLogger(AbstractLoaderListener.class);
	/**
	 * 组件加载器
	 */
	protected PillowLoader pillowLoader = new PillowLoader();
	/**
	 * 组件管理器
	 */
	protected final PillowManager manager = PillowManager.getPillowManager();
	
	/**
	 * 初始化并加载所有组件上下文
	 */
	@Override
	public final void contextInitialized(ServletContextEvent event) {
		try {
			//初始化manager
			initPillowEnv(event);
			manager.setPillowEnvInitState(PillowManager.CONTEXT_ENV_INITED_STATE);
			
			//list
			List<PillowClassLoader> list = getClassLoaderList(event);
			this.pillowLoader.loadPillows(list,manager);
			manager.setPillowEnvInitState(PillowManager.CONTEXT_INITED_STATE);
			
		} catch (IllegalAccessException e) {
			log.error("Init pillow context failed!",e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Init pillow context failed!",e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("Init pillow context failed!",e);
			e.printStackTrace();
		} catch (Throwable e) {
			log.error("Init pillow context failed!",e);
			e.printStackTrace();
		}
	}

	public void initLoaderParam(ServletContextEvent event,PillowClassLoader loader) {
		String location = event.getServletContext().getInitParameter(PillowConfigtLoader.PILLOW_LOCATION_ATTRIBUE);
	
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put(PillowConfigtLoader.PILLOW_LOCATION_ATTRIBUE, location);
		loader.setParamMap(paramMap);
	}

	public abstract List<PillowClassLoader> getClassLoaderList(ServletContextEvent event);

	/**
	 * 初始化组件环境，主要包括初始化根上下文对象、加载器、搜索路径
	 * @param event
	 * @throws ClassNotFoundException 
	 */
	private void initPillowEnv(ServletContextEvent event) throws ClassNotFoundException {
		ServletContext servletContext = event.getServletContext();
		//根context，确保在pillowcontext之前被加载
		WebApplicationContext rootContext = (WebApplicationContext) servletContext.getAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (rootContext == null) {
			throw new IllegalStateException(
					"Cannot initialize pillow context because can't find a root application context present - " +
					"check whether you have configured ContextLoader* definitions in your web.xml " +
					"and make sure it is defined before PillowLoader*!");
		}
		manager.setServletContext(servletContext);
		manager.setRootContext(rootContext);
		
		//pillow conf file path
		String location = event.getServletContext().getInitParameter(PillowConfigtLoader.PILLOW_LOCATION_ATTRIBUE);
		if(location == null){
			log.warn("No pillow configfile path attribute was defined in web.xml!");
		}
		manager.setPillowLocation(location);
	}

	/**
	 * 暂时不管，未来需要重写
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//do nothing
	}

//	/**
//	 * Close the root web application context.
//	 */
//	public void contextDestroyed(ServletContextEvent event) {
//		if (this.pillowLoader != null) {
//			this.pillowLoader.closeWebApplicationContext(event.getServletContext());
//		}
//		//清理spring在servlet中的一些变量，以org.springframework.开头
////		ContextCleanupListener.cleanupAttributes(event.getServletContext());
//	}
//	
}
