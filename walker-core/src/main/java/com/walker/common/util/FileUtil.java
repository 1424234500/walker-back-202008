package com.walker.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class FileUtil {

	/**
	 * 文件集合排序
	 * 
	 * @param tempList
	 *            源files
	 * @param order
	 *            排序参数time name
	 * @param orderRe
	 *            是否倒序
	 * @return
	 */
	public File[] orderBy(File[] tempList, String order, boolean orderRe) {
		if (order == null)
			order = "name";
		if (order.equals("no"))
			return tempList;
		if (order.equals("null"))
			return tempList;

		final String or = order;
		final boolean orRe = orderRe;

		List<File> sortfiles = Arrays.asList(tempList);
		Collections.sort(sortfiles, new Comparator<File>() {
			public int compare(File o1, File o2) {
				int res = 0;
				if (or.equals("time")) {
					String str1 = String.valueOf(o1.lastModified());
					String str2 = String.valueOf(o2.lastModified());
					res = str1.compareTo(str2);
				} else if (or.equals("name")) {
					res = o1.getName().compareTo(o2.getName());
				}

				if (orRe) {
					res = 0 - res;
				}

				return res;
			}
		});
		for (int i = 0; i < sortfiles.size(); i++) {
			tempList[i] = sortfiles.get(i);
		}

		return tempList;
	}

	/**
	 * 创建文件
	 */
	public static boolean mkfile(String path) {
		if (path == null)
			return false;
		File file = new File(path);
		if (file.exists() || file.isFile()) {
			return true;
		} else {
			try {
				mkdir(file.getParent());
				out("新建文件" + path);
				return file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public static boolean mkdir(String dir) {
		if (dir == null)
			return false;
		File file = new File(dir);
		if (file.exists()) {
			return true;
		}
		out("新建文件夹" + dir);
		return file.mkdirs();
	}

	/**
	 * 获取或操作 所有文件夹和文件 dir可,分割多个dir
	 * 
	 * @param dir  aaa,bbb,ccc
	 * @param funFileOrDir
	 */
	public static List<File> showDir(String dire, Fun<File> funFileOrDir) {
		List<File> files = new ArrayList<File>();
		String[] dirs = dire.split(",");
		for (String dir : dirs) {
			showDir(new File(dir), files, funFileOrDir);// 把遍历得到的东西存放在files里面
		}
		return files;
	} 

	/**
	 * 递归遍历目录 深度优先 子处理 再父处理
	 * 
	 * @param file 当前处理文件
	 * @param files 结果集
	 * @param funFileOrDir 回调函数处理
	 */
	private static void showDir(File file, final List<File> files, final Fun<File> funFileOrDir) {
		if (file == null || !file.exists())
			return;
		files.add(file); // 添加当前 节点
		if (file.isDirectory()) { // 若是文件夹 则递归子节点 深度优先
			File[] fillArr = file.listFiles();
			if (fillArr == null)
				return;
			for (File file2 : fillArr) {
				showDir(file2, files, funFileOrDir);// 把遍历得到的东西存放在files里面
			}
		}
		if (funFileOrDir != null) { // 处理当前节点
			funFileOrDir.make(file);
		}
	}

	/**
	 * 以字节为单位读取文件，通常用于读取二进制文件，如图片
	 * 若不分批回调处理 则积压结果集返回
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] readByBytes(String path) {
		ByteArrayOutputStream res = new ByteArrayOutputStream() ;
		InputStream inputStream = null;
		try {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			int size = 0;
			byte[] bytes = new byte[4096];
			while ((size = inputStream.read(bytes)) != -1) {
				res.write(bytes, 0, size);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res.toByteArray();
	}
	/**
	 * 以字符为单位读取文件，常用于读取文本文件
	 * 
	 * @param path
	 * @param encode utf-8 null default
	 * @return
	 */
	public static String readByChars(String path, String encode) {
		encode = makeEncode(encode);
		StringBuffer res = new StringBuffer();
		Reader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(path), encode);
			char[] tempchars = new char[1024];
			while (reader.read(tempchars) != -1) {
				res.append(tempchars);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return res.toString();
	}
	/**
	 * 默认值编码配置
	 * @param encode utf-8 null default
	 * @return utf-8/encode
	 */
	public static String makeEncode(String encode) {
		return encode == null || encode.length() == 0 ? "utf-8" : encode;
	}

	/**
	 * 以行为单位读取文件，常用于读取面向行的格式化文件 apache.common.io
	 * 
	 * @param path
	 * @return 返回行数
	 * @throws IOException 
	 */
	public static int readByLines(File file, Fun<String> fun, String encode) {
		encode = makeEncode(encode);

		int lines = 0;
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file, encode);
			while(it.hasNext()) {
				String line = it.nextLine();
				lines++;
				fun.make(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(it != null)
				it.close();
		}

		return lines;
	}

	/**
	 * 按行读取	java.io
	 * 若回调null 则返回所有字符串 大文件慎用
	 */
	public static String readByLines(String path, Fun<String> fun, String encode) {
		encode = makeEncode(encode);

		String content = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), encode));

			StringBuffer sb = new StringBuffer();
			String temp = null;
			if (fun != null) {
				while ((temp = bufferedReader.readLine()) != null) {
					fun.make(temp);
				}
			}
			else {
				while ((temp = bufferedReader.readLine()) != null) {
					sb.append(temp).append("\r\n");
				}
			}
			content = sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return content;
	}

	

	/**
	 * 把内容content写的path文件中
	 * 
	 * @param content
	 * @param path
	 * @param append 是否追加
	 * 
	 * @return 是否保存成功
	 */
	public static boolean saveAs(String content, String path, Boolean append) {
		FileWriter fw = null;
		// out("把内容：" + content + "， 写入文件：" + path);
		try {
			/**
			 * Constructs a FileWriter object given a File object. If the second
			 * argument is true, then bytes will be written to the end of the
			 * file rather than the beginning. 根据给定的File对象构造一个FileWriter对象。
			 * 如果append参数为true, 则字节将被写入到文件的末尾（向文件中追加内容）
			 *
			 * Parameters: file, a File object to write to 带写入的文件对象 append, if
			 * true, then bytes will be written to the end of the file rather
			 * than the beginning Throws: IOException - if the file exists but
			 * is a directory rather than a regular file, does not exist but
			 * cannot be created, or cannot be opened for any other reason
			 * 报异常的3种情况： file对象是一个存在的目录（不是一个常规文件） file对象是一个不存在的常规文件，但不能被创建
			 * file对象是一个存在的常规文件，但不能被打开
			 *
			 */
			fw = new FileWriter(new File(path), append);
			if (content != null) {
				fw.write(content);
				fw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	public static void testWriteFile() throws Exception {
		//字符流写入字符到文件 char[] string
		FileWriter fw = new FileWriter(new File("aaa"), false);
		fw.write("aaaaaaaaa");
		fw.flush();
		
		//字符流类来处理字符数据 char[] string
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("bbbbb");
		bw.write(new char[]{'a','b'});
		bw.close();
		
		//原始二进制数据的字节流类 byte
		FileOutputStream op = new FileOutputStream(new File("bbb"));
		op.write("asdf".getBytes());
		op.close();
		 try (FileOutputStream fop = new FileOutputStream(new File("ccc"))) {
			 
		 }catch(Exception e) {
			 
		 }
	}
	
	/**
	 * 全路径 明确路径 文件夹复制或者移动 使用org.apache.commons.io.FileUtils实现
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	private static void cpDir(String oldPath, String newPath, boolean ifMove) {
		if (oldPath.equals(newPath))
			return;
		out((ifMove ? "移动" : "复制") + "文件夹" + oldPath + "->" + newPath);
		try {
			if (ifMove) {
				FileUtils.moveDirectory(new File(oldPath), new File(newPath));
			} else {
				FileUtils.copyDirectory(new File(oldPath), new File(newPath));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 定点文件复制或者移动 使用org.apache.commons.io.FileUtils实现
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	private static void cpFile(String oldPath, String newPath, boolean ifMove) {
		if (oldPath.equals(newPath))
			return;
		out((ifMove ? "移动" : "复制") + "文件" + oldPath + "->" + newPath);
		try {
			if (ifMove) {
				FileUtils.moveFile(new File(oldPath), new File(newPath));
			} else {
				FileUtils.copyFile(new File(oldPath), new File(newPath));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 只支持同路径下的重命名? File.rename
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean rename(String oldPath, String newPath) {
		if (oldPath.equals(newPath)) {
			out("源路径文件" + oldPath + "和目标文件路径相同");
			return false;
		}

		if (!new File(oldPath).exists()) {
			out("文件" + oldPath + " 不存在");
			return false;
		}
		boolean res = new File(oldPath).renameTo(new File(newPath));
		out("移动文件?" + oldPath + "->" + newPath + " 结果=" + res);
		return res;
	}

	/**
	 * 移动或复制文件 适配 类似linux shell命令
	 * 
	 * @param oldPath
	 * @param newPath
	 * @param ifMove
	 */
	private static void cpIfMove(String oldPath, String newPath, boolean ifMove) {
		try {
			int fromType = check(oldPath); // 0 文件 1文件夹 -1不存在
			if (fromType == -1) {
				out("文件操作失败", oldPath, newPath);
			} else if (fromType == 0) {// 文件 cp file1.txt dir1/ cp file1.txt
										// file1
				if (newPath.endsWith(File.separator)) { // 原名字 操作到 新目录下面
					newPath = newPath + File.separator + getFileName(oldPath);
				}
				cpFile(oldPath, newPath, ifMove);
			} else {// 文件夹 操作
				if (newPath.endsWith(File.separator)) { // 原名字 操作到 新目录下面
					newPath = newPath + File.separator + getFileName(oldPath);
				}
				cpDir(oldPath, newPath, ifMove);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 复制 文件夹 或 文件 cp c:/dir1 c:/dir2 cp c:/file1 c:/dir2/ cp c:/file1 c:/file2
	 * 
	 * @param oldPath
	 *            原文件路径 如：c:/fqf
	 * @param newPathth
	 *            复制后路径 如：f:/fqf/ff
	 */
	public static void cp(String oldPath, String newPath) {
		cpIfMove(oldPath, newPath, false);
	}

	/**
	 * 移动文件 或文件夹
	 * 
	 * @param oldPath
	 * @param newPath
	 */
	public static void mv(String oldPath, String newPath) {
		cpIfMove(oldPath, newPath, true);
	}

	/**
	 * 扫描文件夹下ext类型的文件信息，获取路径，名字，大小
	 * 
	 * @param dir
	 * @param ext
	 * @return
	 */
	public static List<Bean> getDirFiles(String dir, String ext) {
		List<Bean> res = new ArrayList<Bean>();

		// 扫描文件夹 读取数据
		out("扫描文件目录" + dir);
		File rootfile = null;
		try {
			rootfile = new File(dir);
		} catch (Exception e) {
			out("打开文件目录[" + dir + "] error ");
		}
		File coders[] = rootfile.listFiles(); // 用户各自文件夹名集合
		if (coders == null) {
			out("文件夹为null");
		} else {
			for (File coder : coders) {
				if (coder.isFile()) {
					// 后缀判定xxx.apk, about.txt, xxx.img
					String fileName = coder.getName();
					if (getFileType(fileName).equals(ext)) {
						res.add(fileToMap(coder));
					}
				}

			}
		}

		return res;
	}

	/**
	 * 获取某路径下所有文件 文件夹 ll
	 */
	public static List<Bean> ls(String dir) {
		List<Bean> res = new ArrayList<Bean>();

		out("扫描文件目录:" + dir);
		File rootfile = new File(dir);
		File coders[] = rootfile.listFiles();
		int countFile = 0;
		for (File coder : coders) {
			if (coder.isDirectory()) {
				res.add(fileToMap(coder));
			}
		}
		for (File coder : coders) {
			if (coder.isFile()) {
				countFile++;
				res.add(fileToMap(coder));
			}
		}
		out("文件夹数量:" + (coders.length - countFile) + " \t文件数量:" + countFile);
		return res;
	}

	/**
	 * 获取某文件详情
	 */
	public static Bean getFileMap(String file) {
		if (file != null && file.length() > 0) {
			File rootfile = new File(file);
			if (rootfile.exists()) {
				return fileToMap(rootfile);
			}
		}
		return new Bean();
	}

	/**
	 * 获取文件 map样式键集合
	 */
	public static List<?> getFileMap() {
		List<String> res = new ArrayList<String>();
		res.add("PATH");
		res.add("NAME");
		res.add("SIZE");
		res.add("LENGTH");
		res.add("TIME");
		res.add("TYPE");
		res.add("CHILDS");
		return res;
	}

	public static Bean fileToMap(File coder) {
		Bean map = new Bean();
		map.put("PATH", coder.getAbsolutePath());
		map.put("NAME", coder.getName());
		map.put("SIZE", calcSize(coder.length()));
		map.put("LENGTH", coder.length());
		map.put("TIME", coder.lastModified());

		String type = "";
		int dirfiles = 0;
		if (coder.isFile()) {
			type = getFileType(coder.getAbsolutePath());
		} else if (coder.isDirectory()) {
			type = "dir";
			File[] f = coder.listFiles();
			if (f != null)
				dirfiles = f.length;
		}
		map.put("TYPE", type);
		map.put("CHILDS", dirfiles);

		return map;
	}

	/**
	 *  通过字符串长度，计算大概的 流量大小 MB KB B char=B
	 *  
	 * @param length
	 * @return
	 */
	static String calcSize(long length) {
		long m = length / (1024 * 1024);
		long k = length % (1024 * 1024) / 1024;
		long b = length % (1024 * 1024) % 1024;
		return m > 0 ? m + "." + k / 100 + "MB" : k > 0 ? k + "." + b / 100 + "KB" : b + "B";
	}
	/**
	 *  通过字符串长度，计算大概的 流量大小 MB KB B char=B
	 *  
	 * @param length
	 * @return
	 */
	static String calcSize(int length) {
		return calcSize(1L * length);
	}

	/**
	 * 检查文件类型
	 * 
	 * @param path
	 * @return 0 文件 1文件夹 -1不存在
	 */
	public static int check(String path) {
		int res = -1;
		File file = new File(path);
		if (file.exists()) {
			if (file.isFile()) {
				res = 0;
			} else if (file.isDirectory()) {
				res = 1;
			} else {
				out("文件：" + path + "不是文件夹也不是文件");
			}
		} else {
			out("文件：" + path + "不存在");
		}
		return res;
	}

	/**
	 * 删除指定路径下指定后缀文件 Constant.fileupload路径下所有filename后缀文件
	 * @param dir	E:/down
	 * @param filename	xxx.doc
	 * 
	 * @return
	 */
	public static boolean delete(String dir, String filename) {
		final String name = getFileName(filename);
		
		showDir(dir, new Fun<File>() {
			@Override
			public <T> T make(File file) {
				if(file.isFile() && getFileType(file.getName()).equals(name)){
					file.delete();
				}
				return null;
			}
		});
		
		return false;
	}

	/**
	 * 递归删除 文件 或者 文件夹 所有
	 */
	public static boolean delete(String path) {
		showDir(path, new Fun<File>() {
			@Override
			public <T> T make(File file) {
				file.delete();
				return null;
			}
		});

		return false;
	}


	/**
	 * 获取文件后缀名 ext
	 * 
	 * @param path /sdcard/mycc/record/test.amr
	 * @return	amr
	 */
	public static String getFileType(String path) {
		String res = "null";
		if (path == null) {
		} else {
			int ii = path.lastIndexOf(".");
			// out(""+ii);
			if (ii >= 0) {
				return path.substring(ii + 1);
			}
		}
		return res;
	}


	/**
	 * 获取文件名 不要后缀
	 * 
	 * @param path /sdcard/mycc/record/test.amr
	 * @return	test
	 */
	public static String getFileNameOnly(String path) {
		String res = "null";

		if (path == null) {
		} else {
			int ii = path.lastIndexOf(".");
			// out(""+ii);
			if (ii >= 0) {
				res = path.substring(0, ii); // F:/s/d/1000
				ii = path.lastIndexOf("\\");
				// out(""+ii);
				if (ii >= 0) {
					res = res.substring(ii + 1);
				}
			}
		}
		return res;
	}

	/**
	 * 获取文件名 
	 * 
	 * @param path /sdcard/mycc/record/test.amr
	 * @return	test.amr
	 */
	public static String getFileName(String path) {
		String res = "null";

		if (path == null) {
		} else {
			int ii = path.lastIndexOf(File.separator);
			// out(""+ii);
			if (ii >= 0) {
				res = path.substring(ii + 1);
			}
		}
		return res;
	}

	/**
	 * 获取文件父路径
	 * 
	 * @param path /sdcard/mycc/record/test.amr
	 * @return	/sdcard/mycc/record
	 */
	public static String getFilePath(String path) {
		String res = "";

		if (path == null) {
		} else {
			int ii = path.lastIndexOf(File.separator);
			// out(""+ii);
			if (ii >= 0) {
				res = path.substring(0, ii);
			}
		}
		return res;

	}

	public static void out(Object... objects) {
		Tools.out("FileUtil", objects);
	}

}