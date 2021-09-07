package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import graph.model.AdjacencyList;
import graph.model.EdgeAttribute;
import graph.model.GraphModel;
import model.criteria.CouplingCriteria;

/**
 * Class to create a proxy between the graph model and the used methods during
 * the algorithm
 */
public class Graph {

	/** reference to the graph model */
	private GraphModel graphModel;
	/** reference to all groups of the edge creation */
	private List<CouplingGroup> relatedGroups;

	public Graph() {
		this.graphModel = new AdjacencyList();
		this.relatedGroups = new ArrayList<>();
	}

	/**
	 * Adds a new node
	 *
	 * @param node node
	 */
	public void addNewVertex(String node) {
		this.graphModel.addNewVertex(node);
	}

	/**
	 * Adds a new edge
	 *
	 * @param edge edge
	 * @param attr attr
	 */
	public void addNewEdge(EdgeWrapper edge, EdgeAttribute... attr) {
		if (this.graphModel.hasEdge(edge.getFirstNode(), edge.getSecondNode())) {
			this.graphModel.addEdgeAttribute(edge.getFirstNode(), edge.getSecondNode(), attr);
		} else {
			this.graphModel.addNewEdge(edge.getFirstNode(), edge.getSecondNode(), attr);
		}
	}

	/**
	 * Adds a new score
	 *
	 * @param edge     edge
	 * @param criteria coupling criteria
	 * @param value    new value
	 */
	public void addNewScore(EdgeWrapper edge, CouplingCriteria criteria, Double value) {
		this.graphModel.addEdgeScore(edge.getFirstNode(), edge.getSecondNode(), criteria, value);
	}

	/**
	 * Check if edge exists
	 *
	 * @param edge edge
	 * @return exists
	 */
	public boolean hasEdge(EdgeWrapper edge) {
		return this.graphModel.hasEdge(edge.getFirstNode(), edge.getSecondNode());
	}

	/**
	 * Check if edge has an attribute
	 *
	 * @param edge edge
	 * @param attr attr
	 * @return exists
	 */
	public boolean hasAttribute(EdgeWrapper edge, EdgeAttribute attr) {
		return this.graphModel.hasEdgeAttribute(edge.getFirstNode(), edge.getSecondNode(), attr);
	}

	/**
	 * Gets the edge weight
	 *
	 * @param wrapper edge
	 * @return current weight
	 */
	public Double getEdgeWeight(EdgeWrapper wrapper) {
		return this.graphModel.getCurrentWeight(wrapper.getFirstNode(), wrapper.getSecondNode());
	}

	/**
	 * Gets the edge weight of an criteria
	 *
	 * @param wrapper  edge
	 * @param criteria coupling criteria
	 * @return current weight
	 */
	public Double getEdgeWeight(EdgeWrapper wrapper, CouplingCriteria criteria) {
		return this.graphModel.getCurrentWeightOfCriteria(wrapper.getFirstNode(), wrapper.getSecondNode(), criteria);
	}

	/**
	 * Get directly linked neighbors of a node
	 *
	 * @param node searched node
	 * @return direct neighbors
	 */
	public Collection<EdgeWrapper> getDirectNeighbours(String node) {
		List<EdgeWrapper> res = new ArrayList<>();
		for (String edge : this.graphModel.getAllNeighbours(node)) {
			res.add(new EdgeWrapper(node, edge));
		}
		return res;
	}

	/**
	 * @return get all edges in the model
	 */
	public Collection<EdgeWrapper> getAllEdges() {
		List<EdgeWrapper> res = new ArrayList<>();
		for (Entry<String, Set<String>> edge : this.graphModel.getAllEdges().entrySet()) {
			for (String dest : edge.getValue()) {
				res.add(new EdgeWrapper(edge.getKey(), dest));
			}
		}
		return res;
	}

	/**
	 * Adds a group to the list
	 *
	 * @param group coupling group
	 */
	public void addRelatedGroup(CouplingGroup group) {
		this.relatedGroups.add(group);
	}

	/**
	 * @return the relatedGroups
	 */
	public List<CouplingGroup> getRelatedGroups() {
		return this.relatedGroups;
	}

	/**
	 * @return the graph
	 */
	public GraphModel getGraphModel() {
		return this.graphModel;
	}
}
