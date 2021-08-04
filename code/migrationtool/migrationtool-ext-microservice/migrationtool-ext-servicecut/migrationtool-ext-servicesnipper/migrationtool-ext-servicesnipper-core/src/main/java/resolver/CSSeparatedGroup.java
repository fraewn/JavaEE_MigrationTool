package resolver;

import static utils.DefinitionDomain.MIN_SCORE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.CouplingGroup;
import model.Edge;
import utils.DefinitionDomain;

public class CSSeparatedGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		List<CouplingGroup> otherGroups = getExclusiveGroupOfEdgeAndCriteria(currentEdge, relatedGroup.getCriteria());
		Set<Edge> otherEdges = new HashSet<>();
		for (CouplingGroup group : otherGroups) {
			otherEdges.addAll(group.getRelatedEdges());
		}
		addPenalityToEdges(DefinitionDomain.getScore(MIN_SCORE), currentEdge, relatedGroup.getRelatedEdges(), otherEdges,
				relatedGroup.getCriteria());
		return 0;
	}

}
