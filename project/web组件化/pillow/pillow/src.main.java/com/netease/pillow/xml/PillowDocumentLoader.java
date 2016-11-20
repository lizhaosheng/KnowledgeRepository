package com.netease.pillow.xml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.xml.DefaultDocumentLoader;

/**
 * 相关服务
 * @author lizhaosheng
 * @version 2014-10-23 下午5:19:45
 */
public class PillowDocumentLoader extends DefaultDocumentLoader{

	/**
	 * 设置忽略空白节点。
	 * 好像对回车换行节点不起作用
	 */
	protected DocumentBuilderFactory createDocumentBuilderFactory(int validationMode, boolean namespaceAware)
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = super.createDocumentBuilderFactory(validationMode, namespaceAware);
		factory.setIgnoringElementContentWhitespace(true);
		return factory;
	}
}
