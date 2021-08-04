package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.GraphInitException;

public class Edge {

	private Node firstNode;

	private Node secondNode;

	private EdgeWeight weight;

	private List<EdgeAttribute> attributes;

	public Edge(Node firstNode, Node secondNode) {
		if ((firstNode == null) || (secondNode == null)) {
			throw new GraphInitException("Null is not allowed");
		}
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.weight = new EdgeWeight();
		this.attributes = new ArrayList<>();
	}

	public String getId() {
		return this.firstNode.getId() + "---" + this.secondNode.getId();
	}

	public String getIdSwaped() {
		return this.secondNode.getId() + "---" + this.firstNode.getId();
	}

	/**
	 * @return the weight
	 */
	public EdgeWeight getWeight() {
		return this.weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(EdgeWeight weight) {
		this.weight = weight;
	}

	/**
	 * @return the firstNode
	 */
	public Node getFirstNode() {
		return this.firstNode;
	}

	/**
	 * @param firstNode the firstNode to set
	 */
	public void setFirstNode(Node firstNode) {
		this.firstNode = firstNode;
	}

	/**
	 * @return the secondNode
	 */
	public Node getSecondNode() {
		return this.secondNode;
	}

	/**
	 * @param secondNode the secondNode to set
	 */
	public void setSecondNode(Node secondNode) {
		this.secondNode = secondNode;
	}

	/**
	 * @return the attributes
	 */
	public List<EdgeAttribute> getAttributes() {
		return this.attributes;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Edge other = (Edge) obj;
		if ((this.firstNode == null) ? (other.firstNode != null)
				: !(this.firstNode.equals(other.firstNode) || this.firstNode.equals(other.secondNode))) {
			return false;
		}
		if ((this.secondNode == null) ? (other.secondNode != null)
				: !(this.secondNode.equals(other.secondNode) || this.secondNode.equals(other.firstNode))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.firstNode != null ? this.firstNode.hashCode() : 0);
		hash = (53 * hash) + (this.secondNode != null ? this.secondNode.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		return this.firstNode.toString() + "<--  -->" + this.secondNode.toString();
	}
}
