package resolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import core.CouplingGroup;
import core.Edge;
import core.EdgeAttribute;

public class CSSemanticProximity extends CriteriaScorerWrapper {

	@Override
	public double getScore(Edge currentEdge, CouplingGroup relatedGroup) {
		double value = 0;
		if (currentEdge.getAttributes().contains(EdgeAttribute.READ_ACCESS)) {
			value += Scores.getScore(Scores.SCORE_READ);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.WRITE_ACCESS)) {
			value += Scores.getScore(Scores.SCORE_WRITE);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.MIXED_ACCESS)) {
			value += Scores.getScore(Scores.SCORE_MIXED);
		}
		if (currentEdge.getAttributes().contains(EdgeAttribute.AGGREGATION)) {
			value += Scores.getScore(Scores.SCORE_AGGREGATION);
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
		double divisor = referenceValue / Scores.getScore(Scores.MAX_SCORE);
		for (Entry<Edge, Double> edge : values.entrySet()) {
			double newScore = Math.min(Scores.getScore(Scores.MAX_SCORE), edge.getValue() / divisor);
			values.put(edge.getKey(), newScore);
		}
		return values;
	}
}
