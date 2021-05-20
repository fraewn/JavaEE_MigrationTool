package model.criteria;

import static model.criteria.CouplingGroup.COHESIVENESS;
import static model.criteria.CouplingGroup.COMPATIBILITY;
import static model.criteria.CouplingGroup.CONSTRAINTS;

import model.data.Instance;
import model.data.UseCase;
import processing.ProcessAutomate;

/**
 * Coupling Criteria
 */
public enum CouplingCriteria implements ProcessAutomate<CouplingCriteria> {

	/**
	 * {@link Instance} that belong to the same identity and therefore share a
	 * common lifecycle.
	 */
	IDENTITY_LIFECYCLE("CC-01", COHESIVENESS) {
		@Override
		public CouplingCriteria nextStep() {
			return SEMANTIC_PROXIMITY;
		}
	},
	/**
	 * Two {@link Instance} are semantically proximate when they have a semantic
	 * connection given by the business domain. {@link UseCase}
	 */
	SEMANTIC_PROXIMITY("CC-02", COHESIVENESS) {
		@Override
		public CouplingCriteria nextStep() {
			return SHARED_OWNER;
		}
	},
	/**
	 * Same Entity is responsible for a group of {@link Instance}. Service
	 * decomposition should try to keep entities with the same responsible role
	 * together while not mixing entities with different responsible instances in
	 * one service.
	 */
	SHARED_OWNER("CC-03", COHESIVENESS) {
		@Override
		public CouplingCriteria nextStep() {
			return STRUCTURAL_VOLATILITY;
		}
	},
	/**
	 * How often change requests need to be implemented affecting {@link Instance}.
	 */
	STRUCTURAL_VOLATILITY("CC-04", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return LATENCY;
		}
	},
	/**
	 * Groups of {@link Instance} with high performance requirements for a specific
	 * user request. These {@link Instance} should be modelled in the same service
	 * to avoid remote calls.
	 */
	LATENCY("CC-05", COHESIVENESS) {
		@Override
		public CouplingCriteria nextStep() {
			return CONSISTENCY_CRITICALITY;
		}
	},
	/**
	 * Some data such as financial records loses its value in case of
	 * inconsistencies while other data is more tolerant to inconsistencies.
	 */
	CONSISTENCY_CRITICALITY("CC-06", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return AVAILABILITY_CRITICALITY;
		}
	},
	/**
	 * {@link Instance} have varying availability constraints. Some are critical
	 * while others can be unavailable for some time. As providing high availability
	 * comes at a cost, {@link Instance} classified with different characteristics
	 * should not be composed in the same service
	 */
	AVAILABILITY_CRITICALITY("CC-07", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return CONTENT_VOLATILITY;
		}
	},
	/**
	 * A {@link Instance} can be classified by its volatility which defines how
	 * frequent it is updated. Highly volatile and more stable {@link Instance}
	 * should be composed in different services.
	 */
	CONTENT_VOLATILITY("CC-08", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return CONSISTENCY_CONSTRAINT;
		}
	},
	/**
	 * A group of {@link Instance} that have a dependent state and therefore need to
	 * be kept consistent to each other.
	 */
	CONSISTENCY_CONSTRAINT("CC-09", CONSTRAINTS) {
		@Override
		public CouplingCriteria nextStep() {
			return STORAGE_SIMILARITY;
		}
	},
	/** Storage that is required to persist all instances of a {@link Instance}. */
	STORAGE_SIMILARITY("CC-10", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return PREDEFINED_SERVICE_CONSTRAINT;
		}
	},
	/**
	 * There might be the following reasons why some {@link Instance} forcefully
	 * need to be modelled in the same service: Technological optimizations or
	 * Legacy systems
	 */
	PREDEFINED_SERVICE_CONSTRAINT("CC-11", CONSTRAINTS) {
		@Override
		public CouplingCriteria nextStep() {
			return SECURITY_CONTEXUALITY;
		}
	},
	/**
	 * A security role is allowed to see or process a group of {@link Instance}.
	 * Mixing security contexts in one service complicates authentication and
	 * authorization implementations.
	 */
	SECURITY_CONTEXUALITY("CC-12", COHESIVENESS) {
		@Override
		public CouplingCriteria nextStep() {
			return SECURITY_CRITICALITY;
		}
	},
	/**
	 * Criticality of an {@link Instance} in case of data loss or a privacy
	 * violation. Represents the reputational or financial damage when the
	 * information is disclosed to unauthorized parties. As high security
	 * criticality comes at a cost, {@link Instance} classified with different
	 * characteristics should not be composed in the same service.
	 */
	SECURITY_CRITICALITY("CC-13", COMPATIBILITY) {
		@Override
		public CouplingCriteria nextStep() {
			return SECURITY_CONSTRAINT;
		}
	},
	/**
	 * Groups of {@link Instance} are semantically related but must not reside in
	 * the same service in order to satisfy information security requirements. This
	 * restriction can be established by an external party such as a certification
	 * authority or an internal design team.
	 */
	SECURITY_CONSTRAINT("CC-14", CONSTRAINTS) {
		@Override
		public CouplingCriteria nextStep() {
			return null;
		}
	};

	private String code;

	private CouplingGroup group;

	private CouplingCriteria(String code, CouplingGroup group) {
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
