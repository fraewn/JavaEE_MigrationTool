package recommender.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ModelRepresentation;
import model.data.Instance;
import model.data.UseCase;
import model.erm.Entity;

/**
 * Recommendation can affect usecase or the attributes of an entity (instances)
 */
public enum Targets {
	/**
	 * UseCases {@link UseCase}
	 */
	USE_CASE {
		@Override
		public Map<String, List<Integer>> fillWithTargetIds(ModelRepresentation model) {
			Map<String, List<Integer>> recommand = new HashMap<>();
			for (UseCase usecase : model.getInformation().getUseCases()) {
				recommand.put(usecase.getName(), new ArrayList<>());
			}
			return recommand;
		}
	},

	/**
	 * Attributes of an entity (instances) {@link Instance}
	 */
	ENTITY {
		@Override
		public Map<String, List<Integer>> fillWithTargetIds(ModelRepresentation model) {
			Map<String, List<Integer>> recommand = new HashMap<>();
			for (Entity entity : model.getEntityDiagram().getEntities()) {
				for (String attr : entity.getAttributes()) {
					recommand.put(entity.getName() + "." + attr, new ArrayList<>());
				}
			}
			return recommand;
		}
	};

	/**
	 * Creates based of the target a list of possible affected objects
	 *
	 * @param model current decomposition model
	 * @return list of affected objects
	 */
	public abstract Map<String, List<Integer>> fillWithTargetIds(ModelRepresentation model);
}
