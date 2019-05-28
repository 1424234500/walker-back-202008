package test;

import org.junit.Test;

import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.ClientUI;

public class ClientTest {

	@Test
	public void main() throws Exception {
//		new ClientUI(new ClientIO("127.0.0.1", 8090), "io-io");
//		new ClientUI(new ClientIO("127.0.0.1", 8091), "io-nio");
//		new ClientUI(new ClientNIO("127.0.0.1", 8090), "nio-io");
//		new ClientUI(new ClientNIO("127.0.0.1", 8091), "nio-nio-server");
//		new ClientUI(new ClientNIO("127.0.0.1", 8091), "nio-nio-client");
//		new ClientUI(new ClientNIO("127.0.0.1", 8092), "nio-netty-client");
		new ClientUI(new ClientNetty("127.0.0.1", 8092), "netty-netty-client");
		while(true) {}
	}

}
