package model.criteria;

import model.data.Instance;
import model.priorities.Priorities;

/**
 * Groups of the Coupling Criteria Catalog
 */
public enum CouplingGroup {
	/**
	 * Criteria describing certain common properties of mutually related
	 * {@link Instance} that justify why these {@link Instance} should belong to the
	 * same service
	 */
	COHESIVENESS(Priorities.RELEVANT),
	/**
	 * Criteria indicating divergent characteristics of {@link Instance}. A service
	 * should not contain {@link Instance} with incompatible characteristics.
	 */
	COMPATIBILITY(Priorities.LOW),
	/**
	 * Criteria specifying high-impact requirements that enforce that certain groups
	 * of {@link Instance} a) must jointly constitute a dedicated service or b) must
	 * be distributed amongst different services
	 */
	CONSTRAINTS(Priorities.RELEVANT);

	private Priorities defaultPrio;

	private CouplingGroup(Priorities defaultPrio) {
		this.defaultPrio = defaultPrio;
	}

	/**
	 * @return the defaultPrio
	 */
	public Priorities getDefaultPrio() {
		return this.defaultPrio;
	}
}