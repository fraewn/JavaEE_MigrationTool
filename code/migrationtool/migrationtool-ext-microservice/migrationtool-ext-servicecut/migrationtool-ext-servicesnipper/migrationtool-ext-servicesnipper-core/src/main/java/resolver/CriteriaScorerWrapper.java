package resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.CouplingGroup;
import model.Edge;
import model.Graph;
import model.Node;
import model.criteria.CouplingCriteria;

public abstract class CriteriaScorerWrapper implements CriteriaScorer {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	protected Graph graph;

	@Override
	public Map<Edge, Double> getScores(CouplingGroup relatedGroup, Graph graph) {
		this.graph = graph;
		Map<Edge, Double> values = new HashMap<>();
		for (Edge edge : relatedGroup.getRelatedEdges()) {
			if (this.graph.hasEdge(edge)) {
				double score = getScore(edge, relatedGroup);
				values.put(edge, Double.valueOf(score));
				LOG.info(edge + " new score added " + score + " (" + relatedGroup.getGroupName() + ") ");
			}
		}
		return values;
	}

	@Override
	public Map<Edge, Double> normalize(Map<Edge, Double> values) {
		// Default not used
		return values;
	}

	protected void addPenalityToEdges(double penality, Edge currentEdge, Set<Edge> relatedEdges,
			CouplingCriteria criteria) {
		addPenalityToEdges(penality, currentEdge, relatedEdges, null, criteria);
	}

	protected void addPenalityToEdges(double penality, Edge currentEdge, Set<Edge> relatedEdges,
			Set<Edge> correspondingEdges, CouplingCriteria criteria) {
		Node origin = currentEdge.getFirstNode();
		for (Edge edge : this.graph.getAllEdges()) {
			if (edge.getFirstNode().equals(origin) || edge.getSecondNode().equals(origin)) {
				// check same group
				if (!relatedEdges.contains(edge)) {
					if (correspondingEdges == null) {
						// all edges are relevant
						LOG.info(edge + " new score added " + penality + " () ");
						this.graph.addNewScore(edge, criteria, penality);
					} else if (correspondingEdges.contains(edge)) {
						// check only specific comparison group
						LOG.info(edge + " new score added " + penality + " () ");
						this.graph.addNewScore(edge, criteria, penality);
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
