package model.serviceDefintion;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import model.data.Instance;

/**
 * Class representing the definition of a microservice
 */
public class Service implements Comparator<Service>, Comparable<Service> {

	/** name of the service */
	private String name;
	/** all related instances to the service */
	@JsonIgnore
	private Set<Instance> instances;

	public Service() {
		this.instances = new HashSet<>();
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
	 * @return the instances
	 */
	@JsonGetter(value = "elements")
	public List<String> getInputAsString() {
		return this.instances.stream().map(Instance::getQualifiedName).sorted().collect(Collectors.toList());
	}

	/**
	 * @param input the instances to set
	 */
	@JsonSetter(value = "elements")
	public void setInputAsString(List<String> input) {
		if ((input != null) && !input.isEmpty()) {
			this.instances.clear();
			for (String s : input) {
				this.instances.add(new Instance(s));
			}
		}
	}

	/**
	 * @return the instances
	 */
	public Set<Instance> getInstances() {
		return this.instances;
	}

	/**
	 * @param instances the instances to set
	 */
	public void setInstances(Set<Instance> instances) {
		this.instances = instances;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final Service other = (Service) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.instances == null) ? (other.instances != null) : !this.instances.equals(other.instances)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(Service i1, Service i2) {
		return i1.getName().compareTo(i2.getName());
	}

	@Override
	public int compareTo(Service o) {
		return getName().compareTo(o.getName());
	}
}
