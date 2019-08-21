package com.walker.common.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * win32工具窗口 鼠标
 * 
 * @author Walker
 *
 */
public class Pc {
	public static volatile Robot robot = null;
	
	private Pc() { }

	private static class SingletonFactory{           
		  private static Robot instance;
		  static {
			 try {
				instance = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		  }
	}

	public static Robot getInstance() {
	    return SingletonFactory.instance;           
	}



	/**
	 * 按键按下 KeyEvent.VK_A
	 * @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static void keyPress(int keycode) {
		getInstance().keyPress(keycode);
	}
	/**
	 * 按键松开 KeyEvent.VK_A
	* @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static void keyRelease(int keycode) {
		getInstance().keyRelease(keycode);
	}

	/**
	 * 按键按下松开 延时 KeyEvent.VK_A
	 * @param   keycode Key to release (e.g. <code>KeyEvent.VK_A</code>)
	 */
	public static int keyClick(int keycode, long dtime) {
		keyPress(keycode);
		if(dtime > 0){
			try {
				Thread.sleep(dtime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		keyRelease(keycode);
		return keycode;
	}

	/**
	 * 鼠标按键按下 MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static void mousePress(int button123) {
		int button = InputEvent.getMaskForButton(button123);
		getInstance().mousePress(button);
	}
	/**
	 * 鼠标按键松开 MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static void mouseRelease(int button123) {
		int button = InputEvent.getMaskForButton(button123);
		getInstance().mouseRelease(button);
	}

	/**
	 * 按下 松开  MouseEvent.BUTTON1 MouseEvent.BUTTON2 MouseEvent.BUTTON3
	 */
	public static Point mouseClick(int button123, long dtime){
		mousePress(button123);
		if(dtime > 0){
			try {
				Thread.sleep(dtime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mouseRelease(button123);
		return mouseGet();
    }



    public static Tuple<Integer,Integer> getScreenWidthHeight() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int) screenSize.getWidth();
        int h = (int) screenSize.getHeight();
        return new Tuple<Integer,Integer>(w, h);
    }
	/**
	 * 获取鼠标点
	 */
	public static Point mouseGet() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	/**
	 * 设置鼠标点
	 */
	public static Point mouseSet(int x, int y) {
		getInstance().mouseMove(x, y);
		return mouseGet();
	}
	/**
	 * 移动鼠标点
	 */
	public static Point mouseMove(int dx, int dy) {
		Point p = mouseGet();
		getInstance().mouseMove((int)p.getX() + dx, (int)p.getY() + dy);
		return mouseGet();
	}
	/**
	 * 获取鼠标点颜色
	 */
	public static Color getColor() {
		Point p = mouseGet();
		return getColor(p.x, p.y);
	}
	/**
	 * 获取指定点颜色
	 */
	public static Color getColor(int x, int y) {
		return getInstance().getPixelColor(x, y);
	}

	/**
	 * 从剪切板获得文字。
	 */
	public static String getClipboardText() {
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板中的内容
		Transferable clipTf = sysClip.getContents(null);

		if (clipTf != null) {
			// 检查内容是否是文本类型
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	/**
	 * 将字符串复制到剪切板。
	 */
	public static void setClipboardText(String str) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(str);
		clip.setContents(tText, null);
	}

	/**
	 * 从剪切板获得图片。
	 */
	public static Image getClipboardImage() throws Exception {
		Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable cc = sysc.getContents(null);
		if (cc == null)
			return null;
		else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
			return (Image) cc.getTransferData(DataFlavor.imageFlavor);
		return null;
	}

	/**
	 * 复制图片到剪切板。
	 */
	public static void setClipboardImage(final Image image) {
		Transferable trans = new Transferable() {
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}

			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (isDataFlavorSupported(flavor))
					return image;
				throw new UnsupportedFlavorException(flavor);
			}

		};
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
	}

	/**
	 * 执行cmd命令
	 *
	 * @param command
	 * @return
	 * @throws IOException
	 */
	public static Process doCmd(String command) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(command);
	}

	/**
	 * 获取执行环境信息
	 * @return
	 */
	public static String getRuntime() {
		Runtime runtime = Runtime.getRuntime();
		String res = "Runtime: \n";
		res += " maxMemory: " + Tools.calcSize(runtime.maxMemory()) + " \n";
		res += " freeMemory: " + Tools.calcSize(runtime.freeMemory()) + " \n";
		res += " totalMemory: " + Tools.calcSize(runtime.totalMemory()) + " \n";
		return res;
	}
	
	/**
	 * 超时应该在3钞以上
	 */
	public static boolean ping(String ipAddress, int timeout)  {
		boolean res = false;
		try {
			res = InetAddress.getByName(ipAddress).isReachable(timeout);
		}catch(Exception e){
			res = false;
		}
		return res;
    }


	/**
	 * 模拟telnet实现socket端口检测
	 */
	public static long telnet(String ip, int port, int timeout){
		long start = System.currentTimeMillis();
		long deta = -1;
		try{
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ip, port), timeout);
			if(socket != null){
				deta = System.currentTimeMillis() - start;
				socket.close();
			}
		}catch(Exception e){

		}

		return deta;
	}




}