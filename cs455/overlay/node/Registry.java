package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.Vertex;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Deregister;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.EventFactory;
import cs455.overlay.wireformats.LinkWeights;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.Protocol;
import cs455.overlay.wireformats.Register;

public class Registry implements Node{
	
	private String registryHostname;
	private int registryPort;
	private Thread serverThread;
	private EventFactory eventFactory;
	private ArrayList<Vertex> activeNodeList = new ArrayList<Vertex>();
	private Graph overlay;
	
	
	public Registry(String portNum) {
		// Grab the port of machine where Registry is running (start.sh)
		try {
			registryPort = Integer.parseInt(portNum);
			//System.out.println("Registry Port: " + registryPort);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Grab the hostname of machine where Registry is running (start.sh)
		try {
			registryHostname = InetAddress.getLocalHost().getHostName();
			//System.out.println("Registry Hostname: " + registryHostname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// starts up singleton instance of event factory 
		eventFactory = EventFactory.getInstance();
		eventFactory.setCurrentNode(this);
		
		try {
			serverThread = new Thread(new TCPServerThread(registryPort, eventFactory));
			serverThread.start();
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
	
	@Override
	public void onEvent(Event e) {
		// message type
		int type = e.getProtocolMessageType();
		long timestamp = e.getTimestamp();
		String identifier = e.getIdentifier();
		InetAddress identifierAddress = null;

		try {
			identifierAddress = InetAddress.getByName(identifier);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

		int tracker = e.getTracker();
		Vertex requestingNodeIPPort = new Vertex(identifier, tracker);

		// send messages like in MessagingNode using TCP sender
		if(type == Protocol.REGISTER_REQUEST ) {
			ArrayList<String> copy = new ArrayList<String>();
			String nodeInfo = identifier + ":" + tracker;
			int found = -1;
			for(int i = 0; i < activeNodeList.size(); i++) {
				String activeNodeString = activeNodeList.get(i).getIdentifier() + ":" + activeNodeList.get(i).getTracker();
				if(nodeInfo.equals(activeNodeString)) {
					found = i;
				}
			}
			if(found != -1){
				try {
					Socket messagingNodeSocket = new Socket(identifierAddress, tracker);
					TCPSender tcpSender = new TCPSender(messagingNodeSocket);

					Register registerResponseMessage = 
							new Register(Protocol.REGISTER_RESPONSE, Protocol.FAILURE, registryHostname, registryPort);
					byte[] dataToSend = registerResponseMessage.getBytes();
					tcpSender.sendData(dataToSend);

				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}

				System.out.println("Registration request failed. Messaging node "
						+ identifier + ":" + tracker + " is already registered.");
			}

			else {
				try {
					Socket messagingNodeSocket = new Socket(identifierAddress, tracker);
					TCPSender tcpSender = new TCPSender(messagingNodeSocket);

					Register registerResponseMessage = 
							new Register(Protocol.REGISTER_RESPONSE, Protocol.SUCCESS, registryHostname, registryPort);
					byte[] dataToSend = registerResponseMessage.getBytes();
					tcpSender.sendData(dataToSend);

				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
				
				activeNodeList.add(requestingNodeIPPort);
				System.out.println("Registration request successful. "
						+ "The number of messaging nodes currently constituting the overlay is (" 
						+ activeNodeList.size() + ")");
			}

		}
		
		if(type == Protocol.DEREGISTER_REQUEST) {
			ArrayList<String> copy = new ArrayList<String>();
			String nodeInfo = identifier + ":" + tracker;
			int found = -1;
			for(int i = 0; i < activeNodeList.size(); i++) {
				String activeNodeString = activeNodeList.get(i).getIdentifier() + ":" + activeNodeList.get(i).getTracker();
				if(nodeInfo.equals(activeNodeString)) {
					found = i;
				}
			}
			//if(activeNodeList.contains(requestingNodeIPPort)) {
			if(found != -1) {
				try {
					Socket messagingNodeSocket = new Socket(identifierAddress, tracker);
					TCPSender tcpSender = new TCPSender(messagingNodeSocket);
					Deregister deregisterResponse = 
							new Deregister(Protocol.DEREGISTER_RESPONSE, Protocol.SUCCESS, registryHostname, registryPort);
					byte[] dataToSend = deregisterResponse.getBytes();
					tcpSender.sendData(dataToSend);
					
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
				
				System.out.println("Deregistration Successful. Node " + identifier + ":" 
				+ tracker + " is leaving the overlay");
				activeNodeList.remove(found);
				System.out.println("The number of messaging nodes currently constituting the overlay is (" 
						+ activeNodeList.size() + ")");
			}
			else {
				try {
					Socket messagingNodeSocket = new Socket(identifierAddress, tracker);
					TCPSender tcpSender = new TCPSender(messagingNodeSocket);
					Deregister deregisterResponse = 
							new Deregister(Protocol.DEREGISTER_RESPONSE, Protocol.FAILURE, registryHostname, registryPort);
					byte[] dataToSend = deregisterResponse.getBytes();
					tcpSender.sendData(dataToSend);
					
				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
				
				System.out.println("Deregistration failed. Node " + identifier + ":" 
				+ tracker + " is not registered in overlay");
			}
		}
	}
	
	/*
	 * Listens for input in the terminal where the registry is running.
	 * Begins in the main method
	 */
	private static void commandListener(Registry registry) {
		Scanner scanner = new Scanner(System.in);
		try {
			// runs infinitely until Registry exits
			System.out.println("Registry: Listening for commands");
			while(true) {
				//System.out.println("Enter a command whenever");
				String command = scanner.nextLine();
				
				if(command.startsWith("setup-overlay")) {
					String[] split = command.split(" ");
					if(split.length == 2) {
						int requiredConnections = 0;
						boolean validCommand = false;
						
						try {
							requiredConnections = Integer.parseInt(split[1]);
							validCommand = true;
						}
						catch (Exception e) {
							System.out.println("Invalid setup-overlay command. number-of-connections should be a number.");
						}
						
						if(validCommand && requiredConnections >= 4) {
							System.out.println("Starting overlay...");
							registry.setupOverlay(Integer.parseInt(split[1]));
							registry.sendMessagingNodesList();
						}
						else {
							System.out.println("number-of-connections must be at least 4");
						}
					}
					else {
						System.out.println("Invalid setup-overlay command. Missing number-of-connections");
					}
				}
				
				else if(command.equals("list-messaging-nodes")) {
					registry.listMessagingNodes();
				}
				
				else if(command.equals("list-weights")) {
					registry.listWeights();
				}
				
				else if(command.equals("send-overlay-link-weights")) {
					registry.sendOverlayLinkWeights();
				}
				else {
					System.out.println("Please enter a valid command");
				}
			}
		} catch(IllegalStateException e) {
			System.out.println("Terminal was closed. Command Listener exiting.");
		}
	}

	public void setupOverlay(int requiredConnections) {
		overlay = new Graph(activeNodeList, requiredConnections);
	}
	
	/*
	 * TODO: change activenodelist to overlay.getVertexes(). Maybe. Consider a bit longer
	 * Use activenodelist when not followed by setupOverlay command?
	 */
	public void sendMessagingNodesList() {
		//if(activeNodeList.size() >= 2) {
			for(int i = 0; i < activeNodeList.size(); i++) {
				Vertex currentNode = activeNodeList.get(i);
				List<Vertex> peers = overlay.getMessagingNodePeerList(currentNode);
				//System.out.println("One peer" + peers.get(0));
				try {
					Socket messagingNodeSocket = 
							new Socket(currentNode.getIdentifier(), currentNode.getTracker());
					TCPSender tcpSender = new TCPSender(messagingNodeSocket);
					MessagingNodesList messagingNodesList = 
							new MessagingNodesList(Protocol.MESSAGING_NODES_LIST, peers.size(), peers);
					byte[] dataToSend = messagingNodesList.getBytes();
					tcpSender.sendData(dataToSend);

				} catch (IOException ioe) {
					System.out.println(ioe.getMessage());
				}
			}
		//}
	}
	
	public void listMessagingNodes() {
		if(activeNodeList.size() == 0) {
			System.out.println("No active messaging nodes");
		}
		else {
			System.out.println("Printing list of active messaging nodes:");
			for(Vertex node : activeNodeList) {
				System.out.println(node.getIdentifier() + ":" + node.getTracker());
			}
		}
	}
	
	public void sendOverlayLinkWeights() {
		//System.out.println("Number of links:");
		for(int i = 0; i < activeNodeList.size(); i++) {
			Vertex currentNode = activeNodeList.get(i);
			List<Edge> peerEdges = overlay.getMessagingNodeEdgeList(currentNode);
			try {
				Socket messagingNodeSocket = 
						new Socket(currentNode.getIdentifier(), currentNode.getTracker());
				TCPSender tcpSender = new TCPSender(messagingNodeSocket);
				LinkWeights linkWeights = 
						new LinkWeights(Protocol.LINK_WEIGHTS, peerEdges.size(), peerEdges);
				byte[] dataToSend = linkWeights.getBytes();
				tcpSender.sendData(dataToSend);
				
			} catch (IOException ioe) {
				System.out.println(ioe.getMessage());
			}
			
		}
//		List<Edge> overlayEdges = overlay.getEdges();
//		for(Edge edge: overlayEdges) {
//			
//		}
	}
	
	public void listWeights() {
		System.out.println("Printing list of links comprising the overlay:");
		List<Edge> overlayEdges = overlay.getEdges();
		for(Edge edge : overlayEdges) {
			System.out.println(edge.toString());
		}
	}
	
	
	
	public static void main(String[] args) {
		Registry registry = new Registry(args[0]);
		System.out.println("Starting registry...");
		commandListener(registry);
		
	}
}
