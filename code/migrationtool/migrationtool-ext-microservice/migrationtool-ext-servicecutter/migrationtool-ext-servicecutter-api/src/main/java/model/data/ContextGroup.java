package model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Template Class to group {@link Instance} objects
 */
public class ContextGroup {

	private String name;
	@JsonIgnore
	private List<Instance> instances;

	public ContextGroup() {
		this.instances = new ArrayList<>();
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
	public List<Instance> getInstances() {
		return this.instances;
	}

	/**
	 * @param instances the instances to set
	 */
	public void setInstances(List<Instance> instances) {
		this.instances = instances;
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
}
