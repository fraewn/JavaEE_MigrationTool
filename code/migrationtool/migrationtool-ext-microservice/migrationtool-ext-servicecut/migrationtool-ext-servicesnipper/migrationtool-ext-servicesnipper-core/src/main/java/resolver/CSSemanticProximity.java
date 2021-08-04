package resolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import model.CouplingGroup;
import model.Edge;
import model.EdgeAttribute;
import utils.DefinitionDomain;

public class CSSemanticProximity extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		double value = 0;
		if (currentEdge.getAttributes().contains(EdgeAttribute.READ_ACCESS)) {
			value += DefinitionDomain.getScore(DefinitionDomain.SCORE_READ);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.WRITE_ACCESS)) {
			value += DefinitionDomain.getScore(DefinitionDomain.SCORE_WRITE);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.MIXED_ACCESS)) {
			value += DefinitionDomain.getScore(DefinitionDomain.SCORE_MIXED);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.AGGREGATION)) {
			value += DefinitionDomain.getScore(DefinitionDomain.SCORE_AGGREGATION);
		}
		return value;
	}

	@Override
	public Map<Edge, Double> normalize(Map<Edge, Double> values) {
		List<Double> scores = values.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
		// top 10% gets max score
		int tenPercent = Math.max(1, (int) (scores.size() * 0.1d));
		// get corresponding reference value
		double referenceValue = scores.get(tenPercent - 1);
		double divisor = referenceValue / DefinitionDomain.getScore(DefinitionDomain.MAX_SCORE);
		for (Entry<Edge, Double> edge : values.entrySet()) {
			double newScore = Math.min(DefinitionDomain.getScore(DefinitionDomain.MAX_SCORE),
					edge.getValue() / divisor);
			values.put(edge.getKey(), newScore);
		}
		return values;
	}
}
