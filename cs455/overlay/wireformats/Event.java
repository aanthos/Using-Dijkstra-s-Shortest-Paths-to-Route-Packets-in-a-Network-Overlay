package cs455.overlay.wireformats;

import java.io.IOException;
import java.util.ArrayList;


public interface Event {
	
	public byte[] getBytes() throws IOException;
	public void getType(byte[] marshalledBytes) throws IOException;
	
	// Wireformatwidget getters
	public int getProtocolMessageType();
	public long getTimestamp();
	public String getIdentifier();
	public int getTracker();
	public byte getStatusCode();
	public String getAdditionalInfo();
	public ArrayList<String> getPeerNodeInfo();
	public ArrayList<String> getPeerEdgeInfo();

}
