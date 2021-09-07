package graph.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

/**
 * Model represents a undirected and weighted graph.
 */
public interface GraphModel {

	/**
	 * Add a new vertex to the model
	 *
	 * @param vertex new vertex
	 */
	void addNewVertex(String vertex);

	/**
	 * Add a new edge to the model
	 *
	 * @param vertexA vertex origin
	 * @param vertexB vertex destination
	 * @param attr    edge attributes
	 */
	void addNewEdge(String vertexA, String vertexB, EdgeAttribute... attr);

	/**
	 * Add a new edge attribute to an existing edge
	 *
	 * @param vertexA vertex origin
	 * @param vertexB vertex destination
	 * @param attr    edge attribute
	 */
	void addEdgeAttribute(String vertexA, String vertexB, EdgeAttribute... attr);

	/**
	 * Adds a new edge score to an existing edge
	 *
	 * @param vertexA  vertex origin
	 * @param vertexB  vertex destination
	 * @param criteria corresponding coupling criteria
	 * @param value    new value
	 */
	void addEdgeScore(String vertexA, String vertexB, CouplingCriteria criteria, Double value);

	/**
	 * Sets a new edge score to an existing edge
	 *
	 * @param vertexA  vertex origin
	 * @param vertexB  vertex destination
	 * @param criteria corresponding coupling criteria
	 * @param value    new value
	 */
	void setEdgeScore(String vertexA, String vertexB, CouplingCriteria criteria, Double value);

	/**
	 * Checks if there is an existing edge in the data model
	 *
	 * @param vertexA vertex origin
	 * @param vertexB vertex destination
	 * @return edge exists
	 */
	boolean hasEdge(String vertexA, String vertexB);

	/**
	 * Checks if there is a specific attribute at the corresponding edge
	 *
	 * @param vertexA vertex origin
	 * @param vertexB vertex destination
	 * @param attr    searched attribute
	 * @return edge attribute exists
	 */
	boolean hasEdgeAttribute(String vertexA, String vertexB, EdgeAttribute attr);

	/**
	 * @param vertexA vertex origin
	 * @param vertexB vertex destination
	 * @return total weight
	 */
	Double getCurrentWeight(String vertexA, String vertexB);

	/**
	 * @param vertexA  vertex origin
	 * @param vertexB  vertex destination
	 * @param criteria coupling criteria
	 * @return criteria weight
	 */
	Double getCurrentWeightOfCriteria(String vertexA, String vertexB, CouplingCriteria criteria);

	/**
	 * @param vertex searched vertex
	 * @return gets a list of all vertices connected to the searched vertex
	 */
	Set<String> getAllNeighbours(String vertex);

	/**
	 * @return list of all edges
	 */
	Map<String, Set<String>> getAllEdges();

	/**
	 * @return list of all vertices
	 */
	Collection<String> getAllVertices();

	/**
	 * @return the graph
	 */
	Map<String, Map<String, Double>> getGraph();

	/**
	 * Values multiplied with prioritized value
	 *
	 * @return the graph
	 */
	Map<String, Map<String, Double>> getGraph(Map<CouplingCriteria, Priorities> priorities);
}
