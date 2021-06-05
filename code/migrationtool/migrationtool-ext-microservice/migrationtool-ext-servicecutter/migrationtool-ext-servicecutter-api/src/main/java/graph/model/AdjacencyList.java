package graph.model;

import java.util.HashMap;
import java.util.Map;

public class AdjacencyList {

	private Map<String, Map<String, Double>> graph;

	public AdjacencyList() {
		this.graph = new HashMap<>();
	}

	public void addNewVertex(String vertex) {
		if (!this.graph.containsKey(vertex)) {
			this.graph.put(vertex, new HashMap<>());
		}
	}

	public void addNewEdge(String vertexA, String vertexB, Double weight) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB)) {
			this.graph.get(vertexA).put(vertexB, weight);
		}
	}

	/**
	 * @return the graph
	 */
	public Map<String, Map<String, Double>> getGraph() {
		return this.graph;
	}
}
