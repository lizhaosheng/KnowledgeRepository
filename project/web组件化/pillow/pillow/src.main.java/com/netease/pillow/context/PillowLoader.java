package com.netease.pillow.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import com.netease.pillow.PillowDomain;
import com.netease.pillow.PillowManager;
import com.netease.pillow.loader.PillowClassLoader;
import com.netease.pillow.xml.PillowConfig;
import com.netease.pillow.xml.PillowConfigtLoader;



/**
 * 组件化监听器
 * spring root context初始化完成后调用；<br>
 * 暂时只支持组件为jar形式，并且放在WEB-INF/lib/plugins下；<br>
 * 通过pluginService查询哪些plugin是启用的，获取启用plugin列表；<br>
 * 依次启动各个plugin（注意依赖关系）；<br>
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public class PillowLoader{
	
	private static final Logger log = Logger.getLogger(PillowLoader.class);
	/**
	 * spring 根上下文
	 */
	private WebApplicationContext rootContext;
	/**
	 * 构造方法，初始化
	 */
	public PillowLoader(){
		initInternal();
	}
	private void initInternal(){
		log.debug("Init pillow loader.");
	}
	
	/**
	 * 加载组件
	 * @param clList - 组件配置文件加载器集合
	 * @param loaderParamMap - 加载器参数
	 * @param pillowManager
	 * @throws Exception
	 */
	public Map<String, PillowDomain> loadPillows(List<PillowClassLoader>clList, PillowManager pillowManager) throws Exception {
		if(pillowManager.getPillowEnvInitState() < PillowManager.CONTEXT_ENV_INITED_STATE){
			throw new Exception("The loader environment is not inited yet!");
		}
		if(clList == null || clList.isEmpty()){
			log.info("Found 0 pillow classloader, finish to load pillows.");
			return null;
		}
		log.info("Start to load pillows. Size " + clList.size());
		
		int num = 0;
		List<List<PillowDomain>> domainList = new ArrayList<List<PillowDomain>>();
		for(PillowClassLoader loader:clList){
			try{
				//添加到加载器的类路径
				loader.addToClassPath();
				//创建组件上下文
				List<PillowDomain> temp = createDomains(loader,pillowManager);
				if(temp == null){
					log.warn("No pillow for loader["+ loader.getBasePath() +"]");
					continue;
				}
				num = num + temp.size();
				loader.setLoadDomainList(temp);
				domainList.add(temp);
			}
			catch(Exception e){
				log.error("Failed to create pillow domain in'"+ loader.getBasePath() +"'",e);
			}
		}
		log.info("Found " + num + " pillow(s).");
		/*************************************旧的加载方式**********************************/
		//组件名及其配置，组件名全局唯一。检查组件配置是否有误，并生成组件依赖树
//		List<PillowDomain> availableDomainList = new ArrayList<PillowDomain>();
//		if(!domainList.isEmpty()){
//			//生成依赖树
//			Map<String, PillowDomain> map = createDomainTree(domainList,availableDomainList,pillowManager);
//			//加载到系统中
//			add2Forest(map,availableDomainList,pillowManager);
//			//遍历组件树，生成上下文
//			loadContexts(availableDomainList,pillowManager);
//		}
//		//remove not used classloader
//		removeInvilidDomain(clList,availableDomainList);
//		
//		log.info("Finish to load pillows.");
//		
//		pillowManager.postPillowContextInit();
		/*************************************end**********************************/
		
		//去重名
		Map<String, PillowDomain> map = distinct(domainList,pillowManager);
		log.info(map.size() + " pillow(s) wait for load after distinct.");
		//初始化载入
		for(List<PillowDomain> list:domainList){
			for (PillowDomain pd : list) {
				try{
					//初始化组件，失败的将从map中移除，当其组件集合的最上层节点将被放到availableDomainList中
					initContexts(pd.getPillowName(),map,pillowManager);
				}
				catch(Exception e){
					log.error("Failed to init pillow'"+ pd.getPillowName() +"'",e);
				}
			}
		}
		log.info("Success to load " + map.size() + " pillow(s).\n And " + (num - map.size()) + " Failed.");
		//remove not used classloader
		removeInvilidLoader(clList,map);
		log.info("Finish to load pillows.");
		pillowManager.postPillowContextInit();
		
		return map;
	}
	
	/**
	 * 根据指定目录，获取所有的组件配置文件，并加载
	 * @param map 
	 * @return
	 * @throws IOException 
	 */
	private List<PillowDomain> createDomains(PillowClassLoader loader, PillowManager pillowManager) throws IOException {

		//根据路径，获取配置文件，并创建配置信息对象。利用spring中的resourcereader
		PillowConfigtLoader configLoader = new PillowConfigtLoader(loader);
		List<PillowConfig> configList = configLoader.loadPillowConfigs(pillowManager);
		if(configList == null ||  configList.isEmpty()){
			return null;
		}
		List<PillowDomain> domainList = new ArrayList<PillowDomain>();
		for(PillowConfig config:configList){
			PillowDomain domain = new PillowDomain();
			domain.setPillowConfig(config);
			domain.setConfigClassLoader(loader);
			domainList.add(domain);
		}
		return domainList;
	}
	
	private Map<String, PillowDomain> distinct(List<List<PillowDomain>> domainList,PillowManager pillowManager) {
		List<String> nameList = new ArrayList<String>();
		
		Map<String, PillowDomain> map = new HashMap<String, PillowDomain>();
		
		for(List<PillowDomain> list:domainList){
			for (PillowDomain pd : list) {
				//防止重名
				if(map.put(pd.getPillowName(),pd) != null || 
						pillowManager.isContainPillowDomain(pd.getPillowName())){
					nameList.add(pd.getPillowName());
				}
			}
		}
		for(String name : nameList){
			map.remove(name);
			log.error("More than one pillow with the same name '" + name + "'!");
		}
		return map;
	}
	private boolean initContexts(String name, Map<String, PillowDomain> map, PillowManager pillowManager) {
		
		if(!map.containsKey(name)){
			return false;
		}
		
		PillowDomain domain = map.get(name);
		
		if(domain.getParentName() == null){
			initContextClassLoader(domain);
			if(loadContext(domain,pillowManager)){
				pillowManager.addDomainRoot(domain);
				pillowManager.putPillowDomain(domain.getPillowName(), domain);
				return true;
			}
			//失败，移除组件
			removeDomain(domain);
			map.remove(name);
			return false;
		}
		else if(pillowManager.isContainPillowDomain(domain.getParentName())){
			domain.setParent(pillowManager.getPillowDomain(domain.getParentName()));
			initContextClassLoader(domain);
			if(loadContext(domain,pillowManager)){
				pillowManager.putPillowDomain(domain.getPillowName(), domain);
				pillowManager.getPillowDomain(domain.getParentName()).addChild(domain);
				return true;
			}
			//失败，移除组件
			removeDomain(domain);
			map.remove(name);
			return false;
		}
		else if(initContexts(domain.getParentName(),map,pillowManager)){
			domain.setParent(pillowManager.getPillowDomain(domain.getParentName()));
			initContextClassLoader(domain);
			if(loadContext(domain,pillowManager)){
				pillowManager.putPillowDomain(domain.getPillowName(), domain);
				pillowManager.getPillowDomain(domain.getParentName()).addChild(domain);
				return true;
			}
			//失败，移除组件
			removeDomain(domain);
			map.remove(name);
			return false;
		}
		//加载失败，移除
		map.remove(name);
		return false;
	}

	private void removeDomain(PillowDomain domain) {
		domain.getContextClassLoader().removeFromClassPath();
		domain.clear();
	}
	/**
	 * 
	 * @param domain
	 * @param pillowManager
	 */
	private void initContextClassLoader(PillowDomain domain) {
		PillowClassLoader cl = domain.getConfigClassLoader().getContextClassLoader(domain.getPillowName());
		PillowClassLoader parent = null;
		if(domain.getParent() != null){
			parent = domain.getParent().getContextClassLoader();
		}
		cl.setParentPillowClassLoader(parent);
		domain.setContextClassLoader(cl);
	}
	
	/**
	 * 加载初始化组件，若组件有父级，则先加载初始化父级
	 * @param servletContext
	 * @param config
	 */
	private boolean loadContext(PillowDomain domain, PillowManager pillowManager) {
		ApplicationContext father;
		if(domain.getParentName() != null){
			PillowDomain fatherDomain = pillowManager.getPillowDomain(domain.getParentName());
			father = fatherDomain.getContext();
		}
		else{
			father = rootContext;
		}
		PillowContextLoader contextLoader = new PillowContextLoader();
		//因为mybatis加载mapper的ClassLoaderWrapper是一个静态变量，类加载器获取方式固定
		//(org.apache.ibatis.io.ClassLoaderWrapper.getClassLoaders)所以通过设置线程加载器来将
		//让ClassLoaderWrapper获取正确的加载器
		ClassLoader old = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(domain.getContextClassLoader());
		ApplicationContext context = contextLoader.initPillowContext(father, domain, pillowManager);
		Thread.currentThread().setContextClassLoader(old);
		
		if(context != null){
			domain.setContext(context);
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 对于一些加载失败的jar，从类路径中移除
	 * @param clList
	 * @param list
	 */
	private void removeInvilidLoader(List<PillowClassLoader>clList, Map<String, PillowDomain> map) {
		Set<PillowClassLoader> usingClassLoader = new HashSet<PillowClassLoader>();
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()){
			usingClassLoader.add(map.get(it.next()).getConfigClassLoader());
		}
		for(PillowClassLoader loader:clList){
			if(usingClassLoader.contains(loader)){
				continue;
			}
			loader.removeFromClassPath();
			log.info("Loader["+ loader.getBasePath() +"] is not used, remove from classpath!");
		}
	}

	
//	/**
//	 * 将新加载的组件加入到系统中
//	 * @param map
//	 * @param availableDomainList
//	 * @param pillowManager
//	 */
//	private void add2Forest(Map<String, PillowDomain> map, List<PillowDomain> availableDomainList, PillowManager pillowManager) {
//		
//		for(PillowDomain domain:availableDomainList){
//			PillowDomain parent = null;
//			if(domain.getParentName() == null){
//				pillowManager.addDomainRoot(domain);
//			}
//			else{
//				parent = pillowManager.getPillowDomain(domain.getParentName());
//				domain.setParent(parent);
//				parent.addChild(domain);
//			}
//		}
//		pillowManager.putDomainAll(map);
//		
//	}
//
//	/**
//	 * 检查依赖关系并生成依赖树<br>
//	 * 主要是是否存在环，是否存在重名组件。
//	 * @param pillowManager 
//	 * @param pillowManager 
//	 * @param configList
//	 * @return -
//	 * @throws PillowLoadException 
//	 */
//	private Map<String, PillowDomain> createDomainTree(List<List<PillowDomain>> domainList, List<PillowDomain> result, PillowManager pillowManager){
//		
//		log.info(domainList.size() + " pillow(s) need to be load.");
//		Map<String, PillowDomain> resultMap = new HashMap<String, PillowDomain>();
//		
//		// 父级id及其所有直接子节点的映射关系
//		Map<String, List<PillowDomain>> map = new HashMap<String, List<PillowDomain>>();
//		for(List<PillowDomain> list:domainList){
//			for (PillowDomain pd : list) {
//				//防止重名
//				if(pillowManager.isContainPillowDomain(pd.getPillowName()) || resultMap.put(pd.getPillowName(), pd) != null){
//					log.error("Pillow name '" + pd.getPillowName() + "' already exist!");
//					continue;
//				}
//				// 父级id
//				String parentName = pd.getParentName();
//	
//				// 不存在父级id, 或者父级已经被加载且能够被继承
//				if (parentName== null || 
//						(pillowManager.isContainPillowDomain(parentName) && 
//								pillowManager.getPillowDomain(parentName).isCanExtends())){
//					result.add(pd);
//					continue;
//				}
//				if(pillowManager.isContainPillowDomain(parentName) && 
//								!pillowManager.getPillowDomain(parentName).isCanExtends()){
//					log.error("Pillow [" + pd.getPillowName() + "] can not be load. For its parent can't be extends!");
//					continue;
//				}
//				// 判断当前map中是否已存在相同父级id的项,不存在则新建一项
//				if (!map.containsKey(parentName)) {
//					map.put(parentName, new ArrayList<PillowDomain>());
//				}
//				map.get(parentName).add(pd);
//			}
//		}
//
//		//根据总父节点个数-被加载的总父节点个数=未被加载节点数
//		int num = getChilds(result, null, map);
//		log.info("Success to load " + num + " pillow(s).");
//		log.info("Fail to load " + (domainList.size() - num) + " pillow(s).");
//		
//		return resultMap;
//	}
//
//	/**
//	 * 获取给定组件列表的子级，递归调用方法（循环依赖的组件将不加载）
//	 * 
//	 * @param result
//	 *            - 同一父节点下的所有子节点集合
//	 * @param map
//	 * 
//	 * return - 返回父节点个数
//	 */
//	private int getChilds(List<PillowDomain> result, PillowDomain father, Map<String, List<PillowDomain>> map) {
//		
//		int num = 0;
//		// 遍历当前同一父节点下的所有子节点集合
//		for (PillowDomain pd : result) {
//			if(father != null && !father.isCanExtends()){
//				log.error("Pillow [" + pd.getPillowName() + "] can not be load. For its parent can't be extends!");
//				continue;
//			}
//			//设置父级组件
//			pd.setParent(father);
//			// 当前节点
//			String id = pd.getPillowName();
//			// 若当前节点包含子节点（map的key是父节点）
//			if (map.containsKey(id)) {
//				// 将当前节点的所有子节点加入child list中
//				pd.setChildren(map.get(id));
//				// 对当前节点底下的子节点集合进行深度优先递归调用
//				return getChilds(pd.getChildren(), pd, map);
//			}
//			num++;
//		}
//		return num;
//	}
//	
//	/**
//	 * 创建并初始化组件上下文
//	 * @param servletContext
//	 * @param availableDomainList
//	 */
//	private void loadContexts(List<PillowDomain> availableDomainList, PillowManager pillowManager) {
//		for(PillowDomain domain:availableDomainList){
//			//创建设置组件的上下文加载器
//			initContextClassLoader(domain);
//			loadContext(domain,pillowManager);
//			if(domain.getChildren() != null){
//				loadContexts(domain.getChildren(), pillowManager);
//			}
//		}
//	}

}
