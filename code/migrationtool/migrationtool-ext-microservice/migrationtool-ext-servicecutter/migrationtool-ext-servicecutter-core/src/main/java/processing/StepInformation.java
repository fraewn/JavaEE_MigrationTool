package processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import model.CouplingGroup;
import model.Edge;
import model.artifacts.ArchitectureArtifact;
import model.criteria.CouplingCriteria;
import resolver.CriteriaScorer;

public class StepInformation {

	private List<CouplingGroup> groups;

	public StepInformation() {
		this.groups = new ArrayList<>();
	}

	public StepInformation(List<CouplingGroup> groups) {
		this();
		this.groups.addAll(groups);
	}

	public StepInformation(Map<String, Set<Edge>> relatedEdges, ArchitectureArtifact artifact,
			CouplingCriteria criteria, Class<? extends CriteriaScorer> scorer) {
		this();
		for (Entry<String, Set<Edge>> e : relatedEdges.entrySet()) {
			CouplingGroup group = new CouplingGroup(e.getKey(), e.getValue(), criteria, artifact, scorer);
			this.groups.add(group);
		}
	}

	public StepInformation(String groupName, Set<Edge> relatedEdges, ArchitectureArtifact artifact,
			CouplingCriteria criteria, Class<? extends CriteriaScorer> scorer) {
		this();
		CouplingGroup group = new CouplingGroup(groupName, relatedEdges, criteria, artifact, scorer);
		this.groups.add(group);
	}

	/**
	 * @return the groups
	 */
	public List<CouplingGroup> getGroups() {
		return this.groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<CouplingGroup> groups) {
		this.groups = groups;
	}
}
