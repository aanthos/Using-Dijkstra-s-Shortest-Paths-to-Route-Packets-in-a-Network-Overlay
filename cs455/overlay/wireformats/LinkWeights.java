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

import cs455.overlay.dijkstra.Edge;

public class LinkWeights implements Event {

	private int type;
	private int totalPeerEdges;
	private ArrayList<String> peerEdgeInfo;
	
	public LinkWeights() {}
	
	public LinkWeights(int type, int totalPeerEdges, List<Edge> peerEdges) {
		this.type = type;
		this.totalPeerEdges = totalPeerEdges;
		peerEdgeInfo = new ArrayList<String>();
		for(Edge edge : peerEdges) {
			peerEdgeInfo.add(edge.getSource().getIdentifier() + ":" + edge.getSource().getTracker() + " " +
					edge.getDestination().getIdentifier() + ":" + edge.getDestination().getTracker() + " " +
					edge.getWeight());
		}
	}
	
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		dout.writeInt(totalPeerEdges);
		
		for(String edge : peerEdgeInfo) {
			byte[] edgeBytes = edge.getBytes();
			int edgeLength = edgeBytes.length;
			dout.writeInt(edgeLength);
			dout.write(edgeBytes);
		}
		
//		dout.writeLong(timestamp);
//		
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
		totalPeerEdges = din.readInt();
		//System.out.println("Peer edges: " + totalPeerEdges);
		// constructs empty list with specified initial capacity
		peerEdgeInfo = new ArrayList<String>(totalPeerEdges);
		for(int i = 0; i < totalPeerEdges; i++) {
			int singlePeerEdgeLength = din.readInt();
			byte[] singlePeerEdgeBytes = new byte[singlePeerEdgeLength];
			din.readFully(singlePeerEdgeBytes);
			peerEdgeInfo.add(new String(singlePeerEdgeBytes));
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getPeerEdgeInfo() {
		return peerEdgeInfo;
	}

}
