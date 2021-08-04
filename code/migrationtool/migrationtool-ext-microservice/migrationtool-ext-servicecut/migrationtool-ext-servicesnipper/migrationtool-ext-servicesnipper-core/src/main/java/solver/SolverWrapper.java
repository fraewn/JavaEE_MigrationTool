package solver;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import graph.clustering.SolverConfiguration;
import graph.model.AdjacencyList;
import model.Graph;
import model.Result;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import model.priorities.Priorities;
import model.serviceDefintion.Service;
import model.serviceDefintion.ServiceCut;

/**
 * Implement this class to support an cluster algorithm.
 */
public abstract class SolverWrapper<N, E> implements Solver {

	private Graph originGraph;
	private Map<CouplingCriteria, Priorities> priorities;

	@Override
	public Result solve(Graph originGraph, Map<CouplingCriteria, Priorities> priorities, SolverConfiguration config) {
		this.originGraph = originGraph;
		this.priorities = priorities;
		initialize();
		convertGraph();
		return solve(config);
	}

	protected abstract void initialize();

	protected abstract Result solve(SolverConfiguration config);

	private void convertGraph() {
		AdjacencyList adjList = this.originGraph.convert(this.priorities);
		// create nodes
		for (String node : adjList.getGraph().keySet()) {
			createNode(node);
		}
		// create edges
		for (Entry<String, Map<String, Double>> entry : adjList.getGraph().entrySet()) {
			// calc weight
			for (Entry<String, Double> edge : entry.getValue().entrySet()) {
				double sum = edge.getValue();
				if (sum >= 0) {
					// positive edge
					createEdge(entry.getKey(), edge.getKey(), sum);
				} else {
					// negative edge
					treatNegativeScore();
				}
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
	protected abstract void createNode(String node);

	/**
	 * Create a new edge on the graph
	 *
	 * @param edge   the edge
	 * @param weight the weight
	 */
	protected abstract void createEdge(String originVertex, String destinationVertex, double weight);

	/**
	 * @param families
	 */
	protected Result createResult(Map<String, List<String>> families) {
		Result res = new Result();
		ServiceCut cut = new ServiceCut();
		int x = 0;
		for (List<String> service : families.values()) {
			Service s = new Service();
			s.setName(uniqueId(++x, 'A', 26));
			for (String instance : service) {
				s.getInstances().add(new Instance(instance));
			}
			cut.getServices().add(s);
		}
		res.setIsolatedServices(cut);
		return Analyzer.analyseResult(res, this.originGraph, this.priorities);
	}

	private String uniqueId(int current, char start, int cycle) {
		return "" + (current % cycle) + "-" + start;
	}
}
