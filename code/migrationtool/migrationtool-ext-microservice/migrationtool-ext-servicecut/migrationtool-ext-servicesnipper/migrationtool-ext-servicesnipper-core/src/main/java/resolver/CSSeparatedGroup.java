package resolver;

import static utils.DefinitionDomain.MIN_SCORE;

import java.util.List;

import model.CouplingGroup;
import model.EdgeWrapper;
import model.criteria.CouplingCriteria;
import utils.DefinitionDomain;

/**
 * Scorer for a group of instances, which should not be in the same service
 */
public class CSSeparatedGroup extends CriteriaScorerWrapper {

	@Override
	public double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup) {
		return 0;
	}

	@Override
	public void afterCalcScore(CouplingGroup relatedGroup) {
		CouplingCriteria criteria = relatedGroup.getCriteria();
		double penality = DefinitionDomain.getScore(MIN_SCORE);
		List<CouplingGroup> otherGroups = getAllGroupsOfCriteria(relatedGroup.getCriteria());
		for (CouplingGroup couplingGroup : otherGroups) {
			if (!couplingGroup.getGroupName().equals(relatedGroup.getGroupName())) {
				for (EdgeWrapper edge : couplingGroup.getRelatedEdges()) {
					LOG.info("{} new penalty added {} ({}/{})", edge, penality, relatedGroup.getGroupName(), criteria);
					this.graph.addNewScore(edge, criteria, penality);
				}
			}
		}
	}

}
