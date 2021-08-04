package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.model.AdjacencyList;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public class Graph {

	private Set<String> nodes;

	private Map<String, Edge> edges;

	private List<CouplingGroup> relatedGroups;

	public Graph() {
		this.nodes = new HashSet<>();
		this.edges = new HashMap<>();
		this.relatedGroups = new ArrayList<>();
	}

	public void addNewNode(String node) {
		if (!this.nodes.contains(node)) {
			this.nodes.add(node);
		}
	}

	public void addNewEdge(Edge edge) {
		if (!hasEdge(edge)) {
			this.edges.put(edge.getId(), edge);
		} else if (!edge.getAttributes().isEmpty()) {
			Edge e = getEdge(edge);
			for (EdgeAttribute attr : edge.getAttributes()) {
				if (!e.getAttributes().contains(attr)) {
					e.getAttributes().add(attr);
				}
			}
		}
	}

	public void addNewScore(Edge edge, CouplingCriteria criteria, Double value) {
		if (hasEdge(edge)) {
			Edge e = getEdge(edge);
			e.getWeight().addNewValue(criteria, value);
		}
	}

	public boolean hasEdge(Edge edge) {
		return this.edges.containsKey(edge.getId()) || this.edges.containsKey(edge.getIdSwaped());
	}

	public Edge getEdge(Edge edge) {
		Edge e = this.edges.get(edge.getId());
		e = e == null ? this.edges.get(edge.getIdSwaped()) : e;
		return e;
	}

	public Collection<Edge> getAllEdges() {
		return this.edges.values();
	}

	/**
	 * @return the relatedGroups
	 */
	public List<CouplingGroup> getRelatedGroups() {
		return this.relatedGroups;
	}

	/**
	 * @param relatedGroups the relatedGroups to set
	 */
	public void setRelatedGroups(List<CouplingGroup> relatedGroups) {
		this.relatedGroups = relatedGroups;
	}

	public AdjacencyList convert() {
		return convert(null);
	}

	public AdjacencyList convert(Map<CouplingCriteria, Priorities> priorities) {
		AdjacencyList adjList = new AdjacencyList();
		for (String node : this.nodes) {
			adjList.addNewVertex(node);
		}
		for (Edge edge : this.edges.values()) {
			double weight = priorities == null ? edge.getWeight().getTotalScore()
					: edge.getWeight().getFinalScore(priorities);
			adjList.addNewEdge(edge.getFirstNode().getId(), edge.getSecondNode().getId(), weight);
		}
		return adjList;
	}
}
