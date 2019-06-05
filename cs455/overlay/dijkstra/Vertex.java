package cs455.overlay.dijkstra;

/*
 * Will represent each node
 */
public class Vertex {

	//private final String id;
	private final String identifier;
	private final int tracker;
	
	public Vertex(String identifier, int tracker) {
		//this.id = id;
		this.identifier = identifier;
		this.tracker = tracker;
	}
	
//	public String getId() {
//		return id;
//	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public int getTracker() {
		return tracker;
	}
	
	@Override
	public String toString() {
		return identifier + ":" + tracker;
		
	}
}
