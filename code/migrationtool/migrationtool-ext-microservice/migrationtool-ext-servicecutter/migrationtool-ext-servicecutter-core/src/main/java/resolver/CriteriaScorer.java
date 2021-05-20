package resolver;

import core.CouplingGroup;
import core.Graph;

public interface CriteriaScorer {

	void getScores(CouplingGroup relatedGroup, Graph graph);
}
