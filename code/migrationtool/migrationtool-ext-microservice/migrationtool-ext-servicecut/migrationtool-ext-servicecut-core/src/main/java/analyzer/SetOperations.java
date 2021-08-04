package analyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum SetOperations {
	HIGHEST_PRIO {
		@Override
		public Set<String> handle(List<Set<String>> list) {
			Set<String> res = new HashSet<>();
			for (Set<String> set : list) {
				if (!set.isEmpty()) {
					res.addAll(set);
					break;
				}
			}
			return res;
		}
	},
	CONCAT {
		@Override
		public Set<String> handle(List<Set<String>> list) {
			Set<String> res = new HashSet<>();
			for (Set<String> set : list) {
				if (!set.isEmpty()) {
					res.addAll(set);
				}
			}
			return res;
		}
	};

	public abstract Set<String> handle(List<Set<String>> list);
}
