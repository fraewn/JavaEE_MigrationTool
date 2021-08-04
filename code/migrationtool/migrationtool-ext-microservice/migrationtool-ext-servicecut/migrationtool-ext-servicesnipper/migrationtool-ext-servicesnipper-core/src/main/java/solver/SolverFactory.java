package solver;

import java.util.HashMap;
import java.util.Map;

import graph.clustering.ClusterAlgorithms;

public class SolverFactory {

	private static Map<ClusterAlgorithms, Solver> algorithmns = new HashMap<>();

	static {
		algorithmns.put(ClusterAlgorithms.LEUNG, new LeungAlgorithmn());
		algorithmns.put(ClusterAlgorithms.CHINESE_WHISPERS, new ChineseWhispersAlgorithmn());
		algorithmns.put(ClusterAlgorithms.MARKOV, new MarkovClusterAlgorithmn());
		algorithmns.put(ClusterAlgorithms.GIRVAN_NEWMAN, new GirvanNewmanAlgorithmn());
	}

	public static Solver getSolver(ClusterAlgorithms algo) {
		return algorithmns.get(algo);
	}
}
