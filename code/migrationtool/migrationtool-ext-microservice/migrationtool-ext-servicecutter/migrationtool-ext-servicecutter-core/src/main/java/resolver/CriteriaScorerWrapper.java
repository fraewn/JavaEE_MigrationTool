package resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import core.CouplingGroup;
import core.Edge;
import core.Graph;
import core.Node;
import model.criteria.CouplingCriteria;

public abstract class CriteriaScorerWrapper implements CriteriaScorer {

	protected Graph graph;

	@Override
	public void getScores(CouplingGroup relatedGroup, Graph graph) {
		this.graph = graph;
		for (Edge edge : relatedGroup.getRelatedEdges()) {
			if (this.graph.getEdges().containsKey(edge)) {
				double score = getScore(edge, relatedGroup);
				this.graph.addNewScore(edge, Double.valueOf(score));
			}
		}
	}

	protected void addPenalityToEdges(double penality, Edge currentEdge, Set<Edge> relatedEdges) {
		addPenalityToEdges(penality, currentEdge, relatedEdges, null);
	}

	protected void addPenalityToEdges(double penality, Edge currentEdge, Set<Edge> relatedEdges,
			Set<Edge> correspondingEdges) {
		Node origin = currentEdge.getFirstNode();
		for (Edge edge : this.graph.getEdges().keySet()) {
			if (edge.getFirstNode().equals(origin)) {
				// check same group
				if (!relatedEdges.contains(edge)) {
					if (correspondingEdges == null) {
						// all edges are relevant
						this.graph.addNewScore(edge, penality);
					} else if (correspondingEdges.contains(edge)) {
						// check only specific comparison group
						this.graph.addNewScore(edge, penality);
					}
				}
			}
		}
	}

	protected CouplingGroup getGroupOfEdgeAndCriteria(Edge currentEdge, CouplingCriteria criteria) {
		CouplingGroup ownGroup = null;
		for (CouplingGroup group : this.graph.getRelatedGroups()) {
			if (group.getCriteria().equals(criteria)) {
				if (group.getRelatedEdges().contains(currentEdge)) {
					ownGroup = group;
					break;
				}
			}
		}
		return ownGroup;
	}

	protected List<CouplingGroup> getExclusiveGroupOfEdgeAndCriteria(Edge currentEdge, CouplingCriteria criteria) {
		List<CouplingGroup> res = new ArrayList<>();
		for (CouplingGroup group : this.graph.getRelatedGroups()) {
			if (group.getCriteria().equals(criteria)) {
				if (!group.getRelatedEdges().contains(currentEdge)) {
					res.add(group);
				}
			}
		}
		return res;
	}

	public abstract double getScore(Edge currentEdge, CouplingGroup relatedGroup);
}
