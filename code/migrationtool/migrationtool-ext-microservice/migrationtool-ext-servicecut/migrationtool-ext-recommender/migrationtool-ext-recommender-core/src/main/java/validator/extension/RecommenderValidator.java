package validator.extension;

import static utils.RecommendationKeys.RECOMMEND_LOWER_BOUND;
import static utils.RecommendationKeys.RECOMMEND_METRIC;
import static utils.RecommendationKeys.RECOMMEND_METRIC_MULTIPLIER;
import static utils.RecommendationKeys.RECOMMEND_METRIC_OPERATION;
import static utils.RecommendationKeys.RECOMMEND_OPERATION;
import static utils.RecommendationKeys.SEPERATOR;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.MigrationToolArgumentException;
import exceptions.MigrationToolInitException;
import operations.Validator;
import recommender.MetricOperation;
import recommender.Recommender;
import recommender.processing.RecommenderProcessingSteps;
import rules.engine.RuleEvaluator;
import utils.StateMachine;

/**
 * Validator of the file {@link Recommender#FILE_NAME}
 */
public class RecommenderValidator implements Validator {

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public String configFile() {
		return Recommender.FILE_NAME;
	}

	@Override
	public void validate(Map<String, String> cache) throws MigrationToolArgumentException {
		StateMachine<RecommenderProcessingSteps> states = new StateMachine<>(RecommenderProcessingSteps.class);
		do {
			RecommenderProcessingSteps current = states.getCurrent();
			Map<String, Boolean> errors = new HashMap<>();
			String exprOper = cache.get(current + RECOMMEND_OPERATION);
			String exprLimit = cache.get(current + RECOMMEND_LOWER_BOUND);
			errors.put(current + RECOMMEND_OPERATION, (exprOper == null) || exprOper.isBlank());
			errors.put(current + RECOMMEND_LOWER_BOUND, (exprLimit == null) || exprLimit.isBlank());
			long filtered = errors.entrySet().stream().filter(Entry::getValue).count();
			if ((filtered > 0) && (filtered < 2)) {
				throw new MigrationToolArgumentException(errors.keySet() + " is blank");
			}
			if (filtered != 2) {
				try {
					MetricOperation.valueOf(exprOper.toUpperCase());
					String[] tmp = exprLimit.split(SEPERATOR);
					if (tmp.length != current.getGroups().size()) {
						throw new MigrationToolArgumentException(current + RECOMMEND_LOWER_BOUND
								+ " does not match the count of possible groups " + current.getGroups().size());
					}
					for (String string : tmp) {
						Integer.parseInt(string);
					}
				} catch (NumberFormatException e) {
					throw new MigrationToolArgumentException(current + RECOMMEND_LOWER_BOUND
							+ " limit is not of type integer");
				} catch (IllegalArgumentException e) {
					throw new MigrationToolArgumentException(current + RECOMMEND_OPERATION
							+ " must be a value of " + Arrays.toString(MetricOperation.values()));
				}
			}
			errors.clear();
			boolean hasPrio = true;
			int z = 1;
			while (hasPrio) {
				String exprMetricOper = cache.get(current + RECOMMEND_METRIC_OPERATION + z);
				String exprMetric = cache.get(current + RECOMMEND_METRIC + z);
				String exprMultiplyer = cache.get(current + RECOMMEND_METRIC_MULTIPLIER + z);
				errors.put(states.getCurrent() + RECOMMEND_METRIC_OPERATION,
						(exprMetricOper == null) || exprMetricOper.isBlank());
				errors.put(states.getCurrent() + RECOMMEND_METRIC, (exprMetric == null) || exprMetric.isBlank());
				errors.put(states.getCurrent() + RECOMMEND_METRIC_MULTIPLIER,
						(exprMultiplyer == null) || exprMultiplyer.isBlank());
				if ((errors.size() > 0) && (errors.size() < 3)) {
					throw new MigrationToolArgumentException(errors.keySet() + " is blank");
				}
				if (errors.size() != 3) {
					try {
						MetricOperation.valueOf(exprMetricOper.toUpperCase());
						Integer.parseInt(exprMultiplyer);
						new RuleEvaluator(exprMetric);
					} catch (NumberFormatException e) {
						throw new MigrationToolArgumentException(current + RECOMMEND_METRIC_MULTIPLIER + z
								+ " limit is not of type integer");
					} catch (IllegalArgumentException e) {
						throw new MigrationToolArgumentException(current + RECOMMEND_METRIC_OPERATION + z
								+ " must be a value of " + Arrays.toString(MetricOperation.values()));
					} catch (MigrationToolInitException e) {
						throw new MigrationToolArgumentException(
								current + RECOMMEND_METRIC + z + ": " + e.getMessage());
					}
				} else {
					hasPrio = false;
				}
				z++;
			}
			states.nextStep();
		} while (!states.isProcessDone());
	}

}
