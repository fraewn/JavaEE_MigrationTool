package processing;

/**
 *
 */
public enum GraphCreationSteps implements ProcessAutomate<GraphCreationSteps> {

	/**
	 * Creating edges same context
	 */
	SAME_CONTEXT {
		@Override
		public GraphCreationSteps nextStep() {
			return TYPE_INHERITANCE;
		}
	},
	/**
	 * Creating edges relationship inheritance
	 */
	TYPE_INHERITANCE {
		@Override
		public GraphCreationSteps nextStep() {
			return TYPE_COMPOSITION;
		}
	},
	/**
	 * Creating edges relationship composition
	 */
	TYPE_COMPOSITION {
		@Override
		public GraphCreationSteps nextStep() {
			return TYPE_AGGREGATION;
		}
	},
	/**
	 * Creating edges relationship aggregation
	 */
	TYPE_AGGREGATION {
		@Override
		public GraphCreationSteps nextStep() {
			return USE_CASE;
		}
	},
	/**
	 *
	 */
	USE_CASE {
		@Override
		public GraphCreationSteps nextStep() {
			return LATENCY;
		}
	},
	/**
	 *
	 */
	LATENCY {
		@Override
		public GraphCreationSteps nextStep() {
			return AGGREGATES;
		}
	},
	/**
	 *
	 */
	AGGREGATES {
		@Override
		public GraphCreationSteps nextStep() {
			return PREDEFINED_SERVICE;
		}
	},
	/**
	 *
	 */
	PREDEFINED_SERVICE {
		@Override
		public GraphCreationSteps nextStep() {
			return SHARED_OWNER;
		}
	},
	/**
	 *
	 */
	SHARED_OWNER {
		@Override
		public GraphCreationSteps nextStep() {
			return SECURITY_ZONES;
		}
	},
	/**
	 *
	 */
	SECURITY_ZONES {
		@Override
		public GraphCreationSteps nextStep() {
			return ACCESS_GROUPS;
		}
	},
	/**
	 *
	 */
	ACCESS_GROUPS {
		@Override
		public GraphCreationSteps nextStep() {
			return CONTENT_VOLATILITY;
		}
	},
	/**
	 *
	 */
	CONTENT_VOLATILITY {
		@Override
		public GraphCreationSteps nextStep() {
			return STRUCTURAL_VOLATILITY;
		}
	},
	/**
	 *
	 */
	STRUCTURAL_VOLATILITY {
		@Override
		public GraphCreationSteps nextStep() {
			return AVAILABILITY_CRITICALITY;
		}
	},
	/**
	 *
	 */
	AVAILABILITY_CRITICALITY {
		@Override
		public GraphCreationSteps nextStep() {
			return CONSISTENCY_CRITICALITY;
		}
	},
	/**
	 *
	 */
	CONSISTENCY_CRITICALITY {
		@Override
		public GraphCreationSteps nextStep() {
			return STORAGE_SIMILARITY;
		}
	},
	/**
	 *
	 */
	STORAGE_SIMILARITY {
		@Override
		public GraphCreationSteps nextStep() {
			return SECURITY_CRITICALITY;
		}
	},
	/**
	 *
	 */
	SECURITY_CRITICALITY {
		@Override
		public GraphCreationSteps nextStep() {
			return CREATION_DONE;
		}
	},
	/**
	 *
	 */
	CREATION_DONE {
		@Override
		public GraphCreationSteps nextStep() {
			return null;
		}
	};
}
