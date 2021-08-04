package solver;

import java.util.Map;

import graph.clustering.SolverConfiguration;
import model.Graph;
import model.Result;
import model.criteria.CouplingCriteria;
import model.priorities.Priorities;

public interface Solver {

	/**
	 * Find the candidate service cuts using an algorithm on the already created
	 * graph.
	 */
	Result solve(Graph originGraph, Map<CouplingCriteria, Priorities> priorities, SolverConfiguration config);

}
