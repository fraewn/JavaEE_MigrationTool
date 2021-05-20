package core;

import java.util.Comparator;

import model.data.Instance;

public class Node implements Comparator<Node>, Comparable<Node> {

	private Instance instance;

	public Node(Instance instance) {
		this.instance = instance;
	}

	/**
	 * @return the instance
	 */
	public Instance getInstance() {
		return this.instance;
	}

	/**
	 * @param instance the instance to set
	 */
	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Node other = (Node) obj;
		if ((this.instance == null) ? (other.instance != null) : !this.instance.equals(other.instance)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.instance != null ? this.instance.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(Node i1, Node i2) {
		return i1.instance.compareTo(i2.instance);
	}

	@Override
	public int compareTo(Node i) {
		return this.instance.compareTo(i.instance);
	}
}
