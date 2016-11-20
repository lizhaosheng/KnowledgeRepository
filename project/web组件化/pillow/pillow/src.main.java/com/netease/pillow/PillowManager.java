package com.netease.pillow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.netease.pillow.dispatcher.PillowDispatchController;
import com.netease.pillow.event.PillowContextFinishEventListener;
import com.netease.pillow.event.PillowDispatcherFinishEventListener;


/**
 * 组件管理器，单例模式（也可以考虑在spring中配置，使之加入到spring容器，未来讨论两种方式的优缺点）<br>
 * manager仅内部使用，不提供组件可见；<br>
 * 目前安全性问题未解决，主要是如何防止组件之间相互访问
 * @author lizhaosheng
 * @version 2014-10-13 下午6:39:50
 */
public class PillowManager {

	/**
	 * 单例，饥饿模式
	 */
	private static PillowManager manager = new PillowManager();
	
	/**
	 * 组件名与组件配置对象映射
	 */
	private Map<String,PillowDomain> pillowDomainMap =  new HashMap<String,PillowDomain>();
	
	/**
	 * 组件树的根集合
	 */
	private List<PillowDomain> rootDomainList = new ArrayList<PillowDomain>();

	/**
	 * 所有组件所属的servletcontext
	 */
	private ServletContext servletContext = null;

	/**
	 * spring根上下文
	 */
	private WebApplicationContext rootContext;
	
	/**
	 * 基础控制器
	 */
	private PillowDispatchController controller = null;
	
	/**
	 * 组件上下文初始化完成后调用的监听器
	 */
	private List<PillowContextFinishEventListener> contextEventList = new ArrayList<PillowContextFinishEventListener>();

	/**
	 * 组件dispatcher初始化完成后调用的监听器
	 */
	private List<PillowDispatcherFinishEventListener> dispatcherEventList = new ArrayList<PillowDispatcherFinishEventListener>();

	/**
	 * web.xml中配置的组件配置文件路径
	 */
	private String pillowLocation;
	
	/**
	 * 初始状态
	 */
	public static final short START_STATE = 0;
	/**
	 * 组件上下文所需环境初始化文笔
	 */
	public static final short CONTEXT_ENV_INITED_STATE = 2;
	/**
	 * 组件上下文监听器初始化执行完毕
	 */
	public static final short CONTEXT_INITED_STATE = 4;
//	/**
//	 * 组件控制器初始化完毕
//	 */
//	public static final short DISPATCHER_INITED_STATE = 6;
	/**
	 * 组件分发控制器初始化完毕
	 */
	public static final short CONTROLLER_INITED_STATE = 8;

	/**
	 * 初始化过程阶段状态
	 */
	private short pillowEnvInitState = START_STATE;
//	
//	/**
//	 * 组件环境是否已初始化完毕（指初始化组件所需要的环境，并非指所有组件初始化完毕）
//	 */
//	private boolean isDispatcherControllerInit = false;
	
	
	public PillowManager(){
	
	}
	
	public void registerContextFinishEvent(PillowContextFinishEventListener listener){
		contextEventList.add(listener);
	}
	public void registerDispatcherFinishEvent(PillowDispatcherFinishEventListener listener){
		dispatcherEventList.add(listener);
	}
	public void postPillowContextInit(){
		for(PillowContextFinishEventListener listener:contextEventList){
			listener.afterContextFinished();
		}
	}
	public void postPillowDispatcherInit(){
		for(PillowDispatcherFinishEventListener listener:dispatcherEventList){
			listener.afterDisptcherFinished();
		}
	}

	/**
	 * 获取单例实例，//包访问级别，只有处于当前包的类才能访问
	 * @return
	 */
	public static PillowManager getPillowManager(){
		return manager;
	}
	
	/**
	 * 保存组件的配置信息对象
	 * @param pillowName - 组件名 
	 * @param domain - 组件配置信息对象
	 */
	public void putPillowDomain(String pillowName, PillowDomain domain) {
		pillowDomainMap.put(pillowName, domain);
	}

	/**
	 * 获取组件上下文
	 * @param pillowName
	 * @return
	 */
	public PillowDomain getPillowDomain(String pillowName) {
		return pillowDomainMap.get(pillowName);
	}
	
	/**
	 * 添加根节点组件
	 * @param domain
	 */
	public void addDomainRoot(PillowDomain domain) {
		rootDomainList.add(domain);
	}

	/**
	 * 批量添加组件
	 * @param map
	 */
	public void putDomainAll(Map<String, PillowDomain> map) {
		pillowDomainMap.putAll(map);
	}

	/**
	 * 查询组件是否存在
	 * @param pillowName
	 * @return
	 */
	public boolean isContainPillowDomain(String pillowName) {
		return pillowDomainMap.containsKey(pillowName);
	}

	/******************** setter and getter ********************/	
	public ServletContext getServletContext() {
		return servletContext ;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public Map<String, PillowDomain> getPillowDomainMap() {
		return pillowDomainMap;
	}

	public WebApplicationContext getRootContext() {
		return rootContext;
	}

	public void setRootContext(WebApplicationContext rootContext) {
		this.rootContext = rootContext;
	}

	/**
	 * @return the controller
	 */
	public PillowDispatchController getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(PillowDispatchController controller) {
		this.controller = controller;
	}

	public String getPillowLocation() {
		return pillowLocation;
	}

	public void setPillowLocation(String location) {
		this.pillowLocation = location;
	}

	/**
	 * @return the pillowEnvInitState
	 */
	public short getPillowEnvInitState() {
		return pillowEnvInitState;
	}

	/**
	 * @param pillowEnvInitState the pillowEnvInitState to set
	 */
	public void setPillowEnvInitState(short pillowEnvInitState) {
		//不能设置比当前状态还要小的状态
		if(this.pillowEnvInitState >= pillowEnvInitState){
			return ;
		}
		this.pillowEnvInitState = pillowEnvInitState;
	}

	public boolean unLoadPillow(String name){
		if(pillowDomainMap.containsKey(name)){
			PillowDomain domain = pillowDomainMap.get(name);
			//递归调用
			if(domain.getChildren() != null && !domain.getChildren().isEmpty()){
				for(PillowDomain d:domain.getChildren()){
					unLoadPillow(d.getPillowName());
				}
			}
			//卸载类
			domain.getContextClassLoader().removeFromClassPath();
			//移除spring上下文
			//移除spring mvc上下文
			//从map中移除
			pillowDomainMap.remove(name);
			//从root中移除
			rootDomainList.remove(domain);
			
			return true;
		}
		return false;
	}
}
