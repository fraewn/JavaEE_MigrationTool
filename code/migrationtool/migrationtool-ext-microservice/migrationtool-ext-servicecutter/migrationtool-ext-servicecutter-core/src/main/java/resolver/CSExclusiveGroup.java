package resolver;

import static resolver.Scores.MAX_SCORE;
import static resolver.Scores.MIN_SCORE;

import core.CouplingGroup;
import core.Edge;

public class CSExclusiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		addPenalityToEdges(Scores.getScore(MIN_SCORE), currentEdge, relatedGroup.getRelatedEdges(),
				relatedGroup.getCriteria());
		return Scores.getScore(MAX_SCORE);
	}
}
