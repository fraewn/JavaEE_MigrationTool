package model.serviceDefintion;

import java.util.Set;

import model.data.Instance;

/**
 * Class representing the relationship between two {@link Service}. This is the
 * published language
 */
public class ServiceRelation {

	/** ID of Service A */
	private String serviceIdA;
	/** ID of Service B */
	private String serviceIdB;
	/** Shared entites */
	private Set<Instance> sharedEntities;
	/** Relationship direction */
	private Direction direction;

	/**
	 * @return the serviceIdA
	 */
	public String getServiceIdA() {
		return this.serviceIdA;
	}

	/**
	 * @param serviceIdA the serviceIdA to set
	 */
	public void setServiceIdA(String serviceIdA) {
		this.serviceIdA = serviceIdA;
	}

	/**
	 * @return the serviceIdB
	 */
	public String getServiceIdB() {
		return this.serviceIdB;
	}

	/**
	 * @param serviceIdB the serviceIdB to set
	 */
	public void setServiceIdB(String serviceIdB) {
		this.serviceIdB = serviceIdB;
	}

	/**
	 * @return the sharedEntities
	 */
	public Set<Instance> getSharedEntities() {
		return this.sharedEntities;
	}

	/**
	 * @param sharedEntities the sharedEntities to set
	 */
	public void setSharedEntities(Set<Instance> sharedEntities) {
		this.sharedEntities = sharedEntities;
	}

	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return this.direction;
	}

	/**
	 * @param direction the direction to set
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
