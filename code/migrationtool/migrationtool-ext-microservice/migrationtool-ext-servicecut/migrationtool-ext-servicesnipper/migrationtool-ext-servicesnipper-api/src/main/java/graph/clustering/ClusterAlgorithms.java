package graph.clustering;

/**
 * List of all supported cluster algorithm to separate the graph in smaller
 * communities
 */
public enum ClusterAlgorithms {
	/**
	 * Girvan Newman Algorithmn is a hierarchical method to detect communities in
	 * complex systems. The Girvan–Newman algorithm detects communities by
	 * progressively removing edges from the original network. The connected
	 * components of the remaining network are the communities.
	 */
	GIRVAN_NEWMAN(true),
	/**
	 * community detection algorithm based on the epidemic label propagation
	 * paradigm
	 */
	LEUNG,
	/**
	 * Chinese whispers is a hard partitioning, randomized, flat clustering method.
	 * Chinese whispers is time linear which means that it is extremely fast even if
	 * the number of nodes and links are very high in the network.
	 */
	CHINESE_WHISPERS,
	/**
	 * Markov Clustering (MCL) is a hard clustering algorithm that simulates random
	 * walks on the graph.
	 */
	MARKOV;

	/** is the provided algorithm deterministic */
	private boolean deterministic;

	ClusterAlgorithms() {
		// TODO Auto-generated constructor stub
	}

	ClusterAlgorithms(boolean deterministic) {
		this.deterministic = deterministic;
	}

	/**
	 * @return the deterministic
	 */
	public boolean isDeterministic() {
		return this.deterministic;
	}
}
