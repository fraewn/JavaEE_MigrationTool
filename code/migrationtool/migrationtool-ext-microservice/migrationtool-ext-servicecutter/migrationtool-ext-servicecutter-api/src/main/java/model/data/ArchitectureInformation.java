package model.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;

/**
 * Software System Artifacts
 */
public class ArchitectureInformation {

	/** list of use cases alias transactions */
	private List<UseCase> useCases;
	/** software system artifacts */
	private Map<ArchitectureArtifact, List<ContextGroup>> criteria;
	/** criteria of the group compatibility */
	private Map<CouplingCriteria, List<Characteristic>> compatibilities;

	public ArchitectureInformation() {
		this.useCases = new ArrayList<>();
		this.criteria = new HashMap<>();
		this.compatibilities = new HashMap<>();
	}

	/**
	 * @return the useCases
	 */
	public List<UseCase> getUseCases() {
		return this.useCases;
	}

	/**
	 * @param useCases the useCases to set
	 */
	public void setUseCases(List<UseCase> useCases) {
		this.useCases = useCases;
	}

	/**
	 * @return the criteria
	 */
	public Map<ArchitectureArtifact, List<ContextGroup>> getCriteria() {
		return this.criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(Map<ArchitectureArtifact, List<ContextGroup>> criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the compatibilities
	 */
	public Map<CouplingCriteria, List<Characteristic>> getCompatibilities() {
		return this.compatibilities;
	}

	/**
	 * @param compatibilities the compatibilities to set
	 */
	public void setCompatibilities(Map<CouplingCriteria, List<Characteristic>> compatibilities) {
		this.compatibilities = compatibilities;
	}
}
