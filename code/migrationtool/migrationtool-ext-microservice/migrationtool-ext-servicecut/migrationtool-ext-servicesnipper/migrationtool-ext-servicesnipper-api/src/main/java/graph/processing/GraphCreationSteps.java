package graph.processing;

/**
 *
 */
public enum GraphCreationSteps {

	/**
	 * Creating edges same context
	 */
	SAME_CONTEXT,
	/**
	 * Creating edges relationship inheritance
	 */
	TYPE_INHERITANCE,
	/**
	 * Creating edges relationship composition
	 */
	TYPE_COMPOSITION,
	/**
	 * Creating edges relationship aggregation
	 */
	TYPE_AGGREGATION,
	/**
	 *
	 */
	USE_CASE,
	/**
	 *
	 */
	LATENCY,
	/**
	 *
	 */
	AGGREGATES,
	/**
	 *
	 */
	PREDEFINED_SERVICE,
	/**
	 *
	 */
	SHARED_OWNER,
	/**
	 *
	 */
	SECURITY_ZONES,
	/**
	 *
	 */
	ACCESS_GROUPS,
	/**
	 *
	 */
	CONTENT_VOLATILITY,
	/**
	 *
	 */
	STRUCTURAL_VOLATILITY,
	/**
	 *
	 */
	AVAILABILITY_CRITICALITY,
	/**
	 *
	 */
	CONSISTENCY_CRITICALITY,
	/**
	 *
	 */
	STORAGE_SIMILARITY,
	/**
	 *
	 */
	SECURITY_CRITICALITY,
	/**
	 *
	 */
	CREATION_DONE;
}
