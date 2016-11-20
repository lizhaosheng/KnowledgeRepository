package com.netease.pillow.dispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * 组件url请求通用处理器，其作用有二：<br>
 * 1、作为初始化入口，创建并初始化各个组件的dispatcher；
 * 2、为组件提供通用的请求处理方案；
 * @author lizhaosheng
 * @version 2014-10-14 下午2:16:02<br>
 * 
 * merged to PillowDispatchController
 */
@Deprecated
public class PillowCommonDispatcherServlet extends DispatcherServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2410202324155169683L;
	/**
	 * 组件请求分发控制器
	 */
	private PillowDispatchController controller = new PillowDispatchController();
	
	/********************目前不用了，原因参见PillowDispatcherServlet*******************/
//
//	/** Detect all HandlerMappings or just expect "handlerMapping" bean? */
//	private boolean detectAllHandlerMappings = true;
//
//	/** Detect all HandlerAdapters or just expect "handlerAdapter" bean? */
//	private boolean detectAllHandlerAdapters = true;
//
//	/** Detect all HandlerExceptionResolvers or just expect "handlerExceptionResolver" bean? */
//	private boolean detectAllHandlerExceptionResolvers = true;
//
//	/** Detect all ViewResolvers or just expect "viewResolver" bean? */
//	private boolean detectAllViewResolvers = true;
//
//	/** MultipartResolver used by this servlet */
//	private MultipartResolver multipartResolver;
//
//	/** LocaleResolver used by this servlet */
//	private LocaleResolver localeResolver;
//
//	/** ThemeResolver used by this servlet */
//	private ThemeResolver themeResolver;
//
//	/** List of HandlerMappings used by this servlet */
//	private List<HandlerMapping> handlerMappings;
//
//	/** List of HandlerAdapters used by this servlet */
//	private List<HandlerAdapter> handlerAdapters;
//
//	/** List of HandlerExceptionResolvers used by this servlet */
//	private List<HandlerExceptionResolver> handlerExceptionResolvers;
//
//	/** RequestToViewNameTranslator used by this servlet */
//	private RequestToViewNameTranslator viewNameTranslator;
//
//	/** List of ViewResolvers used by this servlet */
//	private List<ViewResolver> viewResolvers;

	/**
	 * 在通用dispatcher的上下文刷新（onRefresh()之后进行对组件dispatcher进行初始化处理）
	 */
	protected WebApplicationContext initWebApplicationContext() {
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
		controller.init(this);
	}

	/**
	 * Exposes the DispatcherServlet-specific request attributes and delegates to {@link #doDispatch}
	 * for the actual dispatching.
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		controller.doService(request, response);
	}

	/********************目前不用了，原因参见PillowDispatcherServlet*******************/
