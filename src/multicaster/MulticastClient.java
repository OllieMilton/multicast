package multicaster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class MulticastClient {

	private static final int BUFFER_SIZE = 256;
	private int port;
	private String multicastAddress;

	public MulticastClient() {
		multicastAddress = "239.0.0.1";
		port = 6669;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMulticastAddress(String multicastAddress) {
		this.multicastAddress = multicastAddress;
	}

	public String send(String message, int timeoutMillis) throws SocketTimeoutException {
		String result = null;
		if (message.length() > BUFFER_SIZE) {
			throw new IllegalArgumentException("Message length must not exceed ["+BUFFER_SIZE+"] characters");
		}
		MulticastSocket socket = null;
		InetAddress group = null;;
		try {
			byte[] buf = new byte[BUFFER_SIZE];
			socket = new MulticastSocket(port);
			group = InetAddress.getByName(multicastAddress);
			socket.joinGroup(group);
	        buf = message.getBytes();
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
	        socket.send(packet);
	        while (true) {
	        	buf = new byte[BUFFER_SIZE];
			    packet = new DatagramPacket(buf, buf.length);
			    socket.setSoTimeout(timeoutMillis);
			    socket.receive(packet);
			    String received = new String(packet.getData());
			    // ignore the message we have just sent
			    if (!received.trim().equals(message)) {
			    	result = received.trim();
			    	break;
			    }
	        }
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if (socket != null) {
					socket.leaveGroup(group);
				}
			} catch (IOException e) {}
			if (socket != null) {
				socket.close();
			}
		}
		return result;
	}
}
