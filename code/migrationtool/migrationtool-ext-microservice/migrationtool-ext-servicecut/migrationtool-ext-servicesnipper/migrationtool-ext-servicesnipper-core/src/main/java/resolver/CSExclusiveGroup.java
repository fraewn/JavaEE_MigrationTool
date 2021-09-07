package resolver;

import static utils.DefinitionDomain.MAX_SCORE;
import static utils.DefinitionDomain.MIN_SCORE;

import model.CouplingGroup;
import model.EdgeWrapper;
import utils.DefinitionDomain;

/**
 * Scorer for an exclusive group. All direct connections to this group, gets a
 * penalty
 */
public class CSExclusiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup) {
		return DefinitionDomain.getScore(MAX_SCORE);
	}

	@Override
	public void afterCalcScore(CouplingGroup relatedGroup) {
		addPenaltyToEdges(DefinitionDomain.getScore(MIN_SCORE), relatedGroup, relatedGroup.getCriteria());
	}
}
