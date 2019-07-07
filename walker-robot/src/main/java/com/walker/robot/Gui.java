package com.walker.robot;

import com.walker.common.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简易图形化终端
 *
 */
public class Gui extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	AtomicLong count = new AtomicLong(0L);
	ScheduledFuture<?> future;


	public JButton btStart  = new JButton("关闭");// 启动服务器
	ActionListener btStartAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (btStart.getText().equals("启动")) {
				btStart.setText("关闭");
				try {
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				btStart.setText("启动");
			}
		}
	};



	public JButton btSend = new JButton("发送");// 发送信息按钮
	ActionListener btSendAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			new Thread(){
				public void run(){
					String mmsg = ("" + jtfSend.getText());// 写入发送流到 客户端去

				}
			}.start();

		}
	};
	public JButton btLogin = new JButton("登录");// 发送信息按钮
	ActionListener btLoginAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			String key = jtfSend2.getText();	//发往用户连接
			out("等待启动");
			ThreadUtil.sleep(5000);

			int movet = 50;
			int dt = 50;
			out("move", Pc.mouseMove(800, 200));
			out("move", Pc.mouseClick(MouseEvent.BUTTON2,dt));
			ThreadUtil.sleep(5000);
			out( "input", Pc.keyClick(KeyEvent.VK_W, dt));
			out("move", Pc.mouseMove(800, 200));
			out("move", Pc.mouseClick(MouseEvent.BUTTON2,dt));
			ThreadUtil.sleep(5000);

			out( "input", Pc.keyClick(KeyEvent.VK_A, dt));


		}
	};
	public JButton jbshowusers = new JButton("测试");
	ActionListener jbshowusersAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int t = 50;
			int dt = 100;
			out("test", "now mouse", Pc.mouseGet());
			out("test", "move to ", Pc.mouseMove(-t, -t));
			out("test", "1 click", Pc.mouseClick(MouseEvent.BUTTON1, dt));
			out("test", "2 click", Pc.mouseClick(MouseEvent.BUTTON2, dt));
			out("test", "3 click", Pc.mouseClick(MouseEvent.BUTTON3, dt));
			out("test", "move to ", Pc.mouseMove(t, t));

			out("test", "input k", Pc.keyClick(KeyEvent.VK_K, dt));
			out("test", "input A", Pc.keyClick(KeyEvent.VK_A, dt));
			out("test", "input ALT", Pc.keyClick(KeyEvent.VK_ALT, dt));
			out("test", "input 0", Pc.keyClick(KeyEvent.VK_0, dt));
			out("test", "input CANCEL", Pc.keyClick(KeyEvent.VK_CANCEL, dt));

		}
	};
	public JButton jbshowrooms = new JButton("技能");
	ActionListener jbshowroomsAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

		}
	};
	public JButton jbtest = new JButton("auto on");
	ActionListener jbtestAction =new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(jbtest.getText().equals("auto on")) {
				jbtest.setText("auto off");
				count.set(0L);
				future = ThreadUtil.scheduleAtFixedRate(new Runnable() {
					public void run() {
						count.addAndGet(1L);

					}
				}, 1000, 10, TimeUnit.MILLISECONDS);
			}else {
				jbtest.setText("auto on");
				future.cancel(true);
			}
		}


	};
	public JCheckBox jcbscroll = new JCheckBox("锁定");


	public JTextField jtfSend;// 需要发送的文本信息
	public JTextField jtfSend1;// 需要发送的文本信息
	public JTextField jtfSend2;// 需要发送的文本信息
	public JTextArea taShow;// 信息展示

	static int clientCount = 0;
	static int clientNum = 4;
	public Gui(String name)   {
		super(name);
		init( name);
		clientCount ++;
	}
	public void init(String name)  {
		jtfSend = new JTextField(30);
		jtfSend1 = new JTextField(6);
		jtfSend2 = new JTextField(6);
		jtfSend1.setText("server_1");
		jtfSend2.setText(Tools.getRandomNum(10, 99, 2));
		jtfSend.setText("");

		taShow = new JTextArea();
		taShow.setLineWrap(true);//设置文本域自动换行
//		taShow.setRows(3);//设置文本域行数
//		taShow.setColumns(5);//设置文本域的列数
		taShow.setEditable(true);
		taShow.addMouseListener(new MouseAdapter() {
		});
		taShow.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				out("mouseClicked", e.getPoint());
			}

			@Override
			public void mousePressed(MouseEvent e) {
				out("mousePressed", e.getPoint());

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				out("mouseReleased", e.getPoint());

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}
		});
		taShow.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) { // 按键被按下时被触发
				String keyText = KeyEvent.getKeyText(e.getKeyCode());// 获得描述keyCode的标签
				log("keyPressed", e.getKeyCode(), keyText);
				if (e.isActionKey()) { // 判断按下的是否为动作键
				} else {
					int keyCode = e.getKeyCode();// 获得与此事件中的键相关联的字符
					switch (keyCode) {
						case KeyEvent.VK_CONTROL: // 判断按下的是否为Ctrl键
						case KeyEvent.VK_ALT: // 判断按下的是否为Alt键
						case KeyEvent.VK_SHIFT: // 判断按下的是否为Shift键
							out("action", e.getKeyCode(), keyText);
					}
				}
			}

			public void keyTyped(KeyEvent e) { // 发生击键事件时被触发
				out("keyTyped", e.getKeyCode(), e.getKeyChar());
			}

			public void keyReleased(KeyEvent e) { // 按键被释放时被触发
				String keyText = KeyEvent.getKeyText(e.getKeyCode());// 获得描述keyCode的标签
				log("keyReleased", e.getKeyCode(), keyText);
			}
		});
		btStart.addActionListener(btStartAction);
		btSend.addActionListener(btSendAction);
		btLogin.addActionListener(btLoginAction);
		jbshowrooms.addActionListener(jbshowroomsAction);
		jbtest.addActionListener(jbtestAction);
		jbshowusers.addActionListener(jbshowusersAction);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

			}
		});
		JPanel top = new JPanel(new FlowLayout());
		top.add(jtfSend);
		top.add(btSend);

		top.add(jtfSend1);
		top.add(jtfSend2);
		top.add(btLogin);

		top.add(btStart);
		top.add(jbshowrooms);
		top.add(jbtest);
		top.add(jbshowusers);

		jcbscroll.setSelected(true);
		top.add(jcbscroll);
		this.add(top, BorderLayout.SOUTH);
		final JScrollPane sp = new JScrollPane();
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setViewportView(this.taShow);
		this.add(sp, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Tuple<Integer, Integer> wh = Pc.getScreenWidthHeight();

		this.setBounds(10, (int) (120 + clientCount * wh.v2 * 0.6 / clientNum),
				wh.v1 * 5 / 10, wh.v2 * 4/10);
		this.setVisible(true);
	}

	public void log(Object...objects){
		Tools.out(objects);
	}

	public String out(Object...objects) {
		String s = TimeUtil.getTimeSequence() + "." + Tools.out(objects);
		if (s != null) {// 输出当服务端的界面上去显示
			if (s.length() > 60000)
				s = Tools.tooLongCut(s); // 太长的数据 
			if (this.taShow.getText().length() >= 200000) {
				this.taShow.setText("");
			}
			this.taShow.append( s + "\n");
			
			if(this.jcbscroll.isSelected())
				this.taShow.setCaretPosition(this.taShow.getText().length()); // 锁定最底滚动

		}
		return s;
	}

}