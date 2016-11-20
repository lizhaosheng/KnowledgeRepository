package com.netease.pillow.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 相关服务
 * @author lizhaosheng
 * @version 2014-11-6 下午1:35:48
 */
public class ClassLoaderUtils {
	
	private static final String MYBATIS_RESOURCE_CLASS_NAME = "org.apache.ibatis.io.Resources";
	
	private static final String MYBATIS_RESOURCE_SETDEFAULTCLASSLOADER_METHOD_NAME = "setDefaultClassLoader";
	
	/**
	 * 设置mybatis默认加载器,多个组件的情况下，最后被设置为最后加载组件的classLoader，不符合要求
	 * @param cl
	 * @throws ClassNotFoundException 
	 */
	@Deprecated
	public static void setDefaultClassLoaderForMybatis(ClassLoader cl) throws ClassNotFoundException{
		try {
			
			Class<?> c = cl.loadClass(MYBATIS_RESOURCE_CLASS_NAME);
			Method m = c.getMethod(MYBATIS_RESOURCE_SETDEFAULTCLASSLOADER_METHOD_NAME, new Class[] {ClassLoader.class});
			m.invoke(c, new Object[] {cl});
			
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
