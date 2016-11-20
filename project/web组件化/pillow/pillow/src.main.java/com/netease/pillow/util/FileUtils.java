package com.netease.pillow.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * 文件操作
 * @author lizhaosheng
 * @version 2014-11-3 下午7:12:45
 */
public class FileUtils {
	private static final Logger log = Logger.getLogger(FileUtils.class);

    /**
     * 查找目录basePath下的所有文件，递归查找，不包含目录
     * @param basePath
     * @return
     */
    public static List<File> findFiles(String basePath) {
    	List<File> fileList = findFiles(basePath,null,1,true);
		return fileList;
    }
    /**
     * 查找目录basePath下的所有以suffix为后缀的所有文件，递归查找，不包含目录
     * @param basePath
     * @param suffix
     * @return
     */
    public static List<File> findFilesWithSuffix(String basePath,String suffix) {
    	if(basePath == null || suffix == null){
    		return null;
    	}
    	String pattern = ".*\\." + suffix.substring(suffix.lastIndexOf(".") + 1);
    	List<File> fileList = findFiles(basePath,pattern,1,true);
		return fileList;
    }
    /**
     * 查找目录basePath下所有符合pattern表达式的文件，递归查找，不包含目录
     * @param basePath
     * @param pattern
     * @return
     */
    public static List<File> findFiles(String basePath,String pattern) {
    	List<File> fileList = findFiles(basePath,pattern,1,true);
		return fileList;
    }
    /**
     * 查找目录basePath下所有符合pattern表达式的文件
     * @param basePath - 搜索目录
     * @param pattern - 文件名表达式（全路径匹配）
     * @param mode - 文件类型，0只返回目录类型文件，1只返回文件类型文件，2包含目录及文件
     * @param isR - 是否递归调用
     * @return
     */
    public static List<File> findFiles(String basePath, String pattern, int mode, boolean isR) {
		if(basePath == null && (mode != 0 || mode != 1 || mode != 2)){
			return null;
		}
		File file = new File(basePath);
		if(!file.exists()){
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		if (file.isDirectory()) {
			loopFiles(fileList,file,pattern,mode,isR);
		}
		else{
			fileList.add(file);
		}
		return fileList;
	}
    
    /**
     * 对当前文件进行处理，判断是否能加入到结果集，是否进行递归搜索
     * @param fileList - 返回结果
     * @param file - 当前文件
     * @param pattern - 文件名表达式（全路径匹配）
     * @param mode - 文件类型，0只返回目录类型文件，1只返回文件类型文件，2包含目录及文件
     * @param isR - 是否递归调用
     * @return
     */
    private static final void loopFiles(List<File> fileList, File file, String pattern, int mode, boolean isR) {  
        
    	boolean flag = false;
        File[] tmps = file.listFiles();  
        for (File tmp : tmps) {
        	
        	flag = false;
        	
        	if((mode == 0 || mode ==2) && tmp.isDirectory()){
            	flag = true;
            }
        	else if((mode == 1 || mode ==2) && tmp.isFile()){
        		flag = true;
        	}
        
    		if(flag && pattern == null){
    			fileList.add(tmp);
        	}
        	else if(flag && tmp.getAbsolutePath().matches(pattern)){
        		fileList.add(tmp);
        	}
    		
    		if(isR && tmp.isDirectory()){
        		loopFiles(fileList,tmp,pattern,mode,isR);
        	}
        }
    }
    
    /**
     * 获取jar中所有符合表达式的文件
     * @param jarName
     * @param pattern - 文件名表达式（全路径匹配）
     * @param mode - 文件类型，0只返回目录类型文件，1只返回文件类型文件，2包含目录及文件
     * @return
     * @throws IOException
     */
    public static final List<JarEntry> scanJarInternal(String jarName, String basePath, String pattern, int mode, boolean isR) throws IOException{
    	
    	if(log.isDebugEnabled()){
			log.debug("=========>start to load jarName:" + jarName + "\tbasePath:" + basePath + "\tpattern:" + pattern + "\tmode:" + pattern + "\tisR:" + isR);
		}
    	
    	JarFile jarFile = new JarFile(jarName);
    	Enumeration<JarEntry> entrys = jarFile.entries();
    	
    	boolean basePathNotNull = (basePath != null);
    	boolean patternNotNull = (pattern != null);
    	boolean isFileOnly = (mode == 1);
    	boolean isDirOnly = (mode == 0);
    	int index = (basePath == null)?0:(
    			basePath.endsWith("/||\\")?basePath.length():basePath.length()+1);
    	
    	JarEntry tmp = null;
    	List<JarEntry> result = new ArrayList<JarEntry>();
    	while(entrys.hasMoreElements()){
    		tmp = entrys.nextElement();
    		if(basePathNotNull && !(tmp.getName().startsWith(basePath))){
    			continue;
    		}
    		if(patternNotNull && !(tmp.getName().matches(pattern))){
    			continue;
    		}
    		if(isFileOnly && tmp.isDirectory()){
    			continue;
    		}
    		if(isDirOnly && !tmp.isDirectory()){
    			continue;
    		}
    		if(!isR && tmp.getName().indexOf("/||\\",index) != -1){
    			continue;
    		}
    		if(log.isDebugEnabled()){
    			log.debug(tmp.getName());
    		}
    		result.add(tmp);
    	}
    	return result;
    }
}
