package com.walker.core;

import com.walker.common.util.Tools;

import java.nio.ByteBuffer;

public class Test {

	public static void main(String[] args) throws Exception {
		testgc();

//		new Test();
	}
	public static void testgc()throws Exception{
		int d = 1024 * 1024 * 1;
		long all = 0;

		while(true) {
			all ++;
			Tools.out(all, "M");
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(d);
			Thread.sleep(10);
		}
	}

	public Test() throws InterruptedException {
		for(int i = 0; i < 4; i++) {
			new Thread() {
				public void run() {
					int j = 0;
					while (true) {
						System.out.println(j++);
					}

				}
			}.start();
		}
		Thread.sleep(999999999999L);

	}


}
