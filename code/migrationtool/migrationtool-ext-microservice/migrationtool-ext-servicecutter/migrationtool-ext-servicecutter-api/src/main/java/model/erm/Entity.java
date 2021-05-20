package model.erm;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Class representing an entity object
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "name")
public class Entity {

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
}
