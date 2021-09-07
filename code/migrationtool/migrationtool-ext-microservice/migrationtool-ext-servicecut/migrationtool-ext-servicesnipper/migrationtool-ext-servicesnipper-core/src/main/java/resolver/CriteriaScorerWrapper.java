package resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.CouplingGroup;
import model.EdgeWrapper;
import model.Graph;
import model.criteria.CouplingCriteria;

/**
 * Base class of all scorers to define shared methods
 */
public abstract class CriteriaScorerWrapper implements CriteriaScorer {

	/** LOGGER */
	protected static final Logger LOG = LogManager.getLogger();
	/** Reference to the graph and the model */
	protected Graph graph;

	@Override
	public Map<EdgeWrapper, Double> getScores(CouplingGroup relatedGroup, Graph graph) {
		this.graph = graph;
		Map<EdgeWrapper, Double> values = new HashMap<>();
		for (EdgeWrapper edge : relatedGroup.getRelatedEdges()) {
			if (this.graph.hasEdge(edge)) {
				double score = getScore(edge, relatedGroup);
				values.put(edge, Double.valueOf(score));
				LOG.info("{} new score added {} ({})", edge, score, relatedGroup.getGroupName());
			}
		}
		afterCalcScore(relatedGroup);
		return values;
	}

	@Override
	public Map<EdgeWrapper, Double> normalize(Map<EdgeWrapper, Double> values) {
		// Default; not used
		return values;
	}

	/**
	 * Adds a penalty to all edges which are not related to the group, but are
	 * direct neighbors of involved vertices
	 *
	 * @param penalty      penalty value
	 * @param relatedGroup related group
	 * @param criteria     coupling criteria
	 */
	protected void addPenaltyToEdges(double penalty, CouplingGroup relatedGroup, CouplingCriteria criteria) {
		for (String node : relatedGroup.getRelatedNodes()) {
			for (EdgeWrapper edge : this.graph.getDirectNeighbours(node)) {
				if (!relatedGroup.getRelatedEdges().contains(edge)) {
					LOG.info("{} new penalty added {} ({}/{})", edge, penalty, relatedGroup.getGroupName(), criteria);
					this.graph.addNewScore(edge, criteria, penalty);
				}
			}
		}
	}

	/**
	 * Get all groups related to a specific coupling criteria
	 *
	 * @param criteria coupling criteria
	 * @return all related coupling groups
	 */
	protected List<CouplingGroup> getAllGroupsOfCriteria(CouplingCriteria criteria) {
		List<CouplingGroup> res = new ArrayList<>();
		for (CouplingGroup group : this.graph.getRelatedGroups()) {
			if (group.getCriteria().equals(criteria)) {
				res.add(group);
			}
		}
		return res;
	}

	/**
	 * Calculates the score for the current edge
	 *
	 * @param currentEdge  current edge
	 * @param relatedGroup related group
	 * @return value of edge
	 */
	public abstract double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup);

	/**
	 * Execute a task after the calculation of each edge in the related group
	 *
	 * @param relatedGroup related group
	 */
	public void afterCalcScore(CouplingGroup relatedGroup) {
		// Nothing
	}
}
