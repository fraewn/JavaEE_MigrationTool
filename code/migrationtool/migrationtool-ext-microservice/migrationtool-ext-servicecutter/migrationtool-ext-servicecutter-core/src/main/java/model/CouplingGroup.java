package model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;
import model.data.Instance;
import resolver.CriteriaScorer;

public class CouplingGroup {

	private String groupName;

	private Set<Edge> relatedEdges;

	private Set<Instance> relatedNodes;

	private CouplingCriteria criteria;

	private ArchitectureArtifact artifact;

	private Class<? extends CriteriaScorer> scorer;

	public CouplingGroup() {
		this.relatedEdges = new HashSet<>();
		this.relatedNodes = new HashSet<>();
	}

	public CouplingGroup(String groupName, Set<Edge> relatedEdges, CouplingCriteria criteria,
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
	public Set<Node> getOrigins() {
		return this.relatedEdges.stream().map(Edge::getFirstNode).collect(Collectors.toSet());
	}

	/**
	 * @return the destinations
	 */
	public Set<Node> getDestinations() {
		return this.relatedEdges.stream().map(Edge::getSecondNode).collect(Collectors.toSet());
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
	public Set<Edge> getRelatedEdges() {
		return this.relatedEdges;
	}

	/**
	 * @param relatedEdges the relatedEdges to set
	 */
	public void setRelatedEdges(Set<Edge> relatedEdges) {
		this.relatedEdges = relatedEdges;
	}

	/**
	 * @return the relatedNodes
	 */
	public Set<Instance> getRelatedNodes() {
		return this.relatedNodes;
	}

	/**
	 * @param relatedNodes the relatedNodes to set
	 */
	public void setRelatedNodes(Set<Instance> relatedNodes) {
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
