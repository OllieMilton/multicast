package multicaster;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class MulticastClient {

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
		if (message.length() > MCastConstants.BUFFER_SIZE-MCastConstants.PREFIX_LENGTH) {
			throw new IllegalArgumentException("Message length must not exceed ["+(MCastConstants.BUFFER_SIZE-MCastConstants.PREFIX_LENGTH)+"] characters");
		}
		InetAddress group = null;
		try (MulticastSocket socket = new MulticastSocket(port)) {
			byte[] buf = new byte[MCastConstants.BUFFER_SIZE];
			group = InetAddress.getByName(multicastAddress);
			socket.joinGroup(group);
			message = MCastConstants.CLIENT_OUT + message;
	        buf = message.getBytes();
	        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
	        socket.send(packet);
	        while (true) {
	        	buf = new byte[MCastConstants.BUFFER_SIZE];
			    packet = new DatagramPacket(buf, buf.length);
			    socket.setSoTimeout(timeoutMillis);
			    socket.receive(packet);
			    String received = new String(packet.getData());
			    // ignore the message we have just sent
			    received = received.trim();
			    if (!received.equals(message) && received.startsWith(MCastConstants.SERVER_OUT)) {
			    	result = received.substring(MCastConstants.PREFIX_LENGTH);
			    	break;
			    }
	        }
	        socket.leaveGroup(group);
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
