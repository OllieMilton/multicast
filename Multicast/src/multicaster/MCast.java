package multicaster;

import java.net.SocketTimeoutException;

import multicaster.ip.IPAddressMulticastServer;

public class MCast {


	public static void main(String[] args) throws InterruptedException {
		IPAddressMulticastServer ms = new IPAddressMulticastServer();
		ms.runServer();
		MulticastClient server = new MulticastClient();
		boolean stop = false;
		while (!stop) {
			try {
				System.out.println(server.send("HELLO!!", 2000));
				stop = true;
			} catch (SocketTimeoutException e) {

			}
		}
		Thread.sleep(3000);
		stop = false;
		while (!stop) {
			try {
				System.out.println(server.send("HELLO!!", 2000));
				stop = true;
			} catch (SocketTimeoutException e) {

			}
		}
		ms.terminated = true;
	}

	

}
