package model.erm;

/**
 * Describes a relation between two entities
 */
public class EntityRelation {

	/** entity origin */
	private Entity origin;
	/** entity destination */
	private Entity destination;
	/** relationship type */
	private RelationType type;

	public EntityRelation() {

	}

	public EntityRelation(Entity origin, Entity destination, RelationType type) {
		this.origin = origin;
		this.destination = destination;
		this.type = type;
	}

	/**
	 * @return the origin
	 */
	public Entity getOrigin() {
		return this.origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(Entity origin) {
		this.origin = origin;
	}

	/**
	 * @return the destination
	 */
	public Entity getDestination() {
		return this.destination;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Entity destination) {
		this.destination = destination;
	}

	/**
	 * @return the type
	 */
	public RelationType getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(RelationType type) {
		this.type = type;
	}
}
