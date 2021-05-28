package processing;

import model.criteria.CouplingCriteria;

/**
 * @author Rene
 *
 */
public enum GraphProcessingSteps implements ProcessAutomate<GraphProcessingSteps>, SubProcesses {
	/**
	 * Step representing the model editing
	 */
	EDIT_MODEL {
		@Override
		public GraphProcessingSteps nextStep() {
			return NODE_CREATION;
		}
	},

	NODE_CREATION {
		@Override
		public GraphProcessingSteps nextStep() {
			return EDGE_CREATION;
		}
	},

	EDGE_CREATION {
		@Override
		public GraphProcessingSteps nextStep() {
			return CALC_WEIGHT;
		}

		@Override
		public ProcessAutomate<?> startState() {
			return GraphCreationSteps.SAME_CONTEXT;
		}

		@Override
		public ProcessAutomate<?> finishedState() {
			return GraphCreationSteps.CREATION_DONE;
		}

		@Override
		public int processCount() {
			return GraphCreationSteps.values().length;
		}
	},

	CALC_WEIGHT {
		@Override
		public ProcessAutomate<?> startState() {
			return CouplingCriteria.IDENTITY_LIFECYCLE;
		}

		@Override
		public ProcessAutomate<?> finishedState() {
			return CouplingCriteria.SECURITY_CONSTRAINT;
		}

		@Override
		public int processCount() {
			return CouplingCriteria.values().length;
		}

		@Override
		public GraphProcessingSteps nextStep() {
			return SOLVE_CLUSTER;
		}
	},

	SOLVE_CLUSTER {
		@Override
		public GraphProcessingSteps nextStep() {
			return FINISHED;
		}
	},

	FINISHED {
		@Override
		public GraphProcessingSteps previousStep() {
			return SOLVE_CLUSTER;
		}

		@Override
		public GraphProcessingSteps nextStep() {
			return null;
		}
	};

	public boolean isProcessDone() {
		return equals(FINISHED);
	}

	public boolean hasSubProcess() {
		return startState() != null;
	}

	public int getProcentOfProgress() {
		return getProcentOfProgress(0);
	}

	public int getProcentOfProgress(int ordinalSubProcess) {
		double processCount = GraphProcessingSteps.values().length;
		double current = ordinal() / processCount;
		double res = current;
		if (hasSubProcess()) {
			double range = 1. / processCount;
			double y = ((double) ordinalSubProcess) / processCount();
			res += y * range;
		}
		return (int) (res * 100);
	}

	@Override
	public ProcessAutomate<?> startState() {
		return null;
	}

	@Override
	public GraphProcessingSteps previousStep() {
		return null;
	}

	@Override
	public ProcessAutomate<?> finishedState() {
		return null;
	}

	@Override
	public int processCount() {
		return 0;
	}
}
