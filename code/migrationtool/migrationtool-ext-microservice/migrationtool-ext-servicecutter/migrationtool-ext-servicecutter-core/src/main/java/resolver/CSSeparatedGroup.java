package resolver;

import static resolver.Scores.MIN_SCORE;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.CouplingGroup;
import core.Edge;

public class CSSeparatedGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		List<CouplingGroup> otherGroups = getExclusiveGroupOfEdgeAndCriteria(currentEdge, relatedGroup.getCriteria());
		Set<Edge> otherEdges = new HashSet<>();
		for (CouplingGroup group : otherGroups) {
			otherEdges.addAll(group.getRelatedEdges());
		}
		addPenalityToEdges(Scores.getScore(MIN_SCORE), currentEdge, relatedGroup.getRelatedEdges(), otherEdges,
				relatedGroup.getCriteria());
		return 0;
	}

}
