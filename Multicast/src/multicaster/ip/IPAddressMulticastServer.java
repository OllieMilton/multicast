package multicaster.ip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import multicaster.MulticastServer;

public class IPAddressMulticastServer extends MulticastServer {

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
