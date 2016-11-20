package myjnotify;

import net.contentobjects.jnotify.JNotifyListener;


public class MyJnotifyListener implements JNotifyListener {
    public void fileRenamed(int wd, String rootPath, String oldName,
            String newName) {
        System.out.println("文件：" + rootPath + " : " + oldName + " 重命名为： "
                + newName + "\n");
    }

    public void fileModified(int wd, String rootPath, String name) {
        System.out.println("文件修改 " + rootPath + " : " + name + "\n");
    }

    public void fileDeleted(int wd, String rootPath, String name) {
        System.out.println("删除文件： " + rootPath + " : " + name + "\n");
    }

    public void fileCreated(int wd, String rootPath, String name) {
        System.out.println("新建文件: " + rootPath + " : " + name + "\n");
    }
}
