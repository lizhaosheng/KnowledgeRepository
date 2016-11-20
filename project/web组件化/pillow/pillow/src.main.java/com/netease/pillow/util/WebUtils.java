package com.netease.pillow.util;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

/**
 * 
 * @author lizhaosheng
 * @version 2014-11-7 下午4:33:52
 */
public class WebUtils {
	/**
	 * 低版本spring没有WebUtils.getNativeRequest 方法，在这里提供一个一模一样的
	 * Return an appropriate request object of the specified type, if available,
	 * unwrapping the given request as far as necessary.
	 * @param request the servlet request to introspect
	 * @param requiredType the desired type of request object
	 * @return the matching request object, or {@code null} if none
	 * of that type is available
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getNativeRequest(ServletRequest request, Class<T> requiredType) {
		if (requiredType != null) {
			if (requiredType.isInstance(request)) {
				return (T) request;
			}
			else if (request instanceof ServletRequestWrapper) {
				return getNativeRequest(((ServletRequestWrapper) request).getRequest(), requiredType);
			}
		}
		return null;
	}
}
