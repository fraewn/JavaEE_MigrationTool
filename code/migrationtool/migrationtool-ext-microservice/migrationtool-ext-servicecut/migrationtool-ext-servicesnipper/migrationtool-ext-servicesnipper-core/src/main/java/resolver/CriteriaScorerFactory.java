package resolver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Factory class to get the correct implementation of a scorer
 */
public class CriteriaScorerFactory {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private CriteriaScorerFactory() {

	}

	private static Map<Class<? extends CriteriaScorer>, CriteriaScorer> cache = new HashMap<>();

	/**
	 * Get the implementation of a scorer
	 *
	 * @param scorer searched scorer
	 * @return implementation of scorer
	 */
	public static CriteriaScorer getScorer(Class<? extends CriteriaScorer> scorer) {
		if (cache.isEmpty() || !cache.containsKey(scorer)) {
			loadScorer(scorer);
		}
		return cache.get(scorer);
	}

	private static void loadScorer(Class<? extends CriteriaScorer> scorer) {
		try {
			cache.put(scorer, scorer.getConstructor().newInstance());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			LOG.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}
}
