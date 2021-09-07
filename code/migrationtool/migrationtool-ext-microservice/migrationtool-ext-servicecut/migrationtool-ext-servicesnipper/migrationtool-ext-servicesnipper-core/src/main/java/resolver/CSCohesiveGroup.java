package resolver;

import static utils.DefinitionDomain.MAX_SCORE;

import model.CouplingGroup;
import model.EdgeWrapper;
import utils.DefinitionDomain;

/**
 * Scorer for criteria of type cohesiveness
 * {@link model.criteria.CouplingGroup#COHESIVENESS}
 */
public class CSCohesiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup) {
		return DefinitionDomain.getScore(MAX_SCORE);
	}
}
