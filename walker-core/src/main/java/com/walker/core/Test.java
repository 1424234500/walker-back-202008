package com.walker.core;

public class Test {

	public static void main(String[] args) throws InterruptedException {
		new Test();
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
