package com.netease.pillow.context;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.netease.pillow.PillowDomain;
import com.netease.pillow.PillowManager;

/**
 * 组件化监听器
 * spring root context初始化完成后调用；<br>
 * 暂时只支持组件为jar形式，并且放在WEB-INF/lib/plugins下；<br>
 * 通过pluginService查询哪些plugin是启用的，获取启用plugin列表；<br>
 * 依次启动各个plugin（注意依赖关系）；<br>
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public class PillowContextLoader{
	
	private static final Logger log = Logger.getLogger(PillowContextLoader.class);

	/**
	 * 若组件没有配置文件，指定一个空配置文件
	 */
	private static final String DEFAULT_EMPTY_CONTEXT_LOCATION = "classpath:com/netease/pillow/xml/pillow-empty-context.xml";
	/**
	 * 组件spring配置文件变量名
	 */
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
	/**
	 * 默认的上下文环境类（目前必须是XmlWebApplicationContext）
	 */
	private static final String DEFAULT_WEBAPPLICATIONCONTEXT = "org.springframework.web.context.support.XmlWebApplicationContext";

	/**
	 * 默认的上下文环境类（目前必须是XmlWebApplicationContext）
	 */
	private static final String PILLOW_WEBAPPLICATIONCONTEXT = "com.netease.pillow.context.PillowApplicationContext";

	
	/**
	 * Initialize Spring's web application context for the given servlet context,
	 * according to the "{@link #CONTEXT_CLASS_PARAM contextClass}" and
	 * "{@link #CONFIG_LOCATION_PARAM contextConfigLocation}" context-params.
	 * @param father current servlet context
	 * @param domain 
	 * @return the new WebApplicationContext
	 * @see #CONTEXT_CLASS_PARAM
	 * @see #CONFIG_LOCATION_PARAM
	 */
	public WebApplicationContext initPillowContext(ApplicationContext father, PillowDomain domain, PillowManager pillowManager) {
		String pillowName = domain.getPillowName();
		if (log.isInfoEnabled()) {
			log.info("'" + pillowName + "' WebApplicationContext: initialization started");
		}
		long startTime = System.currentTimeMillis();
		
		WebApplicationContext context = null;
		try {
			// Store context in local instance variable, to guarantee that
			// it is available on ServletContext shutdown.
			context = createWebApplicationContext(pillowManager.getServletContext(), domain, pillowManager);

			if (log.isDebugEnabled()) {
				log.debug("Published '" + pillowName + "' WebApplicationContext.");
			}
			if (log.isInfoEnabled()) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				log.info("'" + pillowName + "' WebApplicationContext: initialization completed in " + elapsedTime + " ms");
			}

			return context;
		}
		catch (RuntimeException ex) {
			log.error("Context initialization failed", ex);
			throw ex;
		}
		catch (Error err) {
			log.error("Context initialization failed", err);
			throw err;
		}
	}

	/**
	 * Instantiate the root WebApplicationContext for this loader, either the
	 * default context class or a custom context class if specified.
	 * <p>This implementation expects custom contexts to implement the
	 * {@link ConfigurableWebApplicationContext} interface.
	 * Can be overridden in subclasses.
	 * <p>In addition, {@link #customizeContext} gets called prior to refreshing the
	 * context, allowing subclasses to perform custom modifications to the context.
	 * @param sc current servlet context
	 * @param parent the parent ApplicationContext to use, or <code>null</code> if none
	 * @return the root WebApplicationContext
	 * @see ConfigurableWebApplicationContext
	 */
	protected WebApplicationContext createWebApplicationContext(ServletContext sc, PillowDomain domain, PillowManager pillowManager) {
		Class<?> contextClass = determineContextClass(sc);
		if (!XmlWebApplicationContext.class.isAssignableFrom(contextClass)) {
			
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() +
					"] is not of type [" + XmlWebApplicationContext.class.getName() + "]");
		}
		XmlWebApplicationContext wac =
				(XmlWebApplicationContext) BeanUtils.instantiateClass(contextClass);

		//下面用到sc的可能存在冲突问题
		// Assign the best possible id value.
		setApplicationContextId(sc, wac, domain);

		PillowDomain parent = domain.getParent();
		if(parent != null){
			wac.setParent(parent.getContext());
		}
		else{
			wac.setParent(pillowManager.getRootContext());
		}
		wac.setServletContext(sc);
//		wac.setServletConfig(getServletConfig());
		//若为null会有什么影响？——无法加载成功
		String location = domain.getInitContextParameter(CONFIG_LOCATION_PARAM);
		if(location == null){
			location = DEFAULT_EMPTY_CONTEXT_LOCATION;
		}
		wac.setConfigLocation(location);
		wac.setNamespace(domain.getPillowName());
		customizeContext(sc, wac);
		//设置类加载器
		wac.setClassLoader(domain.getContextClassLoader());
		//refresh
		wac.refresh();
		
		return wac;
	}

