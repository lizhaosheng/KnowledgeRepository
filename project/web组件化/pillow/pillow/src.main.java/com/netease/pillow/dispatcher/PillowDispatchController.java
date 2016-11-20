package com.netease.pillow.dispatcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.netease.pillow.PillowDomain;
import com.netease.pillow.PillowManager;
import com.netease.pillow.exception.PillowLoadException;

/**
 * 相关服务
 * @author lizhaosheng
 * @version 2014-10-15 上午10:16:54
 */
public class PillowDispatchController extends DispatcherServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6373493709818962141L;

	private static final Logger log = Logger.getLogger(PillowDispatchController.class);

	/**
	 * 
	 */
	private final PillowManager manager = PillowManager.getPillowManager();
	
	@Deprecated
	private PillowCommonDispatcherServlet common = null;
	@Deprecated
	public void init(PillowCommonDispatcherServlet pillowCommonDispatcherServlet) {
		common = pillowCommonDispatcherServlet;
		createInitPillowDispatchers();
		manager.postPillowDispatcherInit();
	}
	
	/**
	 * 系统启时需要创建并初始化组件的dispatcher
	 */
	private void createInitPillowDispatchers() {
		Map<String, PillowDomain> configMap = manager.getPillowDomainMap();
		Iterator<String> it = configMap.keySet().iterator();
		while(it.hasNext()){
			String name = it.next();
			PillowDomain domain = configMap.get(name);
			try {
				log.info("create dispatcher of '" + domain.getPillowName() + "'");
				createPillowDispatcher(domain);
				log.info("create dispatcher of '" + domain.getPillowName() + "' complete!");
			} catch (ServletException e) {
				log.error("failed to create dispatcher of '" + domain.getPillowName() + "'", e);
				continue;
			}
			
		}
	}

	/**
	 * 分析：getServletContext和getServletConfig在这个类HelloServlet 中可以直接引用，
	 * 是因为其父类HttpServlet的父类GenericServlet已经实现了该方法。
	 * 所以这里并不是通过request得到这两个方法，而是直接调用。
	 * GenericServlet方法中实现了一些基本的方法，比如getServletInfo,getSerletName,getInitParameter方法等，都可以直接引用吗？还是。

          1：getServletContext()取得的是 <context-param>配置的参数 
               getServletConfig()取得的是 <servlet> <init-param>配置的参数

           2： getServletContext()应用于整个web App,而getServletConfig()仅应用于当前Servlet。
            但是ServletConfig对象拥有ServletContext的引用。
            所以可以通过getServletConfig()来获得ServletContext，从而得到web App的初始值。
	 * @param config
	 * @throws ServletException 
	 */
	public void createPillowDispatcher(PillowDomain domain) throws ServletException {
		if(manager.getPillowEnvInitState() < PillowManager.CONTROLLER_INITED_STATE){
			throw new ServletException("The pillow controller does not finish yet!");
		}
		PillowDispatcherServlet dispatcher = new PillowDispatcherServlet(this,domain);
		dispatcher.setNamespace(domain.getPillowName());
//		dispatcher.setContextConfigLocation(config.getDispatcherConfigLocation());
		//ServletConfig作用参考上面注释，
		dispatcher.init(new PillowServletConfig(domain,this));
		domain.setDispatcherServlet(dispatcher);
		
	}
	
	/**
	 * 在通用dispatcher的上下文刷新（onRefresh()之后进行对组件dispatcher进行初始化处理）
	 */
	protected WebApplicationContext initWebApplicationContext() {
		if(manager.getPillowEnvInitState() < PillowManager.CONTEXT_INITED_STATE){
			throw new PillowLoadException("The loader listener doesn't finish yet! Init of PillowDispatchController must start after LoaderListener.");
		}
		WebApplicationContext wac = super.initWebApplicationContext();
		return wac;
	}
	
	/**
	 * 通用处理器已经初始化完毕，
	 * 开始对组件进行初始化
	 */
	@Override
	protected void initFrameworkServlet() throws ServletException {
		//获取并保存通用处理策略，以便给组件dispatcher使用
//		getStrategies(this.getWebApplicationContext());
		manager.setController(this);
		manager.setPillowEnvInitState(PillowManager.CONTROLLER_INITED_STATE);
		initPillow();
	}
	public void initPillow() {
		createInitPillowDispatchers();
		manager.postPillowDispatcherInit();
	}
	
	/**
	 * 确定请求响应组件，并处理请求
	 * @param request
	 * @param response
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			String[] parts = checkUrl(request);
			if(parts == null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			PillowDomain domain = manager.getPillowDomain(parts[1]);
			if(domain == null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			PillowDispatcherServlet dispatcher = domain.getDispatcherServlet();
			if(dispatcher == null){
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
			String urlMapping = domain.getUrlMapping();
			if(urlMapping != null){
				String path = parts[2];
				if(!path.matches(urlMapping)){
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}
			//因为mybatis加载mapper的ClassLoaderWrapper是一个静态变量，类加载器获取方式固定
			//(org.apache.ibatis.io.ClassLoaderWrapper.getClassLoaders)所以通过设置线程加载器来将
			//让ClassLoaderWrapper获取正确的加载器
			ClassLoader old = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(domain.getContextClassLoader());
			dispatcher.service(request, response);
			Thread.currentThread().setContextClassLoader(old);
			
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 根据url确定组件名
	 * @param request
	 * @return
	 */
	private String[] checkUrl(HttpServletRequest request) {
		String path = request.getPathInfo();
//		
//		System.out.println("uri:"+request.getRequestURI());
//		System.out.println("context:"+request.getContextPath());
//		System.out.println("servlet:"+request.getServletPath());
//		System.out.println("pathinfo:"+request.getPathInfo());
		
		if(path == null){
			path = request.getServletPath();
		}
		String[] temp = path.split("/|\\?",3);
		if(temp.length < 3){
			throw new RuntimeException("error parse url!" + path);
		}
		return temp;
	}
}
