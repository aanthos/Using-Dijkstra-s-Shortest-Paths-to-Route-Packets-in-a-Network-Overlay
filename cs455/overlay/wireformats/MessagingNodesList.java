package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs455.overlay.dijkstra.Vertex;

public class MessagingNodesList implements Event {

	private int type;
	private int totalPeerMessagingNodes;
	private ArrayList<String> peerNodeInfo;

	// for use in the event factory
	public MessagingNodesList() {}
	
	public MessagingNodesList(int type, int totalPeerMessagingNodes, List<Vertex> peers) {
		this.type = type;
		this.totalPeerMessagingNodes = totalPeerMessagingNodes;
		peerNodeInfo = new ArrayList<String>();
		for(Vertex peer : peers) {
			//System.out.println("In MessagingNodesList: " + peer.getIdentifier() + ":" + peer.getTracker());
			peerNodeInfo.add(peer.getIdentifier() + ":" + peer.getTracker());
		}
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		dout.writeInt(totalPeerMessagingNodes);
		
		for(String peer : peerNodeInfo) {
			byte[] peerBytes = peer.getBytes();
			int peerLength = peerBytes.length;
			dout.writeInt(peerLength);
			dout.write(peerBytes);
		}
		
//		byte[] identifierBytes = identifier.getBytes();
//		int elementLength = identifierBytes.length;
//		dout.writeInt(elementLength);
//		dout.write(identifierBytes);
//		
//		dout.writeInt(tracker);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		
		return marshalledBytes;
	}

	@Override
	public void getType(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = 
				new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = 
				new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		totalPeerMessagingNodes = din.readInt();
		
		// constructs empty list with specified initial capacity
		peerNodeInfo = new ArrayList<String>(totalPeerMessagingNodes);
		for(int i = 0; i < totalPeerMessagingNodes; i++) {
			int singlePeerNodeLength = din.readInt();
			byte[] singlePeerNodeBytes = new byte[singlePeerNodeLength];
			din.readFully(singlePeerNodeBytes);
			peerNodeInfo.add(new String(singlePeerNodeBytes));
		}
		
//		timestamp = din.readLong();
//		
//		int identifierLength = din.readInt();
//		byte[] identifierBytes = new byte[identifierLength];
//		din.readFully(identifierBytes);
//		
//		identifier = new String(identifierBytes);
//		
//		tracker = din.readInt();
		
		baInputStream.close();
		din.close();
	}

	@Override
	public int getProtocolMessageType() {
		return type;
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTracker() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte getStatusCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAdditionalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getPeerNodeInfo() {
		return peerNodeInfo;
	}

	@Override
	public ArrayList<String> getPeerEdgeInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
