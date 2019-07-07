package com.walker.common.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.InputEvent;
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