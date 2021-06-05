package resolver;

import static utils.DefinitionDomain.MAX_SCORE;

import model.CouplingGroup;
import model.Edge;
import utils.DefinitionDomain;

public class CSCohesiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		return DefinitionDomain.getScore(MAX_SCORE);
	}

}
