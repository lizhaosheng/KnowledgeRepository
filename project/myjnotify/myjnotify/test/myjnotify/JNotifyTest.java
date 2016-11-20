package myjnotify;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.Util;

/**
 * 相关服务
 *
 * @author lizhaosheng
 * @version 2015年6月7日 下午12:43:01
 *
 */
public class JNotifyTest {

	/**
     * @param args
     */
    public static void main(String[] args) throws Exception{
    	// 监控目录   
        String rootDir = "D:\\";
        String path = rootDir;
        int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED
                | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
        boolean watchSubtree = true;
        System.out.println("begin watch");
        JNotify.addWatch(path, mask, watchSubtree, new MyJnotifyListener());
        while (true) { //否则一次就完了
            Thread.sleep(100000);
            System.out.println("continue watch");
        }
    }
}

