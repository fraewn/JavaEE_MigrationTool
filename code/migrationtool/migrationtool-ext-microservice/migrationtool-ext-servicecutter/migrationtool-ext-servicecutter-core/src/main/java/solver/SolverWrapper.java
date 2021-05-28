package solver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import core.Edge;
import core.Graph;
import core.Node;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.data.Priorities;
import model.service.Service;
import model.service.ServiceCut;

/**
 * Implement this class to support an cluster algorithm.
 */
public abstract class SolverWrapper<N, E> implements Solver {

	private Graph originGraph;
	private Map<CouplingCriteria, Priorities> priorities;

	public SolverWrapper(Graph originGraph, Map<CouplingCriteria, Priorities> priorities) {
		this.originGraph = originGraph;
		this.priorities = priorities;
		convertGraph();
	}

	private void convertGraph() {
		// create nodes
		for (Node nodes : this.originGraph.getNodes()) {
			createNode(nodes);
		}
		// create edges
		for (Entry<Edge, Map<CouplingCriteria, Double>> entry : this.originGraph.getEdges().entrySet()) {
			// calc weight
			double sum = 0;
			for (Entry<CouplingCriteria, Double> values : entry.getValue().entrySet()) {
				Priorities prio = this.priorities.get(values.getKey());
				sum += values.getValue() * prio.getValue();
			}
			if (sum >= 0) {
				// positive edge
				createEdge(entry.getKey(), sum);
			} else {
				// negative edge
				treatNegativeScore();
			}
		}
	}

	protected void treatNegativeScore() {
		// negative scores are removed
	}

	/**
	 * Create a new node on the graph
	 *
	 * @param node the node
	 */
	protected abstract void createNode(Node node);

	/**
	 * Create a new edge on the graph
	 *
	 * @param edge   the edge
	 * @param weight the weight
	 */
	protected abstract void createEdge(Edge edge, double weight);

	/**
	 * @param families
	 */
	protected Result createResult(Map<String, List<String>> families) {
		Result res = new Result();
		ServiceCut cut = new ServiceCut();
		for (List<String> service : families.values()) {
			Service s = new Service();
			for (String instance : service) {
				s.getInstances().add(new Instance(instance));
			}
			cut.getServices().add(s);
		}
		res.setIsolatedServices(cut);
		return Analyzer.analyseResult(res, this.originGraph, this.priorities);
	}
}