//	private ServletConfig getServletConfig() {
//		
//		return null;
//	}

	/**
	 * 设置上下文环境的id，意义不大~
	 * @param sc
	 * @param wac
	 * @param domain
	 */
	private void setApplicationContextId(ServletContext sc, XmlWebApplicationContext wac, PillowDomain domain) {
		if (sc.getMajorVersion() == 2 && sc.getMinorVersion() < 5) {
			// Servlet <= 2.4: resort to name specified in web.xml, if any.
			String servletContextName = sc.getServletContextName();
			wac.setId(XmlWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
					ObjectUtils.getDisplayString(servletContextName) + ":" + domain.getPillowName());
		}
		else {
			// Servlet 2.5's getContextPath available!
			try {
				String contextPath = (String) ServletContext.class.getMethod("getContextPath").invoke(sc);
				wac.setId(XmlWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX +
						ObjectUtils.getDisplayString(contextPath) + ":" + domain.getPillowName());
			}
			catch (Exception ex) {
				throw new IllegalStateException("Failed to invoke Servlet 2.5 getContextPath method", ex);
			}
		}
	}
	/**
	 * Return the WebApplicationContext implementation class to use, either the
	 * default XmlWebApplicationContext or a custom context class if specified.
	 * @param servletContext current servlet context
	 * @return the WebApplicationContext implementation class to use
	 * @see #CONTEXT_CLASS_PARAM
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 */
	protected Class<?> determineContextClass(ServletContext servletContext) {
		try {
			return ClassUtils.forName(PILLOW_WEBAPPLICATIONCONTEXT,PillowContextLoader.class.getClassLoader());
		}
		catch (ClassNotFoundException ex) {
			throw new ApplicationContextException(
					"Failed to load default context class [" + DEFAULT_WEBAPPLICATIONCONTEXT + "]", ex);
		}
	}

	/**
	 * Customize the {@link ConfigurableWebApplicationContext} created by this
	 * ContextLoader after config locations have been supplied to the context
	 * but before the context is <em>refreshed</em>.
	 * <p>The default implementation is empty but can be overridden in subclasses
	 * to customize the application context.
	 * @param servletContext the current servlet context
	 * @param applicationContext the newly created application context
	 * @see #createWebApplicationContext(ServletContext, ApplicationContext)
	 */
	protected void customizeContext(
			ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
	}
//
//	/**
//	 * Template method with default implementation (which may be overridden by a
//	 * subclass), to load or obtain an ApplicationContext instance which will be
//	 * used as the parent context of the root WebApplicationContext. If the
//	 * return value from the method is null, no parent context is set.
//	 * <p>The main reason to load a parent context here is to allow multiple root
//	 * web application contexts to all be children of a shared EAR context, or
//	 * alternately to also share the same parent context that is visible to
//	 * EJBs. For pure web applications, there is usually no need to worry about
//	 * having a parent context to the root web application context.
//	 * <p>The default implementation uses
//	 * {@link org.springframework.context.access.ContextSingletonBeanFactoryLocator},
//	 * configured via {@link #LOCATOR_FACTORY_SELECTOR_PARAM} and
//	 * {@link #LOCATOR_FACTORY_KEY_PARAM}, to load a parent context
//	 * which will be shared by all other users of ContextsingletonBeanFactoryLocator
//	 * which also use the same configuration parameters.
//	 * @param servletContext current servlet context
//	 * @return the parent application context, or <code>null</code> if none
//	 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
//	 */
//	protected ApplicationContext loadParentContext(ServletContext servletContext) {
//		ApplicationContext parentContext = null;
//		String locatorFactorySelector = servletContext.getInitParameter(LOCATOR_FACTORY_SELECTOR_PARAM);
//		String parentContextKey = servletContext.getInitParameter(LOCATOR_FACTORY_KEY_PARAM);
//
//		if (parentContextKey != null) {
//			// locatorFactorySelector may be null, indicating the default "classpath*:beanRefContext.xml"
//			BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator.getInstance(locatorFactorySelector);
//			Log log = LogFactory.getLog(ContextLoader.class);
//			if (log.isDebugEnabled()) {
//				log.debug("Getting parent context definition: using parent context key of '" +
//						parentContextKey + "' with BeanFactoryLocator");
//			}
//			this.parentContextRef = locator.useBeanFactory(parentContextKey);
//			parentContext = (ApplicationContext) this.parentContextRef.getFactory();
//		}
//
//		return parentContext;
//	}
//
//	/**
//	 * Close Spring's web application context for the given servlet context. If
//	 * the default {@link #loadParentContext(ServletContext)} implementation,
//	 * which uses ContextSingletonBeanFactoryLocator, has loaded any shared
//	 * parent context, release one reference to that shared parent context.
//	 * <p>If overriding {@link #loadParentContext(ServletContext)}, you may have
//	 * to override this method as well.
//	 * @param servletContext the ServletContext that the WebApplicationContext runs in
//	 */
//	public void closeWebApplicationContext(ServletContext servletContext) {
//		servletContext.log("Closing Spring root WebApplicationContext");
//		try {
//			if (this.context instanceof ConfigurableWebApplicationContext) {
//				((ConfigurableWebApplicationContext) this.context).close();
//			}
//		}
//		finally {
//			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
//			if (ccl == ContextLoader.class.getClassLoader()) {
//				currentContext = null;
//			}
//			else if (ccl != null) {
//				currentContextPerThread.remove(ccl);
//			}
//			servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
//			if (this.parentContextRef != null) {
//				this.parentContextRef.release();
//			}
//		}
//	}
//
//
//	/**
//	 * Obtain the Spring root web application context for the current thread
//	 * (i.e. for the current thread's context ClassLoader, which needs to be
//	 * the web application's ClassLoader).
//	 * @return the current root web application context, or <code>null</code>
//	 * if none found
//	 * @see org.springframework.web.context.support.SpringBeanAutowiringSupport
//	 */
//	public static WebApplicationContext getCurrentWebApplicationContext() {
//		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
//		if (ccl != null) {
//			WebApplicationContext ccpt = currentContextPerThread.get(ccl);
//			if (ccpt != null) {
//				return ccpt;
//			}
//		}
//		return currentContext;
//	}
	
}
