package cs455.overlay.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Register;

//check number of args in the constructor? or in Main?


public class MessagingNode implements Node{
	
	private ServerSocket serverSocket;
	private String nodeHostname;
	private int nodePort;
	private InetAddress registryHostName;
	private int registryPort;
	private Socket targetSocket;
	private Thread targetReceiverThread;
	private Socket registrySocket;
	private Thread registryReceiverThread;
	
	private EventFactory eventFactory;
	private Thread serverThread;
	private TCPServerThread server;
	
	private ArrayList<String> peerEdgeInfo;
	
	//constructor
//	public MessagingNode() throws Exception {
//		this.serverSocket = new ServerSocket(port);
//		serverSocket.getInetAddress();
//		// might need to add getLocalHost() in between if necessary
//		hostname = InetAddress.getLocalHost().getHostName();
//	}
	
	/*
	 * Initializes the messaging node
	 */
//	private void startUp(String args[]) throws Exception {
	public MessagingNode(String givenRegistryHost, String givenRegistryPortNum) throws Exception {
		
		// Grab hostname of this specific messaging node
		try {
			nodeHostname = InetAddress.getLocalHost().getHostName();
			//System.out.println("Current messaging node: " + nodeHostname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Grab the hostname of machine where Registry is running (start.sh)
		try {
			registryHostName = InetAddress.getByName(givenRegistryHost);
			//System.out.println("Registry Hostname: " + registryHostName.getHostName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Grab the port of machine where Registry is running (start.sh)
		try {
			registryPort = Integer.parseInt(givenRegistryPortNum);
			//System.out.println("Registry Port: " + registryPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// starts up singleton instance of event factory 
		eventFactory = EventFactory.getInstance();
		eventFactory.setCurrentNode(this);
		
		// Starts server thread to wait and listen for other nodes.
		// parameter for TCPServerThread is 0 to use anyS available port
		server = new TCPServerThread(0, eventFactory);
		serverThread = new Thread(server); 
		serverThread.start();
	}
	
	/*
	 * Opens connection to registry and sends register request message
	 */
	private void register(InetAddress registryHostName, int registryPort) throws Exception {
		// connects to the registry server
		try {
			registrySocket = new Socket(registryHostName, registryPort);
			
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
		// Open receiver thread on the registry socket 
		try {
			TCPReceiverThread registryReceiver = new TCPReceiverThread(registrySocket, eventFactory);
			registryReceiverThread = new Thread(registryReceiver);
			registryReceiverThread.start();
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
		// send register message request to registry
		try {
			TCPSender senderToRegistry = new TCPSender(registrySocket);
			nodePort = server.getLocalPort();
			//System.out.println("NodePort: " + nodePort);
			//System.out.println("TCPServer Thread Port for Messaging Node: " + nodePort);
			Register registerRequestMessage = new Register(Protocol.REGISTER_REQUEST, nodeHostname, nodePort);
			byte[] dataToSend = registerRequestMessage.getBytes();
			senderToRegistry.sendData(dataToSend);
		} catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	/*
	 * @param target node's host name and port
	 */
	public void initiateConnectionToOtherNode(InetAddress hostname, int port) {
		// open socket to other node
		try {
			targetSocket = new Socket(hostname, port);
		}
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
		
		// Open receiver thread on the target node
		try {
			TCPReceiverThread targetReceiver = new TCPReceiverThread(targetSocket, eventFactory);
			targetReceiverThread = new Thread(targetReceiver);
			targetReceiverThread.start();
		}
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	@Override
	public void onEvent(Event e) {
		// message type
		int type = e.getProtocolMessageType();
//		byte statusCode = e.getStatusCode();
//		String additionalInfo = e.getAdditionalInfo();
//		
//		System.out.println("Status Code: " + statusCode);
		
		if(type == Protocol.REGISTER_RESPONSE) {
			if(e.getStatusCode() == Protocol.SUCCESS) {
				System.out.println("Successfully registered on the Registry!");
			}
			else {
				System.out.println("Failed to register with the Registry");
			}
		}
		
		if(type == Protocol.MESSAGING_NODES_LIST) {
			ArrayList<String> peerNodeInfo = e.getPeerNodeInfo();
			int count = 0;
			for(String peer : peerNodeInfo) {
				String[] split = peer.split(":");
				try {
					InetAddress ipAddress = InetAddress.getByName(split[0]);
					int port = Integer.parseInt(split[1]);
					initiateConnectionToOtherNode(ipAddress, port);
					count++;
				}
				catch(IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
			System.out.println("All connections are established. Number of connections: " + count);
		}
		
		if(type == Protocol.LINK_WEIGHTS) {
			peerEdgeInfo = e.getPeerEdgeInfo();
			// for debugging
//			System.out.println("Printing peer edges");
//			for(String edge : peerEdgeInfo) {
//				System.out.println(edge);
//			}
			System.out.println("Link weights are received and processed. Ready to send messages.");
		}
		
		if(type == Protocol.DEREGISTER_RESPONSE) {
			if(e.getStatusCode() == Protocol.SUCCESS) {
				System.out.println("Successfully deregistered from the registry and left the overlay!");
			}
			else {
				System.out.println("Failed to deregister with the registry or not registered in registry");
			}
		}
	}
	
	private static void commandListener(MessagingNode node) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Messaging Node: Listening for commands");
			while(true) {
				String command = scanner.nextLine();
				
				if(command.equals("exit-overlay")) {
					node.exitOverlay();
				}
			}
		} catch (IllegalStateException e) {
			System.out.println("Terminal was closed. Command Listener exiting");
		}
	}
	
	public void exitOverlay() {
		try {
			registrySocket = new Socket(registryHostName, registryPort);
			TCPSender tcpSender = new TCPSender(registrySocket);
			Deregister deregisterRequest = 
					new Deregister(Protocol.DEREGISTER_REQUEST, nodeHostname, nodePort);
			byte[] dataToSend = deregisterRequest.getBytes();
			tcpSender.sendData(dataToSend);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		MessagingNode node = new MessagingNode(args[0], args[1]);
		//System.out.println(node.nodePort);
		// send register request message to registry
		node.register(node.registryHostName, node.registryPort);
		commandListener(node);
	}

}
