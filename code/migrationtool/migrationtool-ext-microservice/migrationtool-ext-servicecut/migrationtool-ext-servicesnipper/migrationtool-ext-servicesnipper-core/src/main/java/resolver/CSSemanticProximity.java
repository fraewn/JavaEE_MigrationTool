package resolver;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import graph.model.EdgeAttribute;
import model.CouplingGroup;
import model.EdgeWrapper;
import model.criteria.CouplingCriteria;
import utils.DefinitionDomain;

/**
 * Scorer for the coupling criteria {@link CouplingCriteria#SEMANTIC_PROXIMITY}
 */
public class CSSemanticProximity extends CriteriaScorerWrapper {

	@Override
	public double getScore(EdgeWrapper currentEdge, CouplingGroup relatedGroup) {
		double value = 0;

		for (EdgeWrapper edge : relatedGroup.getRelatedEdges()) {
			if (edge.equals(currentEdge)) {
				EdgeAttribute attr = edge.getAttr();
				if (attr.equals(EdgeAttribute.READ_ACCESS)) {
					value += DefinitionDomain.getScore(DefinitionDomain.SCORE_READ);
				}
				if (attr.equals(EdgeAttribute.WRITE_ACCESS)) {
					value += DefinitionDomain.getScore(DefinitionDomain.SCORE_WRITE);
				}
				if (attr.equals(EdgeAttribute.MIXED_ACCESS)) {
					value += DefinitionDomain.getScore(DefinitionDomain.SCORE_MIXED);
				}
				if (attr.equals(EdgeAttribute.AGGREGATION)) {
					value += DefinitionDomain.getScore(DefinitionDomain.SCORE_AGGREGATION);
				}
				break;
			}
		}
		return value;
	}

	@Override
	public Map<EdgeWrapper, Double> normalize(Map<EdgeWrapper, Double> values) {
		List<Double> scores = values.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
		// top 10% gets max score
		int tenPercent = Math.max(1, (int) (scores.size() * 0.1d));
		// get corresponding reference value
		double referenceValue = scores.get(tenPercent - 1);
		int counter = 0;
		LOG.info("Normalized output");
		for (Entry<EdgeWrapper, Double> edge : values.entrySet()) {
			if ((edge.getValue() > referenceValue) && (counter < tenPercent)) {
				values.put(edge.getKey(), DefinitionDomain.getScore(DefinitionDomain.MAX_SCORE) * 0.75d);
				counter++;
			} else {
				double newScore = DefinitionDomain.getScore(DefinitionDomain.MAX_SCORE) * 0.75d *
						(edge.getValue() / referenceValue);
				values.put(edge.getKey(), newScore);
			}
			LOG.info("{} gets value: {} ", edge.getKey(), values.get(edge.getKey()));
		}
		return values;
	}
}
