package cs455.overlay.wireformats;

public class Protocol {
	
	// message types
	public static final int REGISTER_REQUEST = 0;
	public static final int REGISTER_RESPONSE = 1;
	public static final int DEREGISTER_REQUEST = 2;
	public static final int DEREGISTER_RESPONSE = 3;
	public static final int MESSAGING_NODES_LIST = 4;
	public static final int LINK_WEIGHTS = 5;
	public static final byte SUCCESS = 0;
	public static final byte FAILURE = 1;

}