//	/**
//	 * Initialize the strategy objects that this servlet uses.
//	 * <p>May be overridden in subclasses in order to initialize further strategy objects.
//	 */
//	protected void getStrategies(ApplicationContext context) {
//		initMultipartResolver(context);
//		initLocaleResolver(context);
//		initThemeResolver(context);
//		initHandlerMappings(context);
//		initHandlerAdapters(context);
//		initHandlerExceptionResolvers(context);
//		initRequestToViewNameTranslator(context);
//		initViewResolvers(context);
//	}
//
//	/**
//	 * Initialize the MultipartResolver used by this class.
//	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
//	 * no multipart handling is provided.
//	 */
//	protected void initMultipartResolver(ApplicationContext context) {
//		try {
//			this.multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Using MultipartResolver [" + this.multipartResolver + "]");
//			}
//		}
//		catch (NoSuchBeanDefinitionException ex) {
//			// Default is no multipart resolver.
//			this.multipartResolver = null;
//			if (logger.isDebugEnabled()) {
//				logger.debug("Unable to locate MultipartResolver with name '" + MULTIPART_RESOLVER_BEAN_NAME +
//						"': no multipart request handling provided");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the LocaleResolver used by this class.
//	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
//	 * we default to AcceptHeaderLocaleResolver.
//	 */
//	protected void initLocaleResolver(ApplicationContext context) {
//		try {
//			this.localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Using LocaleResolver [" + this.localeResolver + "]");
//			}
//		}
//		catch (NoSuchBeanDefinitionException ex) {
//			// We need to use the default.
//			this.localeResolver = getDefaultStrategy(context, LocaleResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Unable to locate LocaleResolver with name '" + LOCALE_RESOLVER_BEAN_NAME +
//						"': using default [" + this.localeResolver + "]");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the ThemeResolver used by this class.
//	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
//	 * we default to a FixedThemeResolver.
//	 */
//	protected void initThemeResolver(ApplicationContext context) {
//		try {
//			this.themeResolver = context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Using ThemeResolver [" + this.themeResolver + "]");
//			}
//		}
//		catch (NoSuchBeanDefinitionException ex) {
//			// We need to use the default.
//			this.themeResolver = getDefaultStrategy(context, ThemeResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug(
//						"Unable to locate ThemeResolver with name '" + THEME_RESOLVER_BEAN_NAME + "': using default [" +
//								this.themeResolver + "]");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the HandlerMappings used by this class.
//	 * <p>If no HandlerMapping beans are defined in the BeanFactory for this namespace,
//	 * we default to BeanNameUrlHandlerMapping.
//	 */
//	protected void initHandlerMappings(ApplicationContext context) {
//		this.handlerMappings = null;
//
//		if (this.detectAllHandlerMappings) {
//			// Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
//			Map<String, HandlerMapping> matchingBeans =
//					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
//			if (!matchingBeans.isEmpty()) {
//				this.handlerMappings = new ArrayList<HandlerMapping>(matchingBeans.values());
//				// We keep HandlerMappings in sorted order.
//				OrderComparator.sort(this.handlerMappings);
//			}
//		}
//		else {
//			try {
//				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
//				this.handlerMappings = Collections.singletonList(hm);
//			}
//			catch (NoSuchBeanDefinitionException ex) {
//				// Ignore, we'll add a default HandlerMapping later.
//			}
//		}
//
//		// Ensure we have at least one HandlerMapping, by registering
//		// a default HandlerMapping if no other mappings are found.
//		if (this.handlerMappings == null) {
//			this.handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("No HandlerMappings found in servlet '" + getServletName() + "': using default");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the HandlerAdapters used by this class.
//	 * <p>If no HandlerAdapter beans are defined in the BeanFactory for this namespace,
//	 * we default to SimpleControllerHandlerAdapter.
//	 */
//	protected void initHandlerAdapters(ApplicationContext context) {
//		this.handlerAdapters = null;
//
//		if (this.detectAllHandlerAdapters) {
//			// Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
//			Map<String, HandlerAdapter> matchingBeans =
//					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
//			if (!matchingBeans.isEmpty()) {
//				this.handlerAdapters = new ArrayList<HandlerAdapter>(matchingBeans.values());
//				// We keep HandlerAdapters in sorted order.
//				OrderComparator.sort(this.handlerAdapters);
//			}
//		}
//		else {
//			try {
//				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
//				this.handlerAdapters = Collections.singletonList(ha);
//			}
//			catch (NoSuchBeanDefinitionException ex) {
//				// Ignore, we'll add a default HandlerAdapter later.
//			}
//		}
//
//		// Ensure we have at least some HandlerAdapters, by registering
//		// default HandlerAdapters if no other adapters are found.
//		if (this.handlerAdapters == null) {
//			this.handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("No HandlerAdapters found in servlet '" + getServletName() + "': using default");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the HandlerExceptionResolver used by this class.
//	 * <p>If no bean is defined with the given name in the BeanFactory for this namespace,
//	 * we default to no exception resolver.
//	 */
//	protected void initHandlerExceptionResolvers(ApplicationContext context) {
//		this.handlerExceptionResolvers = null;
//
//		if (this.detectAllHandlerExceptionResolvers) {
//			// Find all HandlerExceptionResolvers in the ApplicationContext, including ancestor contexts.
//			Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils
//					.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
//			if (!matchingBeans.isEmpty()) {
//				this.handlerExceptionResolvers = new ArrayList<HandlerExceptionResolver>(matchingBeans.values());
//				// We keep HandlerExceptionResolvers in sorted order.
//				OrderComparator.sort(this.handlerExceptionResolvers);
//			}
//		}
//		else {
//			try {
//				HandlerExceptionResolver her =
//						context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
//				this.handlerExceptionResolvers = Collections.singletonList(her);
//			}
//			catch (NoSuchBeanDefinitionException ex) {
//				// Ignore, no HandlerExceptionResolver is fine too.
//			}
//		}
//
//		// Ensure we have at least some HandlerExceptionResolvers, by registering
//		// default HandlerExceptionResolvers if no other resolvers are found.
//		if (this.handlerExceptionResolvers == null) {
//			this.handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("No HandlerExceptionResolvers found in servlet '" + getServletName() + "': using default");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the RequestToViewNameTranslator used by this servlet instance.
//	 * <p>If no implementation is configured then we default to DefaultRequestToViewNameTranslator.
//	 */
//	protected void initRequestToViewNameTranslator(ApplicationContext context) {
//		try {
//			this.viewNameTranslator =
//					context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Using RequestToViewNameTranslator [" + this.viewNameTranslator + "]");
//			}
//		}
//		catch (NoSuchBeanDefinitionException ex) {
//			// We need to use the default.
//			this.viewNameTranslator = getDefaultStrategy(context, RequestToViewNameTranslator.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("Unable to locate RequestToViewNameTranslator with name '" +
//						REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME + "': using default [" + this.viewNameTranslator +
//						"]");
//			}
//		}
//	}
//
//	/**
//	 * Initialize the ViewResolvers used by this class.
//	 * <p>If no ViewResolver beans are defined in the BeanFactory for this
//	 * namespace, we default to InternalResourceViewResolver.
//	 */
//	protected void initViewResolvers(ApplicationContext context) {
//		this.viewResolvers = null;
//
//		if (this.detectAllViewResolvers) {
//			// Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
//			Map<String, ViewResolver> matchingBeans =
//					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
//			if (!matchingBeans.isEmpty()) {
//				this.viewResolvers = new ArrayList<ViewResolver>(matchingBeans.values());
//				// We keep ViewResolvers in sorted order.
//				OrderComparator.sort(this.viewResolvers);
//			}
//		}
//		else {
//			try {
//				ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
//				this.viewResolvers = Collections.singletonList(vr);
//			}
//			catch (NoSuchBeanDefinitionException ex) {
//				// Ignore, we'll add a default ViewResolver later.
//			}
//		}
//
//		// Ensure we have at least one ViewResolver, by registering
//		// a default ViewResolver if no other resolvers are found.
//		if (this.viewResolvers == null) {
//			this.viewResolvers = getDefaultStrategies(context, ViewResolver.class);
//			if (logger.isDebugEnabled()) {
//				logger.debug("No ViewResolvers found in servlet '" + getServletName() + "': using default");
//			}
//		}
//	}
	
	/******************************************
	 * 以下的所有方法都是继承于DispatcherServlet，不做任何特别处理，但因为所有这些方法都是protected，<br>
	 * PillowDispatcherServlet无法直接访问PillowCommonDispatcherServlet的父级（DispatcherServlet）中的protected方法，
	 * 因此需要重写这些方法，PillowDispatcherServlet和PillowCommonDispatcherServlet在同一个包中，访问权限为default，可以访问protected方法
	 * ****************************************/
	/********************目前不用了，原因参见PillowDispatcherServlet*******************/
