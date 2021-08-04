package model.artifacts;

import model.data.Instance;

/**
 * Software System Artifacts represent the analysis and design artifacts that
 * contain information about coupling criteria;
 */
public enum ArchitectureArtifact {

	/** classes representing entities */
	ENTITIES,
	/** relationships between entities */
	RELATIONSHIPS,
	/**
	 * Use cases describe the Semantic Proximity of a group of {@link Instance}.
	 * They can also be marked as latency critical which when results in Latency.
	 * Use cases are defined by the {@link Instance} read and written.
	 */
	USE_CASE,
	/**
	 * An Aggregate is a group of associated objects which are considered as one
	 * unit with regard to data changes.
	 */
	AGGREGATES,
	/**
	 * Shared owner group represents a person, role or department that is
	 * responsible for a group of {@link Instance}.
	 */
	SHARED_OWNER_GROUPS,
	/**
	 * A predefined service represents a service that already exists and therefore
	 * is harder or impossible to change.
	 */
	PREDEFINED_SERVICES,
	/**
	 * Separated security zones represent groups of {@link Instance} that should not
	 * be combined into a common service.
	 */
	SEPERATED_SECURITY_ZONES,
	/**
	 * A security access group represents a group of {@link Instance} that can be
	 * accessed by a particular security role .
	 */
	SECURITY_ACCESS_GROUPS,
	/**
	 * Criteria indicating divergent characteristics of {@link Instance}
	 */
	COMPATIBILITIES;
}
