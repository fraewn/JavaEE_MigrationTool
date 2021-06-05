package resolver;

import java.util.List;

import model.CouplingGroup;
import model.Edge;
import model.Node;
import model.criteria.CompabilityCharacteristics;
import model.criteria.CompabilityMapper;
import model.data.Characteristic;

public class CSCharacteristics extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		// characteristic in name of group
		String tmp = relatedGroup.getGroupName().split("/")[1];
		Characteristic c = new Characteristic();
		c.setCompabilityCharacteristics(tmp);
		CompabilityCharacteristics characteristics = c.getCharacteristic();
		addPenalityToEdges(currentEdge, relatedGroup, characteristics);
		return 0;
	}

	private void addPenalityToEdges(Edge currentEdge, CouplingGroup relatedGroup,
			CompabilityCharacteristics currentCC) {
		Node origin = currentEdge.getFirstNode();
		for (Edge edge : this.graph.getAllEdges()) {
			if (edge.getFirstNode().equals(origin)) {
				// check same group
				if (!relatedGroup.getRelatedEdges().contains(edge)) {
					List<CouplingGroup> group = getExclusiveGroupOfEdgeAndCriteria(edge, relatedGroup.getCriteria());
					double value = 0;
					if (group.size() == 0) {
						// default no different value
						CompabilityMapper cm = CompabilityMapper.valueOf(relatedGroup.getCriteria().getCode());
						value = Math.abs(currentCC.getWeight() - cm.getDefault().getWeight());
					} else {
						// different group dont use default
						String tmp = group.get(0).getGroupName().split("/")[1];
						Characteristic c = new Characteristic();
						c.setCompabilityCharacteristics(tmp);
						CompabilityCharacteristics characteristics = c.getCharacteristic();
						value = Math.abs(currentCC.getWeight() - characteristics.getWeight());
					}
					value = value * -1;
					this.graph.addNewScore(edge, relatedGroup.getCriteria(), value);
				}
			}
		}
	}
}
