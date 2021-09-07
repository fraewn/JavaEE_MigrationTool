package solver;

import java.util.HashMap;
import java.util.Map;

import graph.clustering.ClusterAlgorithms;

/**
 * Factory class to get the implementation of a cluster algorithm
 */
public class SolverFactory {

	private static Map<ClusterAlgorithms, Solver> algorithmns = new HashMap<>();

	static {
		algorithmns.put(ClusterAlgorithms.LEUNG, new LeungAlgorithm());
		algorithmns.put(ClusterAlgorithms.CHINESE_WHISPERS, new ChineseWhispersAlgorithm());
		algorithmns.put(ClusterAlgorithms.MARKOV, new MarkovClusterAlgorithm());
		algorithmns.put(ClusterAlgorithms.GIRVAN_NEWMAN, new GirvanNewmanAlgorithm());
	}

	/**
	 * @param algo searched algorithm
	 * @return implementation of algorithm
	 */
	public static Solver getSolver(ClusterAlgorithms algo) {
		return algorithmns.get(algo);
	}
}
