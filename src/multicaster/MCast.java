package multicaster;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

public class MCast extends MulticastServer {


	public static void main(String[] args) {
		MCast ms = new MCast();
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
		ms.terminated = true;
	}

	@Override
	protected String getResponse(String received) {
		String ip = null;
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					InetAddress addr = address.nextElement();
					if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
							&& !(addr.getHostAddress().indexOf(":") > -1)) {
						ip = addr.getHostAddress();
					}
				}
			}
			if (ip == null) {
				ip = "ERROR";
			}

		} catch (SocketException e) {
			ip = "ERROR";
		}
		return ip;
	}

}
