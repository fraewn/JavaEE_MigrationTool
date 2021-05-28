package solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.algorithm.community.Leung;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import core.Graph;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Priorities;

public class LeungAlgorithmn extends SolverWrapper<Node, Edge> {

	private static final String WEIGHT = "weight";
	private SingleGraph graph;

	public LeungAlgorithmn(Graph originGraph, Map<CouplingCriteria, Priorities> priorities) {
		super(originGraph, priorities);
		this.graph = new SingleGraph("ServiceGraph");
	}

	@Override
	public Result solve(SolverConfiguration config) {
		String paramM = config.getConfig().get(SolverConfiguration.LEUNG_PARAM_M);
		String paramDelta = config.getConfig().get(SolverConfiguration.LEUNG_PARAM_DELTA);
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
	protected void createNode(core.Node node) {
		this.graph.addNode(node.getInstance().getQualifiedName());
	}

	@Override
	protected void createEdge(core.Edge edge, double weight) {
		String firstName = edge.getFirstNode().getInstance().getQualifiedName();
		String secondName = edge.getSecondNode().getInstance().getQualifiedName();
		Edge res = this.graph.addEdge(firstName + "-" + secondName, firstName, secondName);
		res.setAttribute(WEIGHT, weight);
	}
}
