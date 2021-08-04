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
import org.nlpub.watset.graph.ChineseWhispers;

import graph.clustering.NodeWeightings;
import graph.clustering.SolverConfiguration;
import model.Result;

public class ChineseWhispersAlgorithmn extends SolverWrapper<String, DefaultWeightedEdge> {

	private SimpleWeightedGraph<String, DefaultWeightedEdge> graph;
	private static final NodeWeightings WEIGHT = NodeWeightings.TOP;

	@Override
	protected void initialize() {
		this.graph = SimpleWeightedGraph.<String, DefaultWeightedEdge>createBuilder(DefaultWeightedEdge.class).build();
	}

	@Override
	protected Result solve(SolverConfiguration config) {
		String paramNodeWeight = Optional
				.ofNullable(config.getConfig().get(SolverConfiguration.CHINESE_WHISPERS_NODE_WEIGHT))
				.orElse(WEIGHT.toString());
		ChineseWhispers<String, DefaultWeightedEdge> cw = ChineseWhispers.<String, DefaultWeightedEdge>builder()
				.setWeighting(org.nlpub.watset.graph.NodeWeightings.parse(paramNodeWeight)).apply(this.graph);
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
