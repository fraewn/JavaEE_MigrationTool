package resolver;

import java.util.List;

import core.CouplingGroup;
import core.Edge;
import core.Node;
import model.criteria.CompabilityCharacteristics;
import model.data.Characteristic;

public class CSCharacteristics extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		// characteristic in name of group
		String tmp = relatedGroup.getGroupName().split("/")[1];
		Characteristic c = new Characteristic();
		c.setCompabilityCharacteristics(tmp);
		CompabilityCharacteristics characteristics = c.getCharacteristic();

		return 0;
	}

	private void addPenalityToEdges(Edge currentEdge, CouplingGroup relatedGroup) {
		Node origin = currentEdge.getFirstNode();
		for (Edge edge : this.graph.getEdges().keySet()) {
			if (edge.getFirstNode().equals(origin)) {
				// check same group
				if (!relatedGroup.getRelatedEdges().contains(edge)) {
					List<CouplingGroup> group = getExclusiveGroupOfEdgeAndCriteria(edge, relatedGroup.getCriteria());

				}
			}
		}
	}
}
