package model.data;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Basic class representing an instance
 */
public class Instance implements Comparator<Instance>, Comparable<Instance> {

	/** name of this instance */
	@JsonIgnore
	private String name;
	/** parent object/class */
	@JsonIgnore
	private String context;

	public Instance() {

	}

	public Instance(String name, String context) {
		this.name = name;
		this.context = context;
	}

	public Instance(String qualifiedName) {
		setQualifiedName(qualifiedName);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return this.context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}

	@JsonGetter(value = "qualifiedName")
	public String getQualifiedName() {
		if ((this.name == null) || (this.context == null)) {
			return null;
		}
		return (this.context + "." + this.name);
	}

	@JsonSetter(value = "qualifiedName")
	public void setQualifiedName(String qualifiedName) {
		if ((qualifiedName != null) && qualifiedName.contains(".")) {
			int z = qualifiedName.lastIndexOf(".");
			setContext(qualifiedName.substring(0, z));
			setName(qualifiedName.substring(z + 1));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Instance other = (Instance) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.context == null) ? (other.context != null) : !this.context.equals(other.context)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.name != null ? this.name.hashCode() : 0);
		hash = (53 * hash) + (this.context != null ? this.context.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(Instance i1, Instance i2) {
		return i1.getQualifiedName().compareTo(i2.getQualifiedName());
	}

	@Override
	public int compareTo(Instance o) {
		return getQualifiedName().compareTo(o.getQualifiedName());
	}
}
