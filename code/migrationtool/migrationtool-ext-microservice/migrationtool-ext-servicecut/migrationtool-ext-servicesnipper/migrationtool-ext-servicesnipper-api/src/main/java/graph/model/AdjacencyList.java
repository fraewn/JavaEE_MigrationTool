package graph.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

/**
 * Collection of unordered lists used to represent a finite graph. Each
 * unordered list within an adjacency list describes the set of neighbors of a
 * particular vertex in the graph.
 *
 * The adjacency list represents a undirected and weighted graph.
 */
public class AdjacencyList implements GraphModel {

	/** adjacency list */
	private Map<String, Map<String, Edge>> graph;

	public AdjacencyList() {
		this.graph = new HashMap<>();
	}

	@Override
	public void addNewVertex(String vertex) {
		if (!this.graph.containsKey(vertex)) {
			this.graph.put(vertex, new HashMap<>());
		}
	}

	@Override
	public void addNewEdge(String vertexA, String vertexB, EdgeAttribute... attr) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB)) {
			if (!hasEdge(vertexA, vertexB)) {
				this.graph.get(vertexA).put(vertexB, new Edge());
				if (attr != null) {
					this.graph.get(vertexA).get(vertexB).getAttributes().addAll(Arrays.asList(attr));
				}
			}
		}
	}

	@Override
	public void addEdgeAttribute(String vertexA, String vertexB, EdgeAttribute... attr) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB) && (attr != null)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				this.graph.get(origin).get(destination).getAttributes().addAll(Arrays.asList(attr));
			}
		}
	}

	@Override
	public void addEdgeScore(String vertexA, String vertexB, CouplingCriteria criteria, Double value) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB) && (value != null)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				EdgeWeight weight = this.graph.get(origin).get(destination).getWeight();
				double currentValue = Optional.ofNullable(weight.getScoreOfCriteria(criteria)).orElse(0d);
				weight.addNewValue(criteria, currentValue + value);
			}
		}
	}

	@Override
	public void setEdgeScore(String vertexA, String vertexB, CouplingCriteria criteria, Double value) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB) && (value != null)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				EdgeWeight weight = this.graph.get(origin).get(destination).getWeight();
				weight.addNewValue(criteria, value);
			}
		}
	}

	@Override
	public boolean hasEdge(String vertexA, String vertexB) {
		return originOfEdge(vertexA, vertexB) != null;
	}

	private String originOfEdge(String vertexA, String vertexB) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB)) {
			if (this.graph.get(vertexA).containsKey(vertexB)) {
				return vertexA;
			}
			if (this.graph.get(vertexB).containsKey(vertexA)) {
				return vertexB;
			}
		}
		return null;
	}

	@Override
	public boolean hasEdgeAttribute(String vertexA, String vertexB, EdgeAttribute attr) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB) && (attr != null)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				if (this.graph.get(origin).get(destination).getAttributes().contains(attr)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Double getCurrentWeight(String vertexA, String vertexB) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				return this.graph.get(origin).get(destination).getWeight().getTotalScore();
			}
		}
		return null;
	}

	@Override
	public Double getCurrentWeightOfCriteria(String vertexA, String vertexB, CouplingCriteria criteria) {
		if (this.graph.containsKey(vertexA) && this.graph.containsKey(vertexB)) {
			String origin = originOfEdge(vertexA, vertexB);
			if (origin != null) {
				String destination = origin.equals(vertexA) ? vertexB : vertexA;
				return this.graph.get(origin).get(destination).getWeight().getScoreOfCriteria(criteria);
			}
		}
		return null;
	}

	@Override
	public Set<String> getAllNeighbours(String vertex) {
		if (this.graph.containsKey(vertex)) {
			Set<String> res = new HashSet<>();
			for (String dest : getAllVertices()) {
				if (hasEdge(vertex, dest)) {
					res.add(dest);
				}
			}
			return res;
		}
		return new HashSet<>();
	}

	@Override
	public Map<String, Set<String>> getAllEdges() {
		Map<String, Set<String>> res = new HashMap<>();
		for (Entry<String, Map<String, Edge>> entry : this.graph.entrySet()) {
			res.put(entry.getKey(), entry.getValue().keySet());
		}
		return res;
	}

	@Override
	public Collection<String> getAllVertices() {
		return this.graph.keySet();
	}

	@Override
	public Map<String, Map<String, Double>> getGraph() {
		Map<String, Map<String, Double>> result = new HashMap<>();
		for (Entry<String, Map<String, Edge>> entry : this.graph.entrySet()) {
			result.put(entry.getKey(), new HashMap<String, Double>());
			for (Entry<String, Edge> edge : entry.getValue().entrySet()) {
				Double value = edge.getValue().getWeight() != null ? edge.getValue().getWeight().getTotalScore() : null;
				result.get(entry.getKey()).put(edge.getKey(), value);
			}
		}
		return result;
	}

	@Override
	public Map<String, Map<String, Double>> getGraph(Map<CouplingCriteria, Priorities> priorities) {
		Map<String, Map<String, Double>> result = new HashMap<>();
		for (Entry<String, Map<String, Edge>> entry : this.graph.entrySet()) {
			result.put(entry.getKey(), new HashMap<String, Double>());
			for (Entry<String, Edge> edge : entry.getValue().entrySet()) {
				Double value = edge.getValue().getWeight() != null
						? edge.getValue().getWeight().getFinalScore(priorities)
						: null;
				result.get(entry.getKey()).put(edge.getKey(), value);
			}
		}
		return result;
	}

}
