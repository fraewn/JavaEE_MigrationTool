package model;

import graph.model.EdgeAttribute;

/**
 * Wrapper class to hold the vertex values of a defined edge
 */
public class EdgeWrapper {

	/** first node */
	private String firstNode;
	/** second node */
	private String secondNode;
	/** attribute of edge, optional */
	private EdgeAttribute attr;

	public EdgeWrapper(String firstNode, String secondNode) {
		this.firstNode = firstNode;
		this.secondNode = secondNode;
	}

	public EdgeWrapper(String firstNode, String secondNode, EdgeAttribute attr) {
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.attr = attr;
	}

	/**
	 * @return the firstNode
	 */
	public String getFirstNode() {
		return this.firstNode;
	}

	/**
	 * @param firstNode the firstNode to set
	 */
	public void setFirstNode(String firstNode) {
		this.firstNode = firstNode;
	}

	/**
	 * @return the secondNode
	 */
	public String getSecondNode() {
		return this.secondNode;
	}

	/**
	 * @param secondNode the secondNode to set
	 */
	public void setSecondNode(String secondNode) {
		this.secondNode = secondNode;
	}

	/**
	 * @return the attr
	 */
	public EdgeAttribute getAttr() {
		return this.attr;
	}

	/**
	 * @param attr the attr to set
	 */
	public void setAttr(EdgeAttribute attr) {
		this.attr = attr;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final EdgeWrapper other = (EdgeWrapper) obj;
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
		return this.firstNode.toString() + "<->" + this.secondNode.toString();
	}
}
