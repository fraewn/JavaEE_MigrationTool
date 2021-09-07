package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.nlpub.watset.graph.MarkovClustering;

import graph.clustering.SolverConfiguration;
import model.Result;

/**
 * Markov Cluster algorithm (MCL)
 */
public class MarkovClusterAlgorithm extends SolverWrapper<String, DefaultWeightedEdge> {

	private SimpleWeightedGraph<String, DefaultWeightedEdge> graph;
	private static final int NUMBER_OF_EXPANSIONS = 2;
	private static final int POWER_COEFFICENT = 2;

	@Override
	protected void initialize() {
		this.graph = SimpleWeightedGraph.<String, DefaultWeightedEdge>createBuilder(DefaultWeightedEdge.class).build();
	}

	@Override
	protected Result solve(SolverConfiguration config) {
		String paramExpansions = Optional
				.ofNullable(
						config.getConfig().get(SolverConfiguration.MARKOV_NUMBER_OF_EXPANSIONS).replaceAll(",", "."))
				.orElse("" + NUMBER_OF_EXPANSIONS);
		String paramCoefficent = Optional
				.ofNullable(config.getConfig().get(SolverConfiguration.MARKOV_POWER_COEFFICENT).replaceAll(",", "."))
				.orElse("" + POWER_COEFFICENT);
		MarkovClustering<String, DefaultWeightedEdge> cw = MarkovClustering.<String, DefaultWeightedEdge>builder()
				.setE(Integer.parseInt(paramExpansions)).setR(Integer.parseInt(paramCoefficent)).apply(this.graph);
		Clustering<String> clusters = cw.getClustering();
		Map<String, List<String>> families = new HashMap<>();
		char id = 'A';
		for (Set<String> cluster : clusters.getClusters()) {
			String family = "" + id;
			families.putIfAbsent(family, new ArrayList<>(cluster));
			id++;
		}
		return createResult(families);
	}

	@Override
	protected void createNode(String node) {
		this.graph.addVertex(node);
	}

	@Override
	protected void createEdge(String originVertex, String destinationVertex, double weight) {
		DefaultWeightedEdge edge = this.graph.addEdge(originVertex, destinationVertex);
		this.graph.setEdgeWeight(edge, weight);
	}

}
