package graph.processing;

import model.criteria.CouplingCriteria;
import utils.StateMachine;

/**
 * @author Rene
 *
 */
public enum GraphProcessingSteps {
	/**
	 * Step representing the model editing
	 */
	EDIT_MODEL,

	NODE_CREATION,

	EDGE_CREATION {
		@Override
		public StateMachine<?> subProcess() {
			return new StateMachine<>(GraphCreationSteps.class);
		}
	},

	CALC_WEIGHT {
		@Override
		public StateMachine<?> subProcess() {
			return new StateMachine<>(CouplingCriteria.class);
		}
	},

	SOLVE_CLUSTER,

	SAVE_RESULT,

	FINISHED;

	public boolean hasSubProcess() {
		return subProcess() != null;
	}

	public StateMachine<?> subProcess() {
		return null;
	}
}
