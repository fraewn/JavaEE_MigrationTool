package model.serviceDefintion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import model.data.Instance;

public class Service {

	private String name;
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
}
