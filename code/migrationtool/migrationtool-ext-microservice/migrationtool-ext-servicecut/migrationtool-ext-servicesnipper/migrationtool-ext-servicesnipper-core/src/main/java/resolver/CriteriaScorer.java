package resolver;

import java.util.Map;

import model.CouplingGroup;
import model.EdgeWrapper;
import model.Graph;

/**
 * A possible calculation of the weight for an edge
 */
public interface CriteriaScorer {

	/**
	 * Calculate the edge weight
	 *
	 * @param relatedGroup analyzed group
	 * @param graph        current graph
	 * @return values
	 */
	Map<EdgeWrapper, Double> getScores(CouplingGroup relatedGroup, Graph graph);

	/**
	 * Normalize the values
	 *
	 * @param values current values
	 * @return final values
	 */
	Map<EdgeWrapper, Double> normalize(Map<EdgeWrapper, Double> values);
}
