package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jgrapht.alg.clustering.GirvanNewmanClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graph.clustering.SolverConfiguration;
import model.Result;

public class GirvanNewmanAlgorithmn extends SolverWrapper<String, DefaultWeightedEdge> {

	private SimpleWeightedGraph<String, DefaultWeightedEdge> graph;
	private static final int CLUSTER_COUNT = 3;

	@Override
	protected void initialize() {
		this.graph = SimpleWeightedGraph.<String, DefaultWeightedEdge>createBuilder(DefaultWeightedEdge.class).build();
	}

	@Override
	protected Result solve(SolverConfiguration config) {
		String paramNumberClusters = Optional.ofNullable(config.getConfig().get(SolverConfiguration.NUMBER_CLUSTERS))
				.orElse("" + CLUSTER_COUNT);
		GirvanNewmanClustering<String, DefaultWeightedEdge> gn = new GirvanNewmanClustering<>(this.graph,
				Integer.parseInt(paramNumberClusters));
		Clustering<String> clusters = gn.getClustering();
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
