package com.walker.common.util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * win32工具窗口 鼠标
 * 
 * @author Walker
 *
 */
public class RobotUtil {
	public static volatile Robot robot = null;
	
	private RobotUtil() { }

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
	public static Robot getInstance(){           
	    return SingletonFactory.instance;           
	}  

	/**
	 * 按键按下
	 */
	public static void keyPress(int keyCode) {
		getInstance().keyPress(keyCode);
	}
	/**
	 * 按键松开
	 */
	public static void keyRelease(int keyCode) {
		getInstance().keyRelease(keyCode);
	}
	/**
	 * 获取鼠标点
	 */
	public static Point getMouse() {
		return MouseInfo.getPointerInfo().getLocation();
	}
	/**
	 * 设置鼠标点
	 */
	public static void setMouse(int x, int y) {
		getInstance().mouseMove(x, y);
	}
	/**
	 * 移动鼠标点
	 */
	public static void moveMouse(int dx, int dy) {
		Point p = getMouse();
		getInstance().mouseMove((int)p.getX() + dx, (int)p.getY() + dy);
	}
	/**
	 * 获取鼠标点颜色
	 */
	public static Color getColor() {
		Point p = getMouse();
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
	public static String getSysClipboardText() {
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
	public static void setSysClipboardText(String str) {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable tText = new StringSelection(str);
		clip.setContents(tText, null);
	}

	/**
	 * 从剪切板获得图片。
	 */
	public static Image getImageFromClipboard() throws Exception {
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
	public static Process doCmd(String command) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		return runtime.exec(command);
	}
	public static String getRuntime() {
		Runtime runtime = Runtime.getRuntime();
		String res = "Runtime: \n";
		res += " maxMemory: " + Tools.calcSize(runtime.maxMemory()) + " \n";
		res += " freeMemory: " + Tools.calcSize(runtime.freeMemory()) + " \n";
		res += " totalMemory: " + Tools.calcSize(runtime.totalMemory()) + " \n";
		return res;
	}
	
	/**
	 * 第一种方法:Jdk1.5的InetAddresss,代码简单。
	 * @param ipAddress
	 * @return
	 * @throws Exception
	 */
	public static boolean ping(String ipAddress) throws Exception {
	       int  timeOut =  3000 ;  //超时应该在3钞以上        
	       boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);     // 当返回值是true时，说明host是可用的，false则不可。
	       return status;
	   }
	/**
	 * 第二种方法:使用java调用cmd命令,这种方式最简单，可以把ping的过程显示在本地。
	 * @param ipAddress
	 * @throws Exception
	 */
	public static void ping02(String ipAddress) throws Exception {
	       String line = null;
	       try {
	           Process pro = Runtime.getRuntime().exec("ping " + ipAddress);
	           BufferedReader buf = new BufferedReader(new InputStreamReader(
	                   pro.getInputStream(),"GBK"));
	           while ((line = buf.readLine()) != null){
	            //line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
	            //line = new String(line.getBytes("UTF-8"),"GBK");
	            //line = new String(line.getBytes("ISO-8859-1"),"utf-8");
	            //line = new String(line.getBytes("ISO-8859-1"),"GBK");
	            //line = new String(line.getBytes("gb2312"),"GBK");
	            //line = new String(line.getBytes("gb2312"),"UTF-8");
	            //System.out.println(transcoding(line, "gbk") );
	            System.out.println(line);
	            
	           }
	            
	               
	       } catch (Exception ex) {
	           System.out.println(ex.getMessage());
	       }
	}
	 
	/**
	 * 第三种方法:也是使用java调用控制台的ping命令，这个比较可靠，还通用，使用起来方便：传入个ip，设置ping的次数和超时，就可以根据返回值来判断是否ping通。 
	 * @param ipAddress
	 * @param pingTimes
	 * @param timeOut
	 * @return
	 */
	public static boolean ping(String ipAddress, int pingTimes, int timeOut) {  
	         BufferedReader in = null;  
	         Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令  
	        String pingCommand = "ping " + ipAddress + " -n " + pingTimes    + " -w " + timeOut;  
	         try {   // 执行命令并获取输出  
	            System.out.println(pingCommand);   
	             Process p = r.exec(pingCommand);   
	             if (p == null) {    
	                 return false;   
	             }
	             in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数  
	            int connectedCount = 0;   
	             String line = null;   
	             while ((line = in.readLine()) != null) {    
	                 connectedCount += getCheckResult(line);   
	             }   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真  
	            return connectedCount == pingTimes;  
	         } catch (Exception ex) {   
	             ex.printStackTrace();   // 出现异常则返回假  
	            return false;  
	         } finally {   
	             try {    
	                 in.close();   
	             } catch (Exception e) {    
	                 e.printStackTrace();   
	             }  
	         }
	     }
	private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);  
	       Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)",    Pattern.CASE_INSENSITIVE);  
	        Matcher matcher = pattern.matcher(line);  
	        while (matcher.find()) {
	            return 1;
	        }
	        return 0; 
	    }
	 
	

}