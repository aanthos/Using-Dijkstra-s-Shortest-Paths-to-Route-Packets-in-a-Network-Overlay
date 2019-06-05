package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import cs455.overlay.node.Node;

public class EventFactory {

	// static variable single_instance of type EventFactory
	private static EventFactory single_instance = null;
	// currentNode can also be Registry
	private Node currentNode = null;
	
	private EventFactory() {};
	
	// static method to create instance of EventFactory class
	public static EventFactory getInstance() {
		if (single_instance == null) {
			single_instance = new EventFactory();
		}
		
		return single_instance;
	}
	
	public void setCurrentNode(Node node) {
		currentNode = node;
	}
	
	// figures out what kind of message it is
	// similar to getType in Register and other message classes
	// Used in TCPReceiverThread
	public void getType(byte[] data) throws IOException {
		
		// grab type of message from the message the TCPReceiverThread received
		int type = 0;
		ByteArrayInputStream baInputStream = 
				new ByteArrayInputStream(data);
		DataInputStream din =
				new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		baInputStream.close();
		din.close();
		
		if(type == Protocol.REGISTER_REQUEST || type == Protocol.REGISTER_RESPONSE) {
			Event event = new Register();
			event.getType(data);
			currentNode.onEvent(event);
		}
		
		if(type == Protocol.MESSAGING_NODES_LIST) {
			Event event = new MessagingNodesList();
			event.getType(data);
			currentNode.onEvent(event);
		}
		
		if(type == Protocol.LINK_WEIGHTS) {
			Event event = new LinkWeights();
			event.getType(data);
			currentNode.onEvent(event);
		}
		
		if(type == Protocol.DEREGISTER_REQUEST || type == Protocol.DEREGISTER_RESPONSE) {
			Event event = new Deregister();
			event.getType(data);
			currentNode.onEvent(event);
		}
		
//		switch(type)
//		{
//			case Protocol.REGISTER_REQUEST :
//				Event event = new Register();
//				event.getType(data);
//				currentNode.onEvent(event);
//			
//			case Protocol.REGISTER_RESPONSE :
//				Event event = new Register();
//				event.getType(data);
//				currentNode
//		}
	}
}
