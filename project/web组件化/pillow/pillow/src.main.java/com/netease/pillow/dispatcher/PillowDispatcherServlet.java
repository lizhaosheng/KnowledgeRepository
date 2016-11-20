package com.netease.pillow.dispatcher;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.netease.pillow.PillowDomain;


/**
 * 参考DispatcherServlet，并做少许改动
 * @author lizhaosheng
 * @version 2014-10-15 上午10:16:05
 */
@SuppressWarnings("serial")
public class PillowDispatcherServlet extends DispatcherServlet{
	
	private static final String DEFAULT_EMPTY_CONTEXT_LOCATION = "classpath:com/netease/pillow/xml/pillow-empty-context.xml";
	
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
	
	/**
	 * 通用处理器，提供通用方案
	 */
	private PillowCommonDispatcherServlet commonDispatcherServlet;

	/**
	 * 关联组件
	 */
	private PillowDomain domain = null;
	
//	/**
//	 * 若不指定策略，是否使用通用策略
//	 */
//	private Boolean isCommonEnable = true;

	/**
	 * 防止重复刷新（createWebApplicationContext会发布事件OnApplicationEvent,会刷新上下文）；
	 * onRefresh(wac);也会刷新上下文
	 */
	private boolean refreshEventReceived = false;

	/**
	 * 通用处理器，提供通用方案
	 */
	private PillowDispatchController dispatcherController;

	
	public PillowDispatcherServlet(PillowCommonDispatcherServlet common, PillowDomain domain){
		commonDispatcherServlet = common;
		this.domain  = domain;
	}
	public PillowDispatcherServlet(PillowDispatchController controller, PillowDomain domain){
		this.dispatcherController = controller;
		this.domain  = domain;
	}

	/**
	 * Initialize and publish the WebApplicationContext for this servlet.
	 * <p>Delegates to {@link #createWebApplicationContext} for actual creation
	 * of the context. Can be overridden in subclasses.
	 * @return the WebApplicationContext instance
	 * @see #setContextClass
	 * @see #setContextConfigLocation
	 */
	protected WebApplicationContext initWebApplicationContext() {
		//在PillowDispatchController.createPillowDispatcher中设置无作用.只能在这里设置
		String configLocation = domain.getInitServletParameter(CONFIG_LOCATION_PARAM);
		if(configLocation == null){
			configLocation = DEFAULT_EMPTY_CONTEXT_LOCATION;
		}
		setContextConfigLocation(configLocation);
		WebApplicationContext wac = findWebApplicationContext();
		if (wac == null) {
			// No fixed context defined for this servlet - create a local one.
			WebApplicationContext parent =
					(WebApplicationContext) domain.getContext();
			setContextClass(PillowDispatcherApplicationContext.class);
			
			wac = createWebApplicationContext(parent);
		}
		
		//refresh!!!
		if(!refreshEventReceived){
			onRefresh(wac);
		}
		
		return wac;
	}

	/**
	 * 设置类加载器
	 */
	@Override
	protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac){
		PillowDispatcherApplicationContext context = (PillowDispatcherApplicationContext)wac;
		context.setClassLoader(domain.getContextClassLoader());
		context.setCommonApplicationContext((AbstractApplicationContext)dispatcherController.getWebApplicationContext());
		context.setCommonEnable(domain.isUseCommon());
	}
	/**
	 * Callback that receives refresh events from this servlet's WebApplicationContext.
	 * <p>The default implementation calls {@link #onRefresh},
	 * triggering a refresh of this servlet's context-dependent state.
	 * @param event the incoming ApplicationContext event
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		this.refreshEventReceived  = true;
		onRefresh(event.getApplicationContext());
	}
	
	
	/***************************************************************************************
	 * 因为已将commondispatcher中所有bean都加入到了组件的上下文环境（PillowDispatcherApplicationContext），
	 * 因此不再需要从commondispatcher获取通用处理策略，下面代码无用~
	 * *************************************************************************************/
	
