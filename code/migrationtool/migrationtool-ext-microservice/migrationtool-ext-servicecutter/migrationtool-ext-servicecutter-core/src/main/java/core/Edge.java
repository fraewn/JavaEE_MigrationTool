package core;

public class Edge {

	private Node firstNode;

	private Node secondNode;

	private EdgeWeight weight;

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

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Edge other = (Edge) obj;
		if ((this.firstNode == null) ? (other.firstNode != null) : !this.firstNode.equals(other.firstNode)) {
			return false;
		}
		if ((this.secondNode == null) ? (other.secondNode != null) : !this.secondNode.equals(other.secondNode)) {
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
}
