package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Register implements Event {

	// WireFormatWidget slides on marshalling
	
	// for the Request message
	private int type;
	private long timestamp;
	// hostname / IP?
	private String identifier;
	// Port Number?
	private int tracker;
	
	// for the Response message
	private byte statusCode;
	private String additionalInfo;
	
	public Register() {
		timestamp = System.currentTimeMillis();
	}
	
	// used for initializing register request messages
	public Register(int type, String nodeHostname, int nodePort) {
		this.type = type;
		timestamp = System.currentTimeMillis();
		identifier = nodeHostname;
		tracker = nodePort;
	}
	
	// used for initializing register response messages
	public Register(int type, byte statusCode, String nodeHostname, int nodePort) {
		this.type = type;
		timestamp = System.currentTimeMillis();
		identifier = nodeHostname;
		tracker = nodePort;
		this.statusCode = statusCode;
		additionalInfo = "No new info";
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

	
	// getters for grabbing message info
	@Override
	public int getProtocolMessageType() {
		return type;
	}
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	@Override
	public String getIdentifier() {
		return identifier;
	}
	@Override
	public int getTracker() {
		return tracker;
	}
	@Override
	public byte getStatusCode() {
		return statusCode;
	}
	@Override
	public String getAdditionalInfo() {
		return additionalInfo;
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
