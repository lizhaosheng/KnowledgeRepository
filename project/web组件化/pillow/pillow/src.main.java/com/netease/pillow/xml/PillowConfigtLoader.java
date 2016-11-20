package com.netease.pillow.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.PluggableSchemaResolver;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import com.netease.pillow.PillowManager;
import com.netease.pillow.exception.PillowLoadException;
import com.netease.pillow.loader.PillowClassLoader;



/**
 * 默认的配置文件加载器，即jar文件加载器<br>
 * 暂时只支持组件为jar形式，并且放在WEB-INF/lib/plugins下；<br>
 * 通过pluginService查询哪些plugin是启用的，获取启用plugin列表；<br>
 * 依次启动各个plugin（注意依赖关系）；<br>
 * 参考
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}<br>
 * @author lizhaosheng
 * @version 2014-10-13 上午11:19:22
 */
public class PillowConfigtLoader {
	
	private static final Logger logger = Logger.getLogger(PillowConfigtLoader.class);

	private final Log saxErrorLoger = new Log4JLogger();
	/**
	 * Indicates that the validation should be disabled.
	 */
	public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

	/**
	 * Indicates that the validation mode should be detected automatically.
	 */
	public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

	/**
	 * Indicates that DTD validation should be used.
	 */
	public static final int VALIDATION_DTD = XmlValidationModeDetector.VALIDATION_DTD;

	/**
	 * Indicates that XSD validation should be used.
	 */
	public static final int VALIDATION_XSD = XmlValidationModeDetector.VALIDATION_XSD;
	
	/**
	 * 默认加载路径
	 */
	private static final String DEFAULT_PILLOW_LOCATION = "classpath*:pillow/conf/pillow.xml";
	
	/**
	 * 默认XSD的systemid和加载路径的映射，systemId即根节点中xsi:schemaLocation=
	 */
	private static final String DEFAULT_PILLOW_SCHEMA_MAPPINGS_LOCATION = "com/netease/pillow/xml/pillow.schemas";
	
	/**
	 * 组件配置文件路径变量
	 */
	public static String PILLOW_LOCATION_ATTRIBUE = "pillowConfigLocation";
	
	/**
	 * 验证模式，默认xsd验证
	 */
	private int validationMode;
	
	/**
	 * 利用spring中的PathMatchingResourcePatternResolver,加载类路径的
	 */
	private PathMatchingResourcePatternResolver resolver;

	/**
	 * 文档解析器
	 */
	private DocumentLoader documentLoader;

	/**
	 * 用于处理xsd
	 */
	private EntityResolver entityResolver;

	/**
	 * 文档验证错误处理
	 */
	private SimpleSaxErrorHandler errorHandler;
	
	/**
	 * 当前资源的缓存，提高效率
	 */
	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded =
			new NamedThreadLocal<Set<EncodedResource>>("XML pillow definition resources currently being loaded");
	
	private boolean namespaceAware = false;

