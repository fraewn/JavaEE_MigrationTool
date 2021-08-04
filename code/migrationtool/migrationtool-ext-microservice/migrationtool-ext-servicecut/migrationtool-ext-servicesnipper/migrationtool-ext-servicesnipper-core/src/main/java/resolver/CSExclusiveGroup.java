package resolver;

import static utils.DefinitionDomain.MAX_SCORE;
import static utils.DefinitionDomain.MIN_SCORE;

import model.CouplingGroup;
import model.Edge;
import utils.DefinitionDomain;

public class CSExclusiveGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		addPenalityToEdges(DefinitionDomain.getScore(MIN_SCORE), currentEdge, relatedGroup.getRelatedEdges(),
				relatedGroup.getCriteria());
		return DefinitionDomain.getScore(MAX_SCORE);
	}
}
