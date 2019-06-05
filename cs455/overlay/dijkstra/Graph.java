package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// test shuffle

public class Graph {
	
	private final List<Vertex> vertexes;
	private final List<Edge> edges;
	
	public Graph(List<Vertex> vertexes, List<Edge> edges) {
		this.vertexes = vertexes;
		this.edges = edges;
	}
	
	public List<Vertex> getVertexes() {
		return vertexes;
	}
	
	public List<Edge> getEdges() {
		return edges;
	}
	
	public Graph(List<Vertex> registeredNodes, int requiredConnections) {
		this.vertexes = registeredNodes;
		this.edges = new ArrayList<Edge>();
		Random random = new Random();
		// Keeps track of how many connections each node has
		int[] connectionCounter = new int[vertexes.size()];
		
		
		/*
		 * connects every node to at least one other to avoid partitions/islands
		 */
		int max = 10;
		int min = 1;
		for(int i = 0; i < vertexes.size() - 1; i++) {
			Edge e = 
					new Edge(vertexes.get(i), vertexes.get(i+1), random.nextInt((max - min) + 1) + min);
			edges.add(e);
			
			// add one to the neighbor nodes connection total
			connectionCounter[i] += 1;
			connectionCounter[i+1] += 1;
		}
		
		// last node and first node not connected in for loop. This does it here.
		Edge e = 
				new Edge(vertexes.get(vertexes.size() - 1), vertexes.get(0), random.nextInt((max - min) + 1) + min);
		edges.add(e);
		connectionCounter[vertexes.size() - 1] = connectionCounter[vertexes.size() - 1] + 1;
		connectionCounter[0] = connectionCounter[0] + 1;
		
		
		/*
		 * Connect nodes with each other randomly
		 */
		
		// create array filled with node list indices
		int[] randomlyOrderedNodeIndices = new int[vertexes.size()];
		for(int i = 0; i < vertexes.size(); i++) {
			randomlyOrderedNodeIndices[i] = i;
		}
		
		// loop that goes through every node in original list vertexes / registeredNode
		for(int i = 0; i < vertexes.size(); i++) {
			// shuffles randomlyOrderedNodeIndices every time for more random pairings
			Collections.shuffle(Arrays.asList(randomlyOrderedNodeIndices));
			
			// loops through randomlyOrderedIndices for a potential destination node
			for(int j = 0; j < randomlyOrderedNodeIndices.length; j++) {
				// checks to see if potential destination node has more than the specified required connections
				if(connectionCounter[i] < requiredConnections) {
					int destinationNodeIndex = randomlyOrderedNodeIndices[j];
					
					if( (connectionCounter[destinationNodeIndex] >= requiredConnections) || 
							vertexes.get(destinationNodeIndex).equals(vertexes.get(i))) {
						//do nothing
					}
					else {
						 edges.add(
								 new Edge(vertexes.get(i), vertexes.get(destinationNodeIndex), random.nextInt((max - min) + 1) + min));
						 connectionCounter[i] += 1;
						 connectionCounter[destinationNodeIndex] += 1;
					}
				}
				else {
					break;
				}
			}
		}
		
		/*
		 * Check for any nodes with less than the required amount of connections.
		 */
		for(int i = 0; i < vertexes.size(); i++) {
			// check if current node has enough connections
			if(connectionCounter[i] < requiredConnections) {
				for(int j = i+1; j < vertexes.size(); j++) {
					// check if neighbor node (in the list) has enough connections
					if(connectionCounter[j] < requiredConnections) {
						Edge edge = new Edge(vertexes.get(i), vertexes.get(j), random.nextInt((max - min) + 1) + min);
						if(edges.contains(edge)) {
							// do nothing
						}
						else {
							edges.add(edge);
							connectionCounter[i] += 1;
							connectionCounter[j] += 1;
						}
					}
				}
			}
		}
	}

	/*
	 * Returns list of nodes that are connected to a specific node during overlay construction
	 * Uses Set to avoid duplicate listing of peers
	 */
	public List<Vertex> getMessagingNodePeerList(Vertex vertex) {
		Set<Vertex> peers = new HashSet<Vertex>();
		
		for(int i = 0; i < edges.size(); i++) {
			if(edges.get(i).getSource().equals(vertex)) {
				peers.add(edges.get(i).getDestination());
			}
			
			if(edges.get(i).getDestination().equals(vertex)) {
				peers.add(edges.get(i).getSource());
			}
		}
		
		List<Vertex> peersList = new ArrayList<Vertex>(peers);
		
		//System.out.println("In getMessagingNodePeerList: " + peersList.get(0));
		return peersList;
	}
	
	public List<Edge> getMessagingNodeEdgeList(Vertex vertex) {
		List<Edge> edgeList = new ArrayList<Edge>();
		
		for(Edge edge : edges) {
			if(edge.getSource().equals(vertex) ||
					edge.getDestination().equals(vertex)) {
				edgeList.add(edge);
			}
		}
		return edgeList;
	}
}
