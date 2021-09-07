package model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;
import resolver.CriteriaScorer;

/**
 * A coupling group is a wrapper for all related data to the edge creation
 */
public class CouplingGroup {

	/** name of this coupling group */
	private String groupName;
	/** all edges of this coupling group */
	private Set<EdgeWrapper> relatedEdges;
	/** all involved nodes of this coupling group */
	private Set<String> relatedNodes;
	/** criteria of this coupling group */
	private CouplingCriteria criteria;
	/** artifact of this coupling group */
	private ArchitectureArtifact artifact;
	/** scorer of this coupling group */
	private Class<? extends CriteriaScorer> scorer;

	public CouplingGroup() {
		this.relatedEdges = new HashSet<>();
		this.relatedNodes = new HashSet<>();
	}

	public CouplingGroup(String groupName, Set<EdgeWrapper> relatedEdges, CouplingCriteria criteria,
			ArchitectureArtifact artifact, Class<? extends CriteriaScorer> scorer) {
		this();
		this.groupName = groupName;
		this.criteria = criteria;
		this.artifact = artifact;
		this.relatedEdges.addAll(relatedEdges);
		this.scorer = scorer;
	}

	/**
	 * @return the origins
	 */
	public Set<String> getOrigins() {
		return this.relatedEdges.stream().map(EdgeWrapper::getFirstNode).collect(Collectors.toSet());
	}

	/**
	 * @return the destinations
	 */
	public Set<String> getDestinations() {
		return this.relatedEdges.stream().map(EdgeWrapper::getSecondNode).collect(Collectors.toSet());
	}

	/**
	 * @return the criteria
	 */
	public CouplingCriteria getCriteria() {
		return this.criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(CouplingCriteria criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the artifact
	 */
	public ArchitectureArtifact getArtifact() {
		return this.artifact;
	}

	/**
	 * @param artifact the artifact to set
	 */
	public void setArtifact(ArchitectureArtifact artifact) {
		this.artifact = artifact;
	}

	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return this.groupName;
	}

	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	/**
	 * @return the relatedEdges
	 */
	public Set<EdgeWrapper> getRelatedEdges() {
		return this.relatedEdges;
	}

	/**
	 * @param relatedEdges the relatedEdges to set
	 */
	public void setRelatedEdges(Set<EdgeWrapper> relatedEdges) {
		this.relatedEdges = relatedEdges;
	}

	/**
	 * @return the relatedNodes
	 */
	public Set<String> getRelatedNodes() {
		return this.relatedNodes;
	}

	/**
	 * @param relatedNodes the relatedNodes to set
	 */
	public void setRelatedNodes(Set<String> relatedNodes) {
		this.relatedNodes = relatedNodes;
	}

	/**
	 * @return the scorer
	 */
	public Class<? extends CriteriaScorer> getScorer() {
		return this.scorer;
	}

	/**
	 * @param scorer the scorer to set
	 */
	public void setScorer(Class<? extends CriteriaScorer> scorer) {
		this.scorer = scorer;
	}
}
