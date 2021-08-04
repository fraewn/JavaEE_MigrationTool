package model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Primarly describe the Semantic Proximity of a group of {@link Instance}.
 */
@JsonInclude(value = Include.NON_NULL)
public class UseCase {

	/** name of this use case */
	@NotBlank
	private String name;
	/** name of this use case */
	private List<Instance> input;
	/** name of this use case */
	private List<Instance> persistenceChanges;
	/** name of this use case */
	private Boolean isLatencyCritical;

	public UseCase() {
		this.input = new ArrayList<>();
		this.persistenceChanges = new ArrayList<>();
	}

	public UseCase(@NotBlank String name, String input, String persistence, Boolean latency) {
		this();
		this.name = name;
		setInputAsString(input);
		setPersistenceChangesAsString(persistence);
		this.isLatencyCritical = latency;
	}

	public UseCase(UseCase useCase) {
		this();
		this.name = useCase.getName();
		if (useCase.getInputAsString() != null) {
			setInputAsString(useCase.getInputAsString());
		}
		if (useCase.getPersistenceChangesAsString() != null) {
			setPersistenceChangesAsString(useCase.getPersistenceChangesAsString());
		}
		this.isLatencyCritical = useCase.isLatencyCritical();
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
	 * @return the input
	 */
	public List<Instance> getInput() {
		return this.input;
	}

	/**
	 * @return the input
	 */
	@JsonGetter(value = "input")
	public String getInputAsString() {
		return this.input.stream().map(Instance::getQualifiedName).collect(Collectors.joining(","));
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(List<Instance> input) {
		this.input = input;
	}

	/**
	 * @param input the input to set
	 */
	@JsonSetter(value = "input")
	public void setInputAsString(String input) {
		if ((input != null) && !input.equals("")) {
			String[] cases = input.split(",");
			this.input.clear();
			for (String s : cases) {
				this.input.add(new Instance(s));
			}
		}
	}

	/**
	 * @return the persistenceChanges
	 */
	public List<Instance> getPersistenceChanges() {
		return this.persistenceChanges;
	}

	/**
	 * @return the persistenceChanges
	 */
	@JsonGetter(value = "persistenceChanges")
	public String getPersistenceChangesAsString() {
		return this.persistenceChanges.stream().map(Instance::getQualifiedName).collect(Collectors.joining(","));
	}

	/**
	 * @param persistenceChanges the persistenceChanges to set
	 */
	public void setPersistenceChanges(List<Instance> persistenceChanges) {
		this.persistenceChanges = persistenceChanges;
	}

	/**
	 * @param persistenceChanges the persistenceChanges to set
	 */
	@JsonSetter(value = "persistenceChanges")
	public void setPersistenceChangesAsString(String persistenceChanges) {
		if ((persistenceChanges != null) && !persistenceChanges.equals("")) {
			String[] cases = persistenceChanges.split(",");
			this.persistenceChanges.clear();
			for (String s : cases) {
				this.persistenceChanges.add(new Instance(s));
			}
		}
	}

	/**
	 * @return the isLatencyCritical
	 */
	public Boolean isLatencyCritical() {
		return this.isLatencyCritical;
	}

	/**
	 * @param isLatencyCritical the isLatencyCritical to set
	 */
	public void setLatencyCritical(Boolean isLatencyCritical) {
		this.isLatencyCritical = isLatencyCritical;
	}
}
