package test;

import org.junit.Test;

import com.walker.socket.server_1.SocketNetty;

public class ServerTest {
	@Test
	public void main() throws Exception {
		
//		Session<String> aaa = new Session<>("aaa", "127.0.0.1:10000");
//		Session<String> bbb = new Session<>("aaa", "127.0.0.1:10000");
//		Session<String> ccc = new Session<>("aaa", "127.0.0.1:10001");
//		Session<String> ddd = new Session<>("ddd", "127.0.0.1:10001");
//		
//		Tools.out(aaa, bbb, aaa.equals(bbb));
//		Tools.out(aaa, ccc, aaa.equals(ccc));
//		Tools.out(ccc, ddd, ccc.equals(ddd));
		
		new SocketNetty().start();
		
		
		
	}

}