//	/**
//	 * 
//	 */
//	@Override
//	protected void onRefresh(ApplicationContext context) {
//		// TODO Auto-generated method stub
//		super.onRefresh(context);
//	}
//
//	@Override
//	protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
//		// TODO Auto-generated method stub
//		return super.getDefaultStrategy(context, strategyInterface);
//	}
//
//	@Override
//	protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
//		// TODO Auto-generated method stub
//		return super.getDefaultStrategies(context, strategyInterface);
//	}
//
//	@Override
//	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
//		// TODO Auto-generated method stub
//		return super.createDefaultStrategy(context, clazz);
//	}
//
//	@Override
//	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		// TODO Auto-generated method stub
//		super.doDispatch(request, response);
//	}
//	@Override
//	protected void cleanupMultipart(HttpServletRequest request) {
//		// TODO Auto-generated method stub
//		super.cleanupMultipart(request);
//	}
//	@Override
//	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
//		// TODO Auto-generated method stub
//		return super.checkMultipart(request);
//	}

	/********************目前不用了，原因参见PillowDispatcherServlet*******************/
	/*****************************************************getter and setter******************************/
//
//	public LocaleResolver getLocaleResolver() {
//		return localeResolver;
//	}
//	public ThemeResolver getThemeResolver() {
//		return themeResolver;
//	}
//	public List<HandlerMapping> getHandlerMappings() {
//		return handlerMappings;
//	}
//	public List<HandlerAdapter> getHandlerAdapters() {
//		return handlerAdapters;
//	}
//	public List<HandlerExceptionResolver> getHandlerExceptionResolvers() {
//		return handlerExceptionResolvers;
//	}
//	public RequestToViewNameTranslator getViewNameTranslator() {
//		return viewNameTranslator;
//	}
//	public List<ViewResolver> getViewResolvers() {
//		return viewResolvers;
//	}
}
