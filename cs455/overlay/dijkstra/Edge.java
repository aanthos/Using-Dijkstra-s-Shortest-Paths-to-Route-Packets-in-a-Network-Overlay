package cs455.overlay.dijkstra;

public class Edge {

	//private final int id;
	private final Vertex source;
	private final Vertex destination;
	private final int weight;
	
	public Edge(Vertex source, Vertex destination, int weight) {
		//this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}
	
	public Vertex getSource() {
		return source;
	}
	
	public Vertex getDestination() {
		return destination;
	}
	
	public int getWeight() {
		return weight;
	}
	
	@Override
	public String toString() {
		return source + " " + destination + " " + weight;
	}
}
