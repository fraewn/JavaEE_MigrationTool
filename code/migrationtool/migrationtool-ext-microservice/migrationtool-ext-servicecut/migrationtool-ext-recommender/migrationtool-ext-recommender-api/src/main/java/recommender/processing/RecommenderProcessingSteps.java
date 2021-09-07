package recommender.processing;

import static model.criteria.CouplingCriteria.CONTENT_VOLATILITY;
import static recommender.processing.Targets.ENTITY;
import static recommender.processing.Targets.USE_CASE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import model.ModelRepresentation;
import model.artifacts.ArchitectureArtifact;
import model.criteria.CompatibitlyCharacteristics;
import model.criteria.CompatibitlyMapper;
import model.criteria.CouplingCriteria;
import model.data.ArchitectureInformation;
import model.data.Characteristic;
import model.data.ContextGroup;
import model.data.Instance;
import model.data.UseCase;
import recommender.model.Recommendation;

/**
 * Processing steps of the recommendation engine. Containg all coupling
 * criteria, which are not set by the analyzer
 *
 * For the definitions of the criteria see {@link CouplingCriteria} and
 * {@link ArchitectureArtifact}
 */
public enum RecommenderProcessingSteps {
	/**
	 * Implemented;
	 */
	LATENCY(USE_CASE, USE_CASE) {
		private static final String GROUP = "INCLUDED";

		@Override
		public ArchitectureInformation createInformation(ModelRepresentation model, List<Recommendation> list,
				List<Integer> limits) {
			ArchitectureInformation res = new ArchitectureInformation();
			List<Recommendation> filtered = list.stream()
					.filter(x -> x.getMetricValue() > limits.get(limits.size() - 1)).collect(Collectors.toList());
			model.getInformation().getUseCases().forEach(x -> {
				for (Recommendation recommendation : filtered) {
					if (recommendation.getName().equals(x.getName())) {
						UseCase copy = new UseCase(x);
						copy.setLatencyCritical(recommendation.isIncluded());
						res.getUseCases().add(copy);
						break;
					}
				}
			});
			return res;
		}

		@Override
		public void setGroups(Map<String, Recommendation> list, Map<Integer, String> limits) {
			for (Recommendation rec : list.values()) {
				rec.setRelatedGroup(rec.getName());
				for (Integer limit : limits.keySet()) {
					if (rec.getMetricValue() > limit) {
						rec.setIncluded(true);
						break;
					}
				}
			}
		}

		@Override
		public void convertToModel(ModelRepresentation model, ArchitectureInformation info) {
			List<UseCase> list = new ArrayList<>(info.getUseCases());
			for (UseCase useCase : model.getInformation().getUseCases()) {
				boolean exists = false;
				for (UseCase u : list) {
					if (u.getName().equals(useCase.getName())) {
						exists = true;
						break;
					}
				}
				if (!exists) {
					list.add(useCase);
				}
			}
			model.getInformation().setUseCases(list);
		}

		@Override
		public List<String> getGroups() {
			return Collections.singletonList(GROUP);
		}
	},
	/**
	 * Not supported, no static code analysis metric
	 */
	AGGREGATES(ENTITY, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	SHARED_OWNER_GROUPS(ENTITY, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	PREDEFINED_SERVICES(ENTITY, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	SEPERATED_SECURITY_ZONES(ENTITY, ENTITY),
	/**
	 * Implemented;
	 */
	SECURITY_ACCESS_GROUPS(USE_CASE, ENTITY) {
		private static final String GROUP = "GROUPED";

		@Override
		public ArchitectureInformation createInformation(ModelRepresentation model, List<Recommendation> list,
				List<Integer> limits) {
			ArchitectureInformation res = new ArchitectureInformation();
			List<Recommendation> filtered = list.stream()
					.filter(x -> x.getRelatedGroup() != null).collect(Collectors.toList());
			List<ContextGroup> result = new ArrayList<>();
			for (Recommendation recommendation : filtered) {
				if (recommendation.isIncluded()) {
					for (String group : recommendation.getRelatedGroup().split(";")) {
						boolean included = false;
						for (ContextGroup cg : result) {
							if (cg.getName().equals(group)) {
								included = true;
								cg.getInstances().add(new Instance(recommendation.getName()));
								break;
							}
						}
						if (!included) {
							ContextGroup cg = new ContextGroup();
							cg.setName(group);
							cg.getInstances().add(new Instance(recommendation.getName()));
							result.add(cg);
						}
					}
				}
			}
			if (!result.isEmpty()) {
				res.getCriteria().put(ArchitectureArtifact.SECURITY_ACCESS_GROUPS, result);
			}
			return res;
		}

		@Override
		public void convertToModel(ModelRepresentation model, ArchitectureInformation info) {
			model.getInformation().getCriteria().put(ArchitectureArtifact.SECURITY_ACCESS_GROUPS,
					info.getCriteria().get(ArchitectureArtifact.SECURITY_ACCESS_GROUPS));
		}

		@Override
		public List<String> getGroups() {
			return Collections.singletonList(GROUP);
		}

		@Override
		public void setGroups(Map<String, Recommendation> list, Map<Integer, String> limits) {
			for (Recommendation rec : list.values()) {
				StringJoiner sb = new StringJoiner(";");
				for (Entry<Integer, String> entry : limits.entrySet()) {
					if ((rec.getMetricValue() != 0) && ((rec.getMetricValue() % entry.getKey()) == 0)) {
						sb.add(entry.getValue());
					}
				}
				if (sb.length() != 0) {
					rec.setRelatedGroup(sb.toString());
					rec.setIncluded(true);
				}
			}
		}
	},
	/**
	 * Not supported, no static code analysis metric
	 */
	COMPATIBILITY_STRUCTURAL_VOLATILITY(ENTITY, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	COMPATIBILITY_CONSISTENCY_CRITICALITY(ENTITY, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	COMPATIBILITY_AVAILABILITY_CRITICALITY(USE_CASE, ENTITY),
	/**
	 * Implemented;
	 */
	COMPATIBILITY_CONTENT_VOLATILITY(USE_CASE, ENTITY) {
		@Override
		public ArchitectureInformation createInformation(ModelRepresentation model, List<Recommendation> list,
				List<Integer> limits) {
			return buildCharacteristics(CONTENT_VOLATILITY, list, limits);
		}

		@Override
		public void convertToModel(ModelRepresentation model, ArchitectureInformation info) {
			model.getInformation().getCompatibilities().put(CONTENT_VOLATILITY,
					info.getCompatibilities().get(CONTENT_VOLATILITY));
		}

		@Override
		public List<String> getGroups() {
			CompatibitlyMapper mapper = CompatibitlyMapper.getMapperFromCriteria(CONTENT_VOLATILITY);
			List<CompatibitlyCharacteristics> temp = new ArrayList<>(mapper.getCharacterisitics());
			Collections.reverse(temp);
			return temp.stream().map(CompatibitlyCharacteristics::getName).collect(Collectors.toList());
		}

		@Override
		public void setGroups(Map<String, Recommendation> list, Map<Integer, String> limits) {
			setCharacteristics(CONTENT_VOLATILITY, new ArrayList<>(list.values()), limits.keySet());
		}
	},
	/**
	 * Not supported, no static code analysis metric
	 */
	COMPATIBILITY_STORAGE_SIMILARITY(USE_CASE, ENTITY),
	/**
	 * Not supported, no static code analysis metric
	 */
	COMPATIBILITY_SECURITY_CRITICALITY(USE_CASE, ENTITY),

	FINISHED(null, null);

	private Targets source;

	private Targets ratingTarget;

	RecommenderProcessingSteps(Targets source, Targets ratingTarget) {
		this.source = source;
		this.ratingTarget = ratingTarget;
	}

	/**
	 * @return the source
	 */
	public Targets getSource() {
		return this.source;
	}

	/**
	 * @return the ratingTarget
	 */
	public Targets getRatingTarget() {
		return this.ratingTarget;
	}

	/**
	 * Creates the correct Model object based of the list of recommendations
	 *
	 * @param model  current Model
	 * @param list   list of recommendations
	 * @param limits list of lower bounds
	 * @return model object with recommendations
	 */
	public ArchitectureInformation createInformation(ModelRepresentation model, List<Recommendation> list,
			List<Integer> limits) {
		return new ArchitectureInformation();
	}

	/**
	 * Sets the related group property of the recommendations based of the current
	 * step
	 *
	 * @param list   list of recommendations
	 * @param limits list of lower bounds
	 */
	public void setGroups(Map<String, Recommendation> list, List<Integer> limits) {
		Map<Integer, String> temp = new HashMap<>();
		for (Integer integer : limits) {
			temp.put(integer, null);
		}
		setGroups(list, temp);
	}

	/**
	 * Sets the related group property of the recommendations based of the current
	 * step
	 *
	 * @param list   list of recommendations
	 * @param limits list of lower bounds
	 */
	public void setGroups(Map<String, Recommendation> list, Map<Integer, String> limits) {

	}

	/**
	 * Gets Information about all possible groups
	 *
	 * @return groups
	 */
	public List<String> getGroups() {
		return new ArrayList<>();
	}

	/**
	 * Convert the object of recommendations to the decomposition model
	 *
	 * @param model decomposition model
	 * @param info  model object of recommendations
	 */
	public void convertToModel(ModelRepresentation model, ArchitectureInformation info) {

	}

	private static void setCharacteristics(CouplingCriteria criteria, List<Recommendation> res, Set<Integer> limits) {
		List<Integer> limit = new ArrayList<>(limits);
		Collections.sort(limit);
		int x = 0;
		for (CompatibitlyCharacteristics cc : CompatibitlyMapper.getMapperFromCriteria(criteria)
				.getCharacterisitics()) {
			// Ignore default characteristic
			if (!cc.isDefaultSelection()) {
				for (Recommendation rec : res) {
					if (rec.getMetricValue() > limit.get(x)) {
						rec.setRelatedGroup(cc.getName());
						rec.setIncluded(true);
					}
				}
			}
			x++;
		}
	}

	private static ArchitectureInformation buildCharacteristics(CouplingCriteria criteria, List<Recommendation> res,
			List<Integer> limits) {
		// highest limit first
		List<Integer> limit = new ArrayList<>(limits);
		Collections.reverse(limit);
		// match order of limit
		List<CompatibitlyCharacteristics> temp = new ArrayList<>(
				CompatibitlyMapper.getMapperFromCriteria(criteria).getCharacterisitics());
		Collections.reverse(temp);
		// create map of included recommendations
		Map<Recommendation, Boolean> filtered = new HashMap<>();
		for (Recommendation r : res) {
			filtered.put(r, false);
		}
		List<Characteristic> result = new ArrayList<>();
		int x = 0;
		for (CompatibitlyCharacteristics cc : temp) {
			// Ignore default characteristic
			if (!cc.isDefaultSelection()) {
				Characteristic c = new Characteristic();
				c.setCharacteristic(cc);
				for (Entry<Recommendation, Boolean> entry : filtered.entrySet()) {
					Recommendation rec = entry.getKey();
					if (!entry.getValue() && rec.isIncluded() && (rec.getMetricValue() > limit.get(x))) {
						c.getInstances().add(new Instance(rec.getName()));
						filtered.put(rec, true);
					}
				}
				if (!c.getInstances().isEmpty()) {
					result.add(c);
				}
			}
			x++;
		}
		ArchitectureInformation info = new ArchitectureInformation();
		if (!result.isEmpty()) {
			info.getCompatibilities().put(criteria, result);
		}
		return info;
	}
}