	private PillowClassLoader loader;
	/**
	 * 构造函数
	 * @param loader
	 */
	public PillowConfigtLoader() {
		
		this.resolver = new PathMatchingResourcePatternResolver();
		this.documentLoader = new PillowDocumentLoader();
		this.entityResolver = new PluggableSchemaResolver(resolver.getClassLoader(),DEFAULT_PILLOW_SCHEMA_MAPPINGS_LOCATION);
		this.validationMode = VALIDATION_XSD;
		this.errorHandler = new SimpleSaxErrorHandler(saxErrorLoger);
		try {
			this.loader = new PillowClassLoader(new URL(""),resolver.getClassLoader());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 构造函数
	 * @param loader
	 */
	public PillowConfigtLoader(PillowClassLoader loader) {
		this.loader = loader;
		this.resolver = new PathMatchingResourcePatternResolver(loader);
		this.documentLoader = new PillowDocumentLoader();
		this.entityResolver = new PluggableSchemaResolver(loader,DEFAULT_PILLOW_SCHEMA_MAPPINGS_LOCATION);
		this.validationMode = VALIDATION_XSD;
		this.errorHandler = new SimpleSaxErrorHandler(saxErrorLoger);
	}

	/**
	 * 先定位并载入文件资源<br>
	 * 然后利用schema验证并加载为document对象<br>
	 * 接着从document中解析为PillowConfig<br>
	 * 最后对PillowConfig进行其他处理
	 */
	public List<PillowConfig> loadPillowConfigs(PillowManager pillowManager) throws IOException {
		
		String location = null;
		if(loader.getParamMap() != null){
			location = (String) loader.getParamMap().get(PILLOW_LOCATION_ATTRIBUE);
		}
		//资源定位
		if(location == null){
			location = pillowManager.getPillowLocation();
			if(location == null){
				location = DEFAULT_PILLOW_LOCATION;
			}
		}
		if(logger.isInfoEnabled()){
			logger.info("Start load pillow config from '"+ location +"'");
		}
		Resource[] resources = resolver.getResources(location);
		if(resources == null || resources.length == 0){
			logger.info("Can't find any resource in '" + location + "'");
			return null;
		}
		List<PillowConfig> configList = loadPillowConfigs(resources);
		if(logger.isInfoEnabled()){
			logger.info("Total load "+ configList.size() +" pillow(s) config.");
		}
		return configList;
	}
	
	/**
	 * 循环加载所有组件配置文件
	 * @param resources
	 * @return
	 */
	private List<PillowConfig> loadPillowConfigs(Resource[] resources) {
		List<PillowConfig> list = new ArrayList<PillowConfig>();
		for (Resource resource : resources) {
			try {
				if(!resource.getURL().toExternalForm().startsWith(
						loader.getBasePath().toExternalForm())){
					logger.warn("Resource from '"+ resource.getURI() + "' is not for the loader of '" + loader.getBasePath() + "'");
					continue;
				}
				int num =doLoadPillowConfig(list,new EncodedResource(resource));
				logger.info("Load " + num + " pillow(s) from '"+ resource.getURI() + "'");
			} catch (IOException e) {
				try {
					logger.error("Failed to load pillow in'"+ resource.getURI() +"'",e);
				} catch (IOException e1) {
				}
			}
			
		}
		return list;
	}

	private int doLoadPillowConfig(List<PillowConfig> list, EncodedResource encodedResource) {
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isInfoEnabled()) {
			logger.info("Loading XML pillow definitions from " + encodedResource.getResource());
		}

		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (currentResources == null) {
			currentResources = new HashSet<EncodedResource>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			throw new PillowLoadException(
					"Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				Document doc= documentLoader.loadDocument(
						inputSource, getEntityResolver(), errorHandler, validationMode, isNamespaceAware() );
				
				return pareDocument(list,doc);
			}
			catch (Throwable ex) {
				throw new BeanDefinitionStoreException(encodedResource.getResource().getDescription(),
						"Unexpected exception parsing XML document from " + encodedResource.getResource(), ex);
			}
			finally {
				inputStream.close();
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"IOException parsing XML document from " + encodedResource.getResource(), ex);
		}
		finally {
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}

	/**
	 * 解析文档
	 * @param list 
	 * @param doc
	 * @return
	 */
	private int pareDocument(List<PillowConfig> list, Document doc) {
		int num = 0;
		//将文档标准化，去除无用的空格和空行，即删除无用的text node(不过好像没作用，还是得自己来)
		doc.normalize();
		Element root = doc.getDocumentElement();
		NodeList childList = root.getChildNodes();
		if(childList == null){
			return num;
		}
		//
		for(int i = 0; i < childList.getLength(); i++) {
			Node node = childList.item(i);
			if(!PillowConfigParser.PILLOW_NODE.equals(node.getNodeName())){
				continue;
			}
			PillowConfig config = parse(node);
			if(config != null){
				list.add(config);
				num++;
			}
			if (logger.isInfoEnabled()) {
				logger.info("Load pillow '" + config.getPillowName() +"'");
			}
		}
		return num;
	}

	/**
	 * 解析单个组件节点
	 * @param node
	 * @return
	 */
	private PillowConfig parse(Node node) {

		PillowConfig config = new PillowConfig();
		//设置名称
		String name = node.getAttributes().getNamedItem(PillowConfigParser.PILLOW_NAME_ATTR).getNodeValue();
		config.setPillowName(name);
		
		//设置是否可继承，默认不可
		String canExtendsStr = node.getAttributes().getNamedItem(PillowConfigParser.PILLOW_CANEXTENDS_ATTR).getNodeValue();
		if("true".equals(canExtendsStr) || "1".equals(canExtendsStr)){
			config.setCanExtends(true);
		}
		
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++) {
			Node child = childList.item(i);
			if(PillowConfigParser.PARENT_NODE.equals(child.getNodeName())){
				String parent = child.getTextContent();
				config.setParentName(parent);
			}
			else if(PillowConfigParser.CONTEXT_NODE.equals(child.getNodeName())){
				parseContextNode(child,config);
			}
			else if(PillowConfigParser.SERVLET_NODE.equals(child.getNodeName())){
				parseServletNode(child,config);
			}
		}
		
		return config;
	}

