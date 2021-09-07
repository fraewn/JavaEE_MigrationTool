package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jfree.util.Log;
import org.jgrapht.alg.clustering.GirvanNewmanClustering;
import org.jgrapht.alg.interfaces.ClusteringAlgorithm.Clustering;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import graph.clustering.SolverConfiguration;
import model.Result;

/**
 * Girvan Newman Cluster algorithm
 */
public class GirvanNewmanAlgorithm extends SolverWrapper<String, DefaultWeightedEdge> {

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
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Callable<Clustering<String>> task = () -> gn.getClustering();
		Future<Clustering<String>> future = executor.submit(task);
		Clustering<String> clusters = null;
		try {
			clusters = future.get(10, TimeUnit.SECONDS);
		} catch (TimeoutException | InterruptedException | ExecutionException ex) {
			Log.warn(ex.getMessage());
		} finally {
			future.cancel(true); // may or may not desire this
		}
		Map<String, List<String>> families = new HashMap<>();
		if (clusters != null) {
			char id = 'A';
			for (Set<String> cluster : clusters.getClusters()) {
				String family = "" + id;
				families.putIfAbsent(family, new ArrayList<>(cluster));
				id++;
			}
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
