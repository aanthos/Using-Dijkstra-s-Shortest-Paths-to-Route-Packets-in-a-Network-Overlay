package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.wireformats.EventFactory;
/**
 * Use https://introcs.cs.princeton.edu/java/84network/EchoServer.java.html as a reference
 */


//maybe implement Runnable
public class TCPServerThread implements Runnable {
	
	private Socket socket;
	private int remotePort;
	private ServerSocket serverSocket;
	private EventFactory eventFactory;
	private int localPort;
	//private DataOutputStream dout;
	
	public TCPServerThread(int remotePort, EventFactory eventFactory) throws IOException {
		this.remotePort = remotePort;
		this.eventFactory = eventFactory;
	}
	
//	public void setLocalPort(int localPort) {
//		this.localPort = localPort;
//	}
	
	public int getLocalPort() {
		// set here because it needs to be outside of the run function
		localPort = serverSocket.getLocalPort();
		return localPort;
	}

	@Override
	public void run() {
		// creates server socket 
		try {
			this.serverSocket = new ServerSocket(remotePort);
		
			// waits for connections repeatedly and processes
			while(true) {
				// blocking call: waits and listens for connection to be made on this socket and then accepts it
				try {
					this.socket = serverSocket.accept();
//					System.out.println("Messaging Node Server Thread socket local port: " 
//					+ socket.getLocalPort());
//					System.out.println("Messaging Node Server Thread socket remote bound port: " 
//							+ socket.getPort());
//			
					// Allows multiple receivers for one node, multi-threading
					//synchronized(this) {
						Thread newReceiverThread = new Thread(new TCPReceiverThread(socket, eventFactory));
						newReceiverThread.start();
					//}
					
				} catch (IOException ioe) {
					System.out.println("TCPServerThread - Server socket connection ended");
					break;
				}
			} 		
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}	
}