//	/**
//	 * 在初始化时调用，
//	 * 重写默认策略方法，默认从通用处理PillowCommonDispatcherServlet中获取
//	 */
//	@Override
//	protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
//		//使用PillowCommonDispatcherServlet作为通用处理
//		if(isCommonEnable){
//			//没什么好方法，暂时这么做吧
//			if(strategyInterface.equals(MultipartResolver.class)){
//				return (T) commonDispatcherServlet.getMultipartResolver();
//			}
//			if(strategyInterface.equals(LocaleResolver.class)){
//				return (T) commonDispatcherServlet.getLocaleResolver();		
//			}
//			if(strategyInterface.equals(ThemeResolver.class)){
//				return (T) commonDispatcherServlet.getThemeResolver();
//			}
////			if(strategyInterface.equals(HandlerMapping.class)){
////				return (T) commonDispatcherServlet.getHandlerMappings();
////			}
////			if(strategyInterface.equals(HandlerAdapter.class)){
////				return (T) commonDispatcherServlet.getHandlerAdapters();
////			}
////			if(strategyInterface.equals(HandlerExceptionResolver.class)){
////				return (T) commonDispatcherServlet.getHandlerExceptionResolvers();
////			}
//			if(strategyInterface.equals(RequestToViewNameTranslator.class)){
//				return (T) commonDispatcherServlet.getViewNameTranslator();
//			}
////			if(strategyInterface.equals(ViewResolver.class)){
////				return (T) commonDispatcherServlet.getViewResolvers();
////			}
//		}
//		return super.getDefaultStrategy(context, strategyInterface);
//	}
//	/**
//	 * 在初始化时调用，
//	 * 重写默认策略方法，默认从通用处理PillowCommonDispatcherServlet中获取
//	 */
//	@Override
//	protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
//		//使用PillowCommonDispatcherServlet作为通用处理
//		if(isCommonEnable){
//			//没什么好方法，暂时这么做吧
////			if(strategyInterface.equals(MultipartResolver.class)){
////				return (List<T>) commonDispatcherServlet.getMultipartResolver();
////			}
////			if(strategyInterface.equals(LocaleResolver.class)){
////				return (List<T>) commonDispatcherServlet.getLocaleResolver();		
////			}
////			if(strategyInterface.equals(ThemeResolver.class)){
////				return (List<T>) commonDispatcherServlet.getThemeResolver();
////			}
//			if(strategyInterface.equals(HandlerMapping.class)){
//				return (List<T>) commonDispatcherServlet.getHandlerMappings();
//			}
//			if(strategyInterface.equals(HandlerAdapter.class)){
//				return (List<T>) commonDispatcherServlet.getHandlerAdapters();
//			}
//			if(strategyInterface.equals(HandlerExceptionResolver.class)){
//				return (List<T>) commonDispatcherServlet.getHandlerExceptionResolvers();
//			}
////			if(strategyInterface.equals(RequestToViewNameTranslator.class)){
////				return (List<T>) commonDispatcherServlet.getViewNameTranslator();
////			}
//			if(strategyInterface.equals(ViewResolver.class)){
//				return (List<T>) commonDispatcherServlet.getViewResolvers();
//			}
//		}
//		return super.getDefaultStrategies(context, strategyInterface);
//	}
//	/**
//	 * 处理请求时调用，
//	 * 若multipartResolver == null，则调用通用处理PillowCommonDispatcherServlet的checkMultipart
//	 */
//	@Override
//	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
//		//使用PillowCommonDispatcherServlet作为通用处理
//		if(getMultipartResolver() == null && isCommonEnable){
//			return commonDispatcherServlet.checkMultipart(request);
//		}
//		return super.checkMultipart(request);
//	}
//	/**
//	 * Clean up any resources used by the given multipart request (if any).
//	 * @param request current HTTP request
//	 * @see MultipartResolver#cleanupMultipart
//	 */
//	protected void cleanupMultipart(HttpServletRequest request) {
//		MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
//		if (multipartRequest != null) {
//			if(getMultipartResolver() == null && isCommonEnable){
//				commonDispatcherServlet.cleanupMultipart(multipartRequest);
//			}
//			else if(getMultipartResolver() != null ){
//				getMultipartResolver().cleanupMultipart(multipartRequest);
//			}
//		}
//	}

}
