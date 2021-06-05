package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.graphstream.algorithm.community.Leung;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import graph.clustering.SolverConfiguration;
import model.Result;

public class LeungAlgorithmn extends SolverWrapper<Node, Edge> {

	private static final String WEIGHT = "weight";
	private SingleGraph graph;

	private static final String M = "0.1";
	private static final String DELTA = "0.05";

	@Override
	protected void initialize() {
		this.graph = new SingleGraph("ServiceGraph");
	}

	@Override
	public Result solve(SolverConfiguration config) {
		String paramM = Optional.ofNullable(config.getConfig().get(SolverConfiguration.LEUNG_PARAM_M)).orElse(M);
		String paramDelta = Optional.ofNullable(config.getConfig().get(SolverConfiguration.LEUNG_PARAM_DELTA))
				.orElse(DELTA);
		Leung leung = new Leung(this.graph, null, WEIGHT);
		leung.setParameters(Double.valueOf(paramM), Double.valueOf(paramDelta));
		leung.compute();
		Map<String, List<String>> families = new HashMap<>();
		for (Node node : this.graph) {
			String family = node.getAttribute("ui.class").toString();
			families.putIfAbsent(family, new ArrayList<>());
			families.get(family).add(node.getId());
		}
		return createResult(families);
	}

	@Override
	protected void createNode(String node) {
		this.graph.addNode(node);
	}

	@Override
	protected void createEdge(String originVertex, String destinationVertex, double weight) {
		Edge res = this.graph.addEdge(originVertex + "-" + destinationVertex, originVertex, destinationVertex);
		res.setAttribute(WEIGHT, weight);
	}
}
