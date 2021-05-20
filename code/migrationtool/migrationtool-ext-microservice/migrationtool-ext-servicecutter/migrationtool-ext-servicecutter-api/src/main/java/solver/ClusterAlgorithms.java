package solver;

/**
 *
 */
public enum ClusterAlgorithms {
	/**
	 *
	 */
	GIRVAN_NEWMAN(true),
	/**
	 *
	 */
	LEUNG,
	/**
	 *
	 */
	CHINESE_WHISPERS,
	/**
	 *
	 */
	MARKOV;

	private boolean deterministic;

	private ClusterAlgorithms() {
		// TODO Auto-generated constructor stub
	}

	private ClusterAlgorithms(boolean deterministic) {
		this.deterministic = deterministic;
	}

	/**
	 * @return the deterministic
	 */
	public boolean isDeterministic() {
		return this.deterministic;
	}
}
