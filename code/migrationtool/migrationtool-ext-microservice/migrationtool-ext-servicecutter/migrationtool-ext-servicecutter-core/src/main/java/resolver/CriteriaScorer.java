package resolver;

import java.util.Map;

import core.CouplingGroup;
import core.Edge;
import core.Graph;

public interface CriteriaScorer {

	Map<Edge, Double> getScores(CouplingGroup relatedGroup, Graph graph);

	Map<Edge, Double> normalize(Map<Edge, Double> values);
}
