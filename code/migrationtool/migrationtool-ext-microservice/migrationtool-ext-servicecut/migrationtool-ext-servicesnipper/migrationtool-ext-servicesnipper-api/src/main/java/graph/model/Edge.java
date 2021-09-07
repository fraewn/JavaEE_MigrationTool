package graph.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Class referencing all attributes of an edge
 */
public class Edge {

	/** edge weight */
	private EdgeWeight weight;
	/** edge attributes */
	private Set<EdgeAttribute> attributes;

	public Edge() {
		this.weight = new EdgeWeight();
		this.attributes = new HashSet<>();
	}

	public Edge(EdgeWeight weight, Set<EdgeAttribute> attributes) {
		this.weight = weight;
		this.attributes = attributes;
	}

	/**
	 * @return the weight
	 */
	public EdgeWeight getWeight() {
		return this.weight;
	}

	/**
	 * @return the attributes
	 */
	public Set<EdgeAttribute> getAttributes() {
		return this.attributes;
	}
}
