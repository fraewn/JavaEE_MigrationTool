package resolver;

import java.util.Map;

import model.CouplingGroup;
import model.Edge;
import model.Graph;

public interface CriteriaScorer {

	Map<Edge, Double> getScores(CouplingGroup relatedGroup, Graph graph);

	Map<Edge, Double> normalize(Map<Edge, Double> values);
}
