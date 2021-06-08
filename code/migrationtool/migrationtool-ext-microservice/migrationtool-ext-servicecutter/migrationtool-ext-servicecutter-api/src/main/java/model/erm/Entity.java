package model.erm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Class representing an entity object
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Entity implements Comparator<Entity>, Comparable<Entity> {

	/** name of entity */
	private String name;
	/** attributes of entity */
	private List<String> attributes;

	public Entity() {
		this.attributes = new ArrayList<>();
	}

	public Entity(String name) {
		this();
		this.name = name;
	}

	/**
	 * @return the attributes
	 */
	public List<String> getAttributes() {
		return this.attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Entity other = (Entity) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.attributes == null) ? (other.attributes != null) : !this.attributes.equals(other.attributes)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.name != null ? this.name.hashCode() : 0);
		hash = (53 * hash) + (this.attributes != null ? this.attributes.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(Entity i1, Entity i2) {
		return i1.getName().compareTo(i2.getName());
	}

	@Override
	public int compareTo(Entity o) {
		return getName().compareTo(o.getName());
	}
}
