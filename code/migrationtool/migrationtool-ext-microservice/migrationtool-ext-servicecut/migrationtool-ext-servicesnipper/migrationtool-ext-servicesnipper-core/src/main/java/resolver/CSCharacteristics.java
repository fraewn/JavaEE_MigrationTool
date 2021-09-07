package resolver;

import java.util.List;

import model.CouplingGroup;
import model.EdgeWrapper;
import model.criteria.CompatibitlyCharacteristics;
import model.criteria.CompatibitlyMapper;
import model.data.Characteristic;

/**
 * Scorer for criteria of type compatibility
 * {@link model.criteria.CouplingGroup#COMPATIBILITY}
 */
public class CSCharacteristics extends CriteriaScorerWrapper {

	@Override
	public double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup) {
		return 0;
	}

	@Override
	public void afterCalcScore(CouplingGroup relatedGroup) {
		Characteristic c = new Characteristic();
		c.setCompabilityCharacteristics(relatedGroup.getGroupName().split("/")[1]);
		CompatibitlyCharacteristics ownCharacteristic = c.getCharacteristic();

		List<CouplingGroup> otherGroups = getAllGroupsOfCriteria(relatedGroup.getCriteria());

		for (String node : relatedGroup.getRelatedNodes()) {
			for (EdgeWrapper edge : this.graph.getDirectNeighbours(node)) {
				if (!relatedGroup.getRelatedEdges().contains(edge)) {
					CompatibitlyMapper cm = CompatibitlyMapper.valueOf(relatedGroup.getCriteria().getCode());
					double value = Math.abs(ownCharacteristic.getWeight() - cm.getDefault().getWeight());
					for (CouplingGroup couplingGroup : otherGroups) {
						if (couplingGroup.getRelatedEdges().contains(edge)) {
							Characteristic tmp = new Characteristic();
							tmp.setCompabilityCharacteristics(couplingGroup.getGroupName().split("/")[1]);
							CompatibitlyCharacteristics characteristics = tmp.getCharacteristic();
							value = Math.abs(ownCharacteristic.getWeight() - characteristics.getWeight());
							break;
						}
					}
					this.graph.addNewScore(edge, relatedGroup.getCriteria(), value * -1);
				}
			}
		}
	}
}
