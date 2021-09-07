package model.erm;

import java.util.Comparator;

/**
 * Describes a relation between two entities
 */
public class EntityRelation implements Comparator<EntityRelation>, Comparable<EntityRelation> {

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

	public boolean equalsIgnoreType(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final EntityRelation other = (EntityRelation) obj;
		if ((this.origin == null) ? (other.origin != null) : !this.origin.equals(other.origin)) {
			return false;
		}
		if ((this.destination == null) ? (other.destination != null) : !this.destination.equals(other.destination)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final EntityRelation other = (EntityRelation) obj;
		if ((this.origin == null) ? (other.origin != null) : !this.origin.equals(other.origin)) {
			return false;
		}
		if ((this.destination == null) ? (other.destination != null) : !this.destination.equals(other.destination)) {
			return false;
		}
		if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.origin != null ? this.origin.hashCode() : 0);
		hash = (53 * hash) + (this.destination != null ? this.destination.hashCode() : 0);
		hash = (53 * hash) + (this.type != null ? this.type.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(EntityRelation i1, EntityRelation i2) {
		return i1.getOrigin().compareTo(i2.getOrigin()) + i1.getDestination().compareTo(i2.getDestination());
	}

	@Override
	public int compareTo(EntityRelation o) {
		return getOrigin().compareTo(o.getOrigin()) + getDestination().compareTo(o.getDestination());
	}
}
