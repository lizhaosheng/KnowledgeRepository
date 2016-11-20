package com.netease.pillow.exception;
/**
 * 相关服务
 *
 * @author lizhaosheng
 * @version 2014年10月19日 下午5:22:50
 *
 */
public class PillowLoadException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PillowLoadException() {
		super();
	}

	public PillowLoadException(String msg) {
		super(msg);
	}

	public PillowLoadException(Exception e) {
		super(e);
	}

	public PillowLoadException(String msg, Exception e) {
		super(msg, e);
	}
}
