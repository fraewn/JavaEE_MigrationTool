package recommender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The possible operation types to handle the list of metric values for a
 * recommendation
 */
public enum MetricOperation {
	/**
	 * The first entry in the list is used
	 */
	HIGHEST_PRIO {
		@Override
		public Map<String, Integer> handle(Map<String, List<Integer>> map) {
			Map<String, Integer> res = new HashMap<>();
			for (Entry<String, List<Integer>> e : map.entrySet()) {
				res.put(e.getKey(), e.getValue().isEmpty() ? 0 : e.getValue().get(0));
			}
			return res;
		}
	},
	/**
	 * Sum of all metric value
	 */
	CONCAT {
		@Override
		public Map<String, Integer> handle(Map<String, List<Integer>> map) {
			Map<String, Integer> res = new HashMap<>();
			for (Entry<String, List<Integer>> e : map.entrySet()) {
				int sum = 0;
				for (Integer i : e.getValue()) {
					sum += i;
				}
				res.put(e.getKey(), sum);
			}
			return res;
		}
	},
	/**
	 * Average of all metric values (sum/count)
	 */
	AVERAGE {
		@Override
		public Map<String, Integer> handle(Map<String, List<Integer>> map) {
			Map<String, Integer> res = new HashMap<>();
			for (Entry<String, List<Integer>> e : map.entrySet()) {
				int sum = 0;
				for (Integer i : e.getValue()) {
					sum += i;
				}
				res.put(e.getKey(), sum / e.getValue().size());
			}
			return res;
		}
	},
	/**
	 * The highest entry in the list is used
	 */
	HIGHEST_VALUE {
		@Override
		public Map<String, Integer> handle(Map<String, List<Integer>> map) {
			Map<String, Integer> res = new HashMap<>();
			for (Entry<String, List<Integer>> e : map.entrySet()) {
				int max = 0;
				for (Integer i : e.getValue()) {
					if (i > max) {
						max = i;
					}
				}
				res.put(e.getKey(), max);
			}
			return res;
		}
	};

	/**
	 * Combines or reduce the list of the metric values
	 *
	 * @param map map
	 * @return combined metric values
	 */
	public abstract Map<String, Integer> handle(Map<String, List<Integer>> map);
}
