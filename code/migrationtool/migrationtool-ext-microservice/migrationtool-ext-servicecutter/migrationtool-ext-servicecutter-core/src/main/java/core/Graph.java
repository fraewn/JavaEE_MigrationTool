package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.criteria.CouplingCriteria;
import ui.AdjacencyMatrix;

public class Graph {

	private Set<Node> nodes;

	private Map<Edge, Map<CouplingCriteria, Double>> edges;

	private List<CouplingGroup> relatedGroups;

	public Graph() {
		this.nodes = new HashSet<>();
		this.edges = new HashMap<>();
		this.relatedGroups = new ArrayList<>();
	}

	public void addNewEdge(Edge edge) {
		if (!this.edges.containsKey(edge)) {
			this.edges.put(edge, new HashMap<>());
		} else if (edge.getAttributes() != null) {
			for (Edge e : this.edges.keySet()) {
				if (e.equals(edge)) {
					if (e.getAttributes() == null) {
						e.setAttributes(new ArrayList<>());
					}
					e.getAttributes().addAll(edge.getAttributes());
				}
			}
		}
	}

	public void addNewScore(Edge edge, CouplingCriteria criteria, Double value) {
		if (this.edges.containsKey(edge)) {
			this.edges.get(edge).put(criteria, value);
		}
	}

	/**
	 * @return the nodes
	 */
	public Set<Node> getNodes() {
		return this.nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Set<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the edges
	 */
	public Map<Edge, Map<CouplingCriteria, Double>> getEdges() {
		return this.edges;
	}

	/**
	 * @param edges the edges to set
	 */
	public void setEdges(Map<Edge, Map<CouplingCriteria, Double>> edges) {
		this.edges = edges;
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

	public AdjacencyMatrix convertToMatrix() {
		AdjacencyMatrix adjMatrix = new AdjacencyMatrix();
		List<String> labelNodes = new ArrayList<>();
		double[][] matrix = new double[this.nodes.size()][this.nodes.size()];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				matrix[i][j] = Double.NEGATIVE_INFINITY;
			}
		}
		for (Node node : this.nodes) {
			labelNodes.add(node.getInstance().getQualifiedName());
		}
		for (Entry<Edge, Map<CouplingCriteria, Double>> edge : this.edges.entrySet()) {
			String origin = edge.getKey().getFirstNode().getInstance().getQualifiedName();
			int indexOrigin = labelNodes.indexOf(origin);
			String destination = edge.getKey().getSecondNode().getInstance().getQualifiedName();
			int indexDestination = labelNodes.indexOf(destination);
			double value = edge.getValue().size() == 0 ? 0
					: edge.getValue().values().stream().mapToDouble(x -> x).sum();
			matrix[indexOrigin][indexDestination] = value;
		}
		adjMatrix.setLabelNodes(labelNodes);
		adjMatrix.setMatrix(matrix);
		return adjMatrix;
	}
}