	private void parseServletNode(Node node, PillowConfig config) {
		NodeList childList = node.getChildNodes();
		if(childList == null || childList.getLength() == 0){
			return;
		}
		//设置是否可继承，默认不可
		String useCommon = node.getAttributes().getNamedItem(PillowConfigParser.SERVLET_USECOMMON_ATTR).getNodeValue();
		if("false".equals(useCommon) || "0".equals(useCommon)){
			config.setUseCommon(false);
		}
		
		for(int i = 0; i < childList.getLength(); i++) {
			Node child = childList.item(i);
			if(PillowConfigParser.SERVLET_PARAM_NODE.equals(child.getNodeName())){
				parseParamNode(child,config.getInitServletParameterMap());
			}
			else if(PillowConfigParser.URL_PATTERM_NODE.equals(child.getNodeName())){
				String url = toRegular(child.getTextContent());
				config.setUrlMapping(url);
			}
			
		}
	}
	/**
	 * 转成表达式形式
	 * @param textContent
	 * @return
	 */
	private String toRegular(String textContent) {
		return textContent.replaceAll("\\*", ".*");
	}

	private void parseContextNode(Node node, PillowConfig config) {
		NodeList childList = node.getChildNodes();
		for(int i = 0; i < childList.getLength(); i++) {
			Node child = childList.item(i);
			if(PillowConfigParser.CONTEXT_PARAM_NODE.equals(child.getNodeName())){
				parseParamNode(child,config.getInitContextParameterMap());
			}
			
		}
	}

	
	private void parseParamNode(Node node, Map<String,String> paramMap) {
		NodeList childList = node.getChildNodes();
		String key = null,value = null;
		for(int i = 0; i < childList.getLength(); i++) {
			Node child = childList.item(i);
			if(PillowConfigParser.PARAM_NAME_NODE.equals(child.getNodeName())){
				key = child.getTextContent();
			}
			else if(PillowConfigParser.PARAM_VALUE_NODE.equals(child.getNodeName())){
				value = child.getTextContent();
			}
		}
		if(key != null){
			paramMap.put(key, value);
		}
	}

	
	/**
	 * Return the EntityResolver to use, building a default resolver
	 * if none specified.
	 */
	protected EntityResolver getEntityResolver() {
		return this.entityResolver;
	}
	/**
	 * 使用xsd
	 * @param resource
	 * @return
	 */
	protected int getValidationModeForResource(Resource resource) {
		return VALIDATION_XSD;
	}
	protected String getDefaultConfigLocations() {
		return DEFAULT_PILLOW_LOCATION;
	}

	public int getValidationMode() {
		return this.validationMode;
	}
	
	public void setValidationMode(int validationMode) {
		this.validationMode = validationMode;
	}
	
	public boolean isNamespaceAware() {
		return namespaceAware;
	}

	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}
}
