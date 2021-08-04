package validator.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.MigrationToolArgumentException;
import utils.DefinitionDomain;
import validator.Validator;

/**
 * Validator of the file {@link DefintionDomain#FILE_NAME}
 */
public class PrioritiesValidator implements Validator {

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public String configFile() {
		return DefinitionDomain.FILE_NAME;
	}

	@Override
	public void validate(Map<String, String> cache) throws MigrationToolArgumentException {
		Map<String, Double> allValues = new HashMap<>();
		allValues.put(DefinitionDomain.MAX_SCORE, null);
		allValues.put(DefinitionDomain.MIN_SCORE, null);
		allValues.put(DefinitionDomain.NO_SCORE, null);

		allValues.put(DefinitionDomain.PRIORITY_IGNORE, null);
		allValues.put(DefinitionDomain.PRIORITY_LOW, null);
		allValues.put(DefinitionDomain.PRIORITY_NORMAL, null);
		allValues.put(DefinitionDomain.PRIORITY_RELEVANT, null);
		allValues.put(DefinitionDomain.PRIORITY_IMPORTANT, null);
		allValues.put(DefinitionDomain.PRIORITY_VERY_IMPORTANT, null);
		allValues.put(DefinitionDomain.PRIORITY_CRITICAL, null);

		allValues.put(DefinitionDomain.SCORE_WRITE, null);
		allValues.put(DefinitionDomain.SCORE_MIXED, null);
		allValues.put(DefinitionDomain.SCORE_READ, null);
		allValues.put(DefinitionDomain.SCORE_AGGREGATION, null);

		allValues.put(DefinitionDomain.CC_9_LOW, null);
		allValues.put(DefinitionDomain.CC_9_NORMAL, null);
		allValues.put(DefinitionDomain.CC_9_HIGH, null);
		allValues.put(DefinitionDomain.CC_10_LOW, null);
		allValues.put(DefinitionDomain.CC_10_NORMAL, null);
		allValues.put(DefinitionDomain.CC_10_HIGH, null);
		allValues.put(DefinitionDomain.CC_11_LOW, null);
		allValues.put(DefinitionDomain.CC_11_NORMAL, null);
		allValues.put(DefinitionDomain.CC_11_HIGH, null);
		allValues.put(DefinitionDomain.CC_12_LOW, null);
		allValues.put(DefinitionDomain.CC_12_NORMAL, null);
		allValues.put(DefinitionDomain.CC_12_HIGH, null);
		allValues.put(DefinitionDomain.CC_13_LOW, null);
		allValues.put(DefinitionDomain.CC_13_NORMAL, null);
		allValues.put(DefinitionDomain.CC_13_HIGH, null);
		allValues.put(DefinitionDomain.CC_14_LOW, null);
		allValues.put(DefinitionDomain.CC_14_NORMAL, null);
		allValues.put(DefinitionDomain.CC_14_HIGH, null);

		for (Entry<String, Double> entry : allValues.entrySet()) {
			double value = checkDouble(cache, entry.getKey());
			allValues.put(entry.getKey(), value);
		}

		if (allValues.get(DefinitionDomain.MIN_SCORE) >= 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.MIN_SCORE + " must be smaller than 0");
		}
		if (allValues.get(DefinitionDomain.MAX_SCORE) <= 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.MIN_SCORE + " must be greater than 0");
		}
		if (allValues.get(DefinitionDomain.NO_SCORE) != 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.MIN_SCORE + " must be equals 0");
		}

		if (allValues.get(DefinitionDomain.PRIORITY_IGNORE) != 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_IGNORE + " must be equals 0");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_IGNORE) >= allValues.get(DefinitionDomain.PRIORITY_LOW)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_LOW + "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_IGNORE) + " (" + DefinitionDomain.PRIORITY_IGNORE + ")");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_LOW) >= allValues.get(DefinitionDomain.PRIORITY_NORMAL)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_NORMAL + "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_LOW) + " (" + DefinitionDomain.PRIORITY_LOW + " )");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_NORMAL) >= allValues.get(DefinitionDomain.PRIORITY_RELEVANT)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_RELEVANT + "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_NORMAL) + " (" + DefinitionDomain.PRIORITY_NORMAL + ")");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_RELEVANT) >= allValues.get(DefinitionDomain.PRIORITY_IMPORTANT)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_IMPORTANT + "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_RELEVANT) + " (" + DefinitionDomain.PRIORITY_RELEVANT
					+ ")");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_IMPORTANT) >= allValues
				.get(DefinitionDomain.PRIORITY_VERY_IMPORTANT)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_VERY_IMPORTANT
					+ "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_IMPORTANT) + " (" + DefinitionDomain.PRIORITY_IMPORTANT
					+ ")");
		}
		if (allValues.get(DefinitionDomain.PRIORITY_VERY_IMPORTANT) >= allValues
				.get(DefinitionDomain.PRIORITY_CRITICAL)) {
			throw new MigrationToolArgumentException(DefinitionDomain.PRIORITY_CRITICAL + "  must be greater than "
					+ allValues.get(DefinitionDomain.PRIORITY_VERY_IMPORTANT) + " ("
					+ DefinitionDomain.PRIORITY_VERY_IMPORTANT
					+ ")");
		}

		if (allValues.get(DefinitionDomain.SCORE_WRITE) < 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.SCORE_WRITE + " must be greater than 0");
		}
		if (allValues.get(DefinitionDomain.SCORE_READ) < 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.SCORE_READ + " must be greater than 0");
		}
		if (allValues.get(DefinitionDomain.SCORE_MIXED) < 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.SCORE_MIXED + " must be greater than 0");
		}
		if (allValues.get(DefinitionDomain.SCORE_AGGREGATION) < 0) {
			throw new MigrationToolArgumentException(DefinitionDomain.SCORE_AGGREGATION + " must be greater than 0");
		}

		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_9_LOW, DefinitionDomain.CC_9_NORMAL,
				DefinitionDomain.CC_9_HIGH);
		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_10_LOW, DefinitionDomain.CC_10_NORMAL,
				DefinitionDomain.CC_10_HIGH);
		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_11_LOW, DefinitionDomain.CC_11_NORMAL,
				DefinitionDomain.CC_11_HIGH);
		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_12_LOW, DefinitionDomain.CC_12_NORMAL,
				DefinitionDomain.CC_12_HIGH);
		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_13_LOW, DefinitionDomain.CC_13_NORMAL,
				DefinitionDomain.CC_13_HIGH);
		checkOrderOfCompatibility(allValues, DefinitionDomain.CC_14_LOW, DefinitionDomain.CC_14_NORMAL,
				DefinitionDomain.CC_14_HIGH);
	}

	private double checkDouble(Map<String, String> cache, String key) {
		String value = cache.get(key);
		if (value == null) {
			throw new MigrationToolArgumentException(key + " is missing or blank");
		}
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			throw new MigrationToolArgumentException(key + " is not a valid number");
		}
	}

	private void checkOrderOfCompatibility(Map<String, Double> allValues, String low, String normal, String high) {
		if (allValues.get(low) < 0) {
			throw new MigrationToolArgumentException(low + " must be greater than 0");
		}
		if (allValues.get(low) >= allValues.get(normal)) {
			throw new MigrationToolArgumentException(normal + "  must be greater than "
					+ allValues.get(low) + " (" + low + ")");
		}
		if (allValues.get(normal) >= allValues.get(high)) {
			throw new MigrationToolArgumentException(high + "  must be greater than "
					+ allValues.get(normal) + " (" + normal + " )");
		}
	}
}
