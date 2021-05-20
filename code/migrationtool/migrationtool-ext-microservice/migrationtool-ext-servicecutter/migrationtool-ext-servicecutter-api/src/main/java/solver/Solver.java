package solver;

import model.Result;

public interface Solver {

	/**
	 * Find the candidate service cuts using an algorithm on the already created
	 * graph.
	 */
	Result solve(SolverConfiguration config);

}
