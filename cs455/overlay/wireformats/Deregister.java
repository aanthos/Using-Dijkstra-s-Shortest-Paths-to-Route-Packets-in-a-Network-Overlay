package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Deregister implements Event {

	private int type;
	private long timestamp;
	private String identifier;
	private int tracker;
	private byte statusCode;
	
	public Deregister() {}
	
	public Deregister(int type, String identifier, int tracker) {
		this.type = type;
		timestamp = System.currentTimeMillis();
		this.identifier = identifier;
		this.tracker = tracker;
	}
	
	public Deregister(int type, byte statusCode, String identifier, int tracker) {
		this.type = type;
		this.statusCode = statusCode;
		timestamp = System.currentTimeMillis();
		this.identifier = identifier;
		this.tracker = tracker;
	}
	
	@Override
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		dout.writeLong(timestamp);
		
		byte[] identifierBytes = identifier.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		dout.writeInt(tracker);
		dout.writeByte(statusCode);
		
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
		timestamp = din.readLong();
		
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		
		identifier = new String(identifierBytes);
		
		tracker = din.readInt();
		
		statusCode = din.readByte();
		
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
		return timestamp;
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return identifier;
	}

	@Override
	public int getTracker() {
		// TODO Auto-generated method stub
		return tracker;
	}

	@Override
	public byte getStatusCode() {
		// TODO Auto-generated method stub
		return statusCode;
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
		// TODO Auto-generated method stub
		return null;
	}

}
