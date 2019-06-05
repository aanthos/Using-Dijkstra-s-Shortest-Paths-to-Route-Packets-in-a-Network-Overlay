package cs455.overlay.util;

import java.util.ArrayList;
import java.util.List;

import cs455.overlay.dijkstra.Edge;
import cs455.overlay.dijkstra.Graph;
import cs455.overlay.dijkstra.Vertex;

public class OverlayCreator {
	private Graph overlay;
	private ArrayList<Vertex> activeNodeList;
	private int requiredConnections;
	
	public OverlayCreator(ArrayList<Vertex> activeNodeList, int requiredConnections) {
		this.activeNodeList = activeNodeList;
		this.requiredConnections = requiredConnections;
	}
	
	
	public void setupOverlay() {
		overlay = new Graph(activeNodeList, requiredConnections);
		
		System.out.println("Printing Node List");
		List<Vertex> vertexes = overlay.getVertexes();
		for(Vertex vertex : vertexes) {
			System.out.println(vertex.toString());
		}
		
		System.out.println("Printing Edge List");
		List<Edge> edges = overlay.getEdges();
		for(Edge edge : edges) {
			System.out.println(edge.toString());
		}
	}
}
