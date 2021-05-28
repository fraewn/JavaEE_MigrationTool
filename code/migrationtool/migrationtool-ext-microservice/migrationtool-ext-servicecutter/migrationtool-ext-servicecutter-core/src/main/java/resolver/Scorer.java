package resolver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class Scorer {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Scorer.class);

	private Scorer() {

	}

	private static Map<Class<? extends CriteriaScorer>, CriteriaScorer> cache = new HashMap<>();

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
