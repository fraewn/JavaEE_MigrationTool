package resolver;

import static resolver.Scores.MAX_SCORE;

import core.CouplingGroup;
import core.Edge;

public class CSCohesiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		return Scores.getScore(MAX_SCORE);
	}

}
