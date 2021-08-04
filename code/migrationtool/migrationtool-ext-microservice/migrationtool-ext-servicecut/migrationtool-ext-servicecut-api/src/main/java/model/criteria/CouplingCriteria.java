package model.criteria;

import static model.criteria.CouplingGroup.COHESIVENESS;
import static model.criteria.CouplingGroup.COMPATIBILITY;
import static model.criteria.CouplingGroup.CONSTRAINTS;

import model.data.Instance;
import model.data.UseCase;

/**
 * Coupling Criteria
 */
public enum CouplingCriteria {

	/**
	 * {@link Instance} that belong to the same identity and therefore share a
	 * common lifecycle.
	 */
	IDENTITY_LIFECYCLE("CC_01", COHESIVENESS),

	/**
	 * Two {@link Instance} are semantically proximate when they have a semantic
	 * connection given by the business domain. {@link UseCase}
	 */
	SEMANTIC_PROXIMITY("CC_02", COHESIVENESS),

	/**
	 * Same Entity is responsible for a group of {@link Instance}. Service
	 * decomposition should try to keep entities with the same responsible role
	 * together while not mixing entities with different responsible instances in
	 * one service.
	 */
	SHARED_OWNER("CC_03", COHESIVENESS),

	/**
	 * Groups of {@link Instance} with high performance requirements for a specific
	 * user request. These {@link Instance} should be modelled in the same service
	 * to avoid remote calls.
	 */
	LATENCY("CC_04", COHESIVENESS),

	/**
	 * A security role is allowed to see or process a group of {@link Instance}.
	 * Mixing security contexts in one service complicates authentication and
	 * authorization implementations.
	 */
	SECURITY_CONTEXUALITY("CC_05", COHESIVENESS),

	/**
	 * A group of {@link Instance} that have a dependent state and therefore need to
	 * be kept consistent to each other.
	 */
	CONSISTENCY_CONSTRAINT("CC_06", CONSTRAINTS),

	/**
	 * Groups of {@link Instance} are semantically related but must not reside in
	 * the same service in order to satisfy information security requirements. This
	 * restriction can be established by an external party such as a certification
	 * authority or an internal design team.
	 */
	SECURITY_CONSTRAINT("CC_07", CONSTRAINTS),

	/**
	 * There might be the following reasons why some {@link Instance} forcefully
	 * need to be modelled in the same service: Technological optimizations or
	 * Legacy systems
	 */
	PREDEFINED_SERVICE_CONSTRAINT("CC_08", CONSTRAINTS),

	/**
	 * How often change requests need to be implemented affecting {@link Instance}.
	 */
	STRUCTURAL_VOLATILITY("CC_09", COMPATIBILITY),

	/**
	 * A {@link Instance} can be classified by its volatility which defines how
	 * frequent it is updated. Highly volatile and more stable {@link Instance}
	 * should be composed in different services.
	 */
	CONTENT_VOLATILITY("CC_10", COMPATIBILITY),

	/**
	 * Some data such as financial records loses its value in case of
	 * inconsistencies while other data is more tolerant to inconsistencies.
	 */
	CONSISTENCY_CRITICALITY("CC_11", COMPATIBILITY),

	/**
	 * {@link Instance} have varying availability constraints. Some are critical
	 * while others can be unavailable for some time. As providing high availability
	 * comes at a cost, {@link Instance} classified with different characteristics
	 * should not be composed in the same service
	 */
	AVAILABILITY_CRITICALITY("CC_12", COMPATIBILITY),

	/** Storage that is required to persist all instances of a {@link Instance}. */
	STORAGE_SIMILARITY("CC_13", COMPATIBILITY),

	/**
	 * Criticality of an {@link Instance} in case of data loss or a privacy
	 * violation. Represents the reputational or financial damage when the
	 * information is disclosed to unauthorized parties. As high security
	 * criticality comes at a cost, {@link Instance} classified with different
	 * characteristics should not be composed in the same service.
	 */
	SECURITY_CRITICALITY("CC_14", COMPATIBILITY);

	/** Coupling criteria id */
	private String code;
	/** Related group */
	private CouplingGroup group;

	CouplingCriteria(String code, CouplingGroup group) {
		this.code = code;
		this.group = group;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * @return the group
	 */
	public CouplingGroup getGroup() {
		return this.group;
	}
}
