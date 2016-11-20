package com.netease.pillow.loader;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * 相关服务
 * 
 * @author lizhaosheng
 * @version 2014-11-3 下午5:05:13
 */
public class JarClassLoader extends PillowClassLoader {
	private static final Logger log = Logger.getLogger(JarClassLoader.class);

	private JarURLConnection cachedJarFile = null;

	private Map<String,URL> resource = new HashMap<String,URL>();
	
	private Map<String,Vector<URL>> resources = new HashMap<String,Vector<URL>>();
//	
	private Vector<URL> jarRoot = new Vector<URL>();

	public static final String JAR_PROTOCOL = "jar:";
	
	public static final String JAR_SEP = "!/";
	
	private Map<String,JarClassLoader> contextClassLoaderMap = new HashMap<String,JarClassLoader>();
	// /**
	// * 资源文件缓存，软引用
	// */
	// private SoftReference<Map<String,Vector<URL>>> resources =
	// new SoftReference<Map<String,Vector<URL>>>(new HashMap<String,Vector<URL>>());

	public JarClassLoader(URL url, ClassLoader parentClassLoader) {
		super(url, parentClassLoader);
		setBasePath(url);
	}

	/**
	 * 将指定的文件url添加到类加载器的classpath中去，并缓存jar connection，方便以后卸载jar
	 * 
	 * @param 一个可想类加载器的classpath中添加的文件url
	 */
	@Override
	public void addToClassPath() {
		try {
			// 打开并缓存文件url连接
			getJarFile();
		} catch (Exception e) {
			log.error("Failed to cache plugin JAR file: " + getBasePath().toExternalForm(), e);
		}
		addRootPath();
		addURL(getBasePath());
//		try {
//			ClassLoaderUtils.setDefaultClassLoaderForMybatis(this);
//		} catch (ClassNotFoundException e) {
//			log.error("Try to set Mybatis defaultclassloader failed!",e);
//		}
	}
	private void addRootPath() {
		Vector<URL> v = new Vector<URL>();
		v.add(getBasePath());
		jarRoot = v;
		resources.put("", jarRoot);
	}

	/**
	 * 
	 */
	@Override
	public PillowClassLoader getContextClassLoader(String key){
		if(!contextClassLoaderMap.containsKey(key)){
			JarClassLoader loader = new JarClassLoader(getBasePath(),this.getParent());
			loader.addToClassPath();
			contextClassLoaderMap.put(key, loader);
		}
		return contextClassLoaderMap.get(key);
	}
	/**
	 * 卸载jar包
	 */
	@Override
	public void removeFromClassPath() {
		try {
			log.info("Unloading plugin JAR file " + cachedJarFile .getJarFile().getName());
			cachedJarFile.getJarFile().close();
			cachedJarFile = null;
			resource.clear();
			resources.clear();
		} catch (Exception e) {
			log.error("Failed to unload JAR file\n", e);
		}
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
//		log.info("=====>start to load class:" + name);
		Class<?> cl = null;
		try {
			if (parentPillowClassLoader != null) {
				cl = parentPillowClassLoader.loadClass(name);
			} else {
				cl = getParent().loadClass(name);
			}
		} catch (ClassNotFoundException e) {
			// If still not found, then invoke findClass in order
			// to find the class.
//			log.info("=====>try to load class:" + name + " ,from: " + getBasePath());
			cl = super.loadClass(name);
		}
//		if (cl != null) {
//			log.info("=====>success load: " + cl.getName());
//		}
		return cl;
	}

	/**
	 * 查找资源[自定义相对URL查找路径] 从以上的URLS中查找当前名称的资源 这个必须重写，因为是public
	 */
	public URL findResource(String name) {
//		log.info("=====>加载资源文件:" + name);
		
		if(!resource.containsKey(name)){
			try {
				JarEntry entry = getJarEntry(name);
				if(entry != null){
					resource.put(name, new URL(getBasePath().toExternalForm() + name));
				}
			} catch (MalformedURLException e) {
				log.error("======>Create URL failed!" + getBasePath().toExternalForm() + name, e);
				return null;
			} catch (IOException e) {
				log.error("======>get Resource from jar failed!" + getBasePath().toExternalForm() + name, e);
				return null;
			}
		}
		return resource.get(name);
	}

	

	/**
	  * 只加载目录,由于默认情况下findResources会返回父加载器和自己所找到的所有同名 资源，所以需要注意
	  */
	 public Enumeration<URL> findResources(String name) throws IOException {
				
		 if(!resources.containsKey(name)){
			URL url = findResource(name);
			if(url != null){
				Vector<URL> all = new Vector<URL>();
				all.add(url);
				resources.put(name, all);
				return all.elements();
			}
			else{
				return null;
			}
		}
		Vector<URL> v = resources.get(name);
		return  v== null?null:v.elements();
//		if(!resources.containsKey(name)){
////			 //过滤.class文件
////			 String reg = "^.*(?<!(\\.class|\\.java))$";
////			 List<JarEntry> list = FileUtil.scanJarInternal(getBasePath().getFile(),name,reg,0,false);
////			
////			Vector<URL> all = new Vector<URL>();
////			 for(JarEntry file : list){
////				 URL url = new URL("jar:" + getBasePath() + JAR_SEP + file.getName());
////				 all.add(url);
////			 }
//			 
//			 URL url = findResource(name);
//			 
//			Vector<URL> all = new Vector<URL>();
//			if(url != null){
//				 all.add(url);
//				 resources.put(name, all);
//			 }
//			return all.elements();
//		 }
//		 return resources.get(name).elements();
	 }

	private JarEntry getJarEntry(String name) throws IOException {
		 try{
			 return getJarFile().getJarEntry(name);
		 } catch (IllegalStateException e){
			 cachedJarFile = null;
			 return getJarFile().getJarEntry(name);
		 }
	}

	private JarFile getJarFile() throws IOException {
		 
		 if(cachedJarFile == null || cachedJarFile.getJarFile() == null){
			cachedJarFile = (JarURLConnection) getBasePath().openConnection();
			cachedJarFile.setUseCaches(true);
			cachedJarFile.getManifest();
		 }
		return cachedJarFile.getJarFile();
	}

}
