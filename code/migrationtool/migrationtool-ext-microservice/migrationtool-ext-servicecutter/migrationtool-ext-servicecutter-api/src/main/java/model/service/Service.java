package model.service;

import java.util.HashSet;
import java.util.Set;

import model.data.Instance;

public class Service {

	private String name;

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
	public Set<Instance> getInstances() {
		return this.instances;
	}

	/**
	 * @param instances the instances to set
	 */
	public void setInstances(Set<Instance> instances) {
		this.instances = instances;
	}
}
