package model.serviceDefintion;

import java.util.Set;

import model.data.Instance;

public class ServiceRelation {

	private String serviceIdA;

	private String serviceIdB;

	private Set<Instance> sharedEntities;

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
