package graph.processing;

import model.criteria.CouplingCriteria;
import utils.StateMachine;

/**
 * Processing steps to create a service cut
 */
public enum GraphProcessingSteps {
	/**
	 * model editing before the process starts (optional)
	 */
	EDIT_MODEL,
	/**
	 * step to create all vertex in the graph
	 */
	NODE_CREATION,
	/**
	 * step to create all edges
	 */
	EDGE_CREATION {
		@Override
		public StateMachine<?> subProcess() {
			return new StateMachine<>(GraphCreationSteps.class);
		}
	},
	/**
	 * step to calculate the weight of the edges
	 */
	CALC_WEIGHT {
		@Override
		public StateMachine<?> subProcess() {
			return new StateMachine<>(CouplingCriteria.class);
		}
	},
	/**
	 * Execute a cluster algorithmn
	 */
	SOLVE_CLUSTER,
	/**
	 * Export the result to a file
	 */
	SAVE_RESULT,
	/**
	 * Process done
	 */
	FINISHED;

	/**
	 * @return actual process step has a sub sequence
	 */
	public boolean hasSubProcess() {
		return subProcess() != null;
	}

	/**
	 * @return {@link StateMachine} of the sub sequence
	 */
	public StateMachine<?> subProcess() {
		return null;
	}
}
