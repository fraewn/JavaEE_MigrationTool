package graph.processing;

import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;

/**
 * Processing steps to create a full graph
 */
public enum GraphCreationSteps {

	/**
	 * Creating edges of same context
	 */
	SAME_CONTEXT,
	/**
	 * Creating edges of relationship inheritance
	 */
	TYPE_INHERITANCE,
	/**
	 * Creating edges of relationship composition
	 */
	TYPE_COMPOSITION,
	/**
	 * Creating edges of relationship aggregation
	 */
	TYPE_AGGREGATION,
	/**
	 * Creating edges of usecase connections
	 */
	USE_CASE,
	/**
	 * Creating edges of {@link CouplingCriteria#LATENCY}
	 */
	LATENCY,
	/**
	 * Creating edges of {@link ArchitectureArtifact#AGGREGATES}
	 */
	AGGREGATES,
	/**
	 * Creating edges of {@link ArchitectureArtifact#PREDEFINED_SERVICES}
	 */
	PREDEFINED_SERVICE,
	/**
	 * Creating edges of {@link ArchitectureArtifact#SHARED_OWNER_GROUPS}
	 */
	SHARED_OWNER,
	/**
	 * Creating edges of {@link ArchitectureArtifact#SEPERATED_SECURITY_ZONES}
	 */
	SECURITY_ZONES,
	/**
	 * Creating edges of {@link ArchitectureArtifact#SECURITY_ACCESS_GROUPS}
	 */
	ACCESS_GROUPS,
	/**
	 * Creating edges of {@link CouplingCriteria#CONTENT_VOLATILITY}
	 */
	CONTENT_VOLATILITY,
	/**
	 * Creating edges of {@link CouplingCriteria#STRUCTURAL_VOLATILITY}
	 */
	STRUCTURAL_VOLATILITY,
	/**
	 * Creating edges of {@link CouplingCriteria#AVAILABILITY_CRITICALITY}
	 */
	AVAILABILITY_CRITICALITY,
	/**
	 * Creating edges of {@link CouplingCriteria#CONSISTENCY_CRITICALITY}
	 */
	CONSISTENCY_CRITICALITY,
	/**
	 * Creating edges of {@link CouplingCriteria#STORAGE_SIMILARITY}
	 */
	STORAGE_SIMILARITY,
	/**
	 * Creating edges of {@link CouplingCriteria#SECURITY_CRITICALITY}
	 */
	SECURITY_CRITICALITY,
	/**
	 * Finished creation
	 */
	CREATION_DONE;
}
