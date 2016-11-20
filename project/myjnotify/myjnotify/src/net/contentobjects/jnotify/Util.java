package net.contentobjects.jnotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
	/**
	 * 从jar中提取dll、so文件，拷贝到临时目录，以便使用system.load进行加载.
	 * synchronized保证线程安全
	 * 
	 * @param libName
	 *            - 在jar包中的类路径
	 * @return
	 * @throws IOException 
	 */
	public synchronized static File getTempLib(String libName, String suffix) throws IOException {
		try {
			InputStream in = Util.class.getClassLoader().getResource(libName).openStream();
			File lib = File.createTempFile("JNLIB_", suffix);
			FileOutputStream out = new FileOutputStream(lib);

			int i;
			byte[] buf = new byte[4096];
			while ((i = in.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
			in.close();
			out.close();
			// 虚拟机退出后删除
			lib.deleteOnExit();
			return lib;
		} catch (IOException e) {
			System.err.println("get file '" + libName + "' failed!\n" + e.getMessage());
			throw e;
		}
	}

	public static String getMaskDesc(int mask) {
		StringBuffer s = new StringBuffer();
		if ((mask & JNotify.FILE_CREATED) != 0) {
			s.append("FILE_CREATED|");
		}
		if ((mask & JNotify.FILE_DELETED) != 0) {
			s.append("FILE_DELETED|");
		}
		if ((mask & JNotify.FILE_MODIFIED) != 0) {
			s.append("FILE_MODIFIED|");
		}
		if ((mask & JNotify.FILE_RENAMED) != 0) {
			s.append("FILE_RENAMED|");
		}
		if (s.length() > 0) {
			return s.substring(0, s.length() - 1);
		} else {
			return "UNKNOWN";
		}
	}
}
