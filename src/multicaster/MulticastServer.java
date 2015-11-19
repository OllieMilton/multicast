package multicaster;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class MulticastServer {

	private static final int BUFFER_SIZE = 256;
	private int port;
	private String multicastAddress;
	protected volatile boolean terminated;

	public MulticastServer() {
		multicastAddress = "239.0.0.1";
		port = 6669;
		terminated = false;
	}

	public void setPort(int port) {
		this.port = port;
	}

	protected void runServer() {
		new Thread(() -> {
				InetAddress group = null;
				try (MulticastSocket socket = new MulticastSocket(port)) {
					group = InetAddress.getByName(multicastAddress);
					socket.joinGroup(group);
					while (!terminated) {
						byte[] buf = new byte[BUFFER_SIZE];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						socket.receive(packet);
					    String received = new String(packet.getData()).trim();
					    if (received.startsWith(MCastConstants.CLIENT_OUT)) {
					    	received = received.substring(MCastConstants.PREFIX_LENGTH);
					    	String response = getResponse(received);
						    if (response != null) {
						    	response = MCastConstants.SERVER_OUT+response;
						    	buf = response.getBytes();
							    packet = new DatagramPacket(buf, buf.length, group, port);
							    socket.send(packet);
						    }
					    }
					}
					socket.leaveGroup(group);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			
		}).start();
	}


	protected abstract String getResponse(String received);

}
