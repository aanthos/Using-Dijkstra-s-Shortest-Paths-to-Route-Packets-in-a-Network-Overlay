package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import cs455.overlay.wireformats.EventFactory;

public class TCPReceiverThread extends Thread {
	
	private Socket socket;
	private DataInputStream din;
	private EventFactory eventFactory;
	
//	public TCPReceiverThread(Socket socket) throws IOException {
//		this.socket = socket;
//		din = new DataInputStream(socket.getInputStream());
//	}
	
	public TCPReceiverThread(Socket socket, EventFactory eventFactory) throws IOException {
		this.socket = socket;
//		
//		System.out.println("Messaging Node Receiver Thread socket local port: " 
//				+ socket.getLocalPort());
//				System.out.println("Messaging Node Receiver Thread socket remote bound port: " 
//						+ socket.getPort());
		
		
		din = new DataInputStream(socket.getInputStream());
		this.eventFactory = eventFactory;
	}
	
	@Override
	public void run() {
		
		int dataLength;
		while(socket != null) {
			try {
				dataLength = din.readInt();
				byte[] data = new byte[dataLength];
				din.readFully(data, 0, dataLength);
				
				eventFactory.getType(data);
				
				// debugging
//				System.out.println("Data Length" + dataLength);
//				System.out.println("Data Received" + data.toString());
			
			} catch (SocketException se) {
				System.out.println(se.getMessage());
				se.printStackTrace();
				break;
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
				ioe.printStackTrace();
				break;
			}
		}
	}
}
