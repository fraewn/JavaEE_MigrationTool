package recommender;

import static utils.RecommendationKeys.RECOMMEND_LOWER_BOUND;
import static utils.RecommendationKeys.RECOMMEND_OPERATION;
import static utils.RecommendationKeys.SEPERATOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;

import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import model.data.Instance;
import model.data.UseCase;
import operations.dto.AstDTO;
import recommender.model.Recommendation;
import recommender.processing.RecommenderProcessingSteps;
import recommender.processing.Targets;
import recommender.service.RecommenderService;
import rules.engine.RuleEvaluator;
import utils.PropertiesLoader;
import utils.RecommendationKeys;

/**
 * Recommendation engine. Rule based approach. The rules are defined by a metric
 * value and lower bounds
 */
public class Recommender implements RecommenderService {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Location of the configuration file */
	public static final String FILE_NAME = "servicesnipper/servicesnipper-recommender.properties";
	/** Config file */
	private PropertiesLoader props;
	/** Decompostion model */
	private ModelRepresentation model;
	/** All Entites */
	private List<AstDTO> entities;
	/** All UseCases */
	private Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases;
	/** RuleEngine */
	private RuleEvaluator ruleEngine;

	public Recommender() {
		this.ruleEngine = new RuleEvaluator();
		this.props = new PropertiesLoader(FILE_NAME);
		this.props.loadProps(false);
	}

	@Override
	public void importArchitecture(ModelRepresentation model, List<AstDTO> entities,
			Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases) {
		this.model = model;
		this.entities = entities;
		this.useCases = useCases;
	}

	@Override
	public Map<String, Recommendation> process(RecommenderProcessingSteps currentStep) {
		Map<String, Recommendation> res = new HashMap<>();
		String keyid = currentStep.name();
		String exprOper = this.props.getCache().get(keyid + RECOMMEND_OPERATION);
		String exprLimit = this.props.getCache().get(keyid + RECOMMEND_LOWER_BOUND);
		if (((exprOper != null) && !exprOper.isBlank()) && ((exprLimit != null) && !exprLimit.isBlank())) {
			Map<String, List<Integer>> recommand = new HashMap<>();
			recommand.putAll(processWrapper(currentStep, currentStep.getSource(), currentStep.getRatingTarget()));
			Map<String, Integer> tmp = MetricOperation.valueOf(exprOper.toUpperCase()).handle(recommand);
			for (Entry<String, Integer> e : tmp.entrySet()) {
				res.put(e.getKey(), new Recommendation(e.getKey(), e.getValue(), null, false));
			}
			String[] temp = exprLimit.split(RecommendationKeys.SEPERATOR);
			List<Integer> limits = Arrays.stream(temp).map(Integer::parseInt).collect(Collectors.toList());
			Collections.reverse(limits);
			currentStep.setGroups(res, limits);
		}
		return res;
	}

	private Map<String, List<Integer>> processWrapper(RecommenderProcessingSteps currentStep, Targets source,
			Targets target) {
		Map<String, String> cache = this.props.getCache();
		String keyid = currentStep.name();
		// It is possible that a target gets more values in one prio definition
		Map<String, List<Integer>> recommand = target.fillWithTargetIds(this.model);
		// Calc all possible prios
		int prio = 1;
		boolean hasPrio = true;
		while (hasPrio) {
			String exprOperation = cache.get(keyid + RecommendationKeys.RECOMMEND_METRIC_OPERATION + prio);
			String exprMetric = cache.get(keyid + RecommendationKeys.RECOMMEND_METRIC + prio);
			String exprMultiplier = cache.get(keyid + RecommendationKeys.RECOMMEND_METRIC_MULTIPLIER + prio);
			if ((exprMetric == null) || (exprMultiplier == null) || (exprOperation == null) || (exprMetric.isBlank())
					|| (exprMultiplier.isBlank()) || (exprOperation.isBlank())) {
				hasPrio = false;
			}
			if (hasPrio) {
				MetricOperation operation = MetricOperation.valueOf(exprOperation.toUpperCase());
				double multiplier = Double.parseDouble(exprMultiplier);
				Map<String, Integer> list = new HashMap<>();
				List<String> keys = new ArrayList<>(recommand.keySet());
				if (source.equals(Targets.USE_CASE) && target.equals(Targets.USE_CASE)) {
					list.putAll(processUseCaseWithTargetUseCase(keys, exprMetric, operation));
				} else if (source.equals(Targets.USE_CASE) && target.equals(Targets.ENTITY)) {
					list.putAll(processUseCaseWithTargetEntity(keys, exprMetric, operation));
				} else if (source.equals(Targets.ENTITY) && target.equals(Targets.ENTITY)) {
					list.putAll(processEntityWithTargetEntity(keys, exprMetric, operation));
				}
				for (Entry<String, Integer> entry : list.entrySet()) {
					if (recommand.containsKey(entry.getKey())) {
						recommand.get(entry.getKey()).add((int) (entry.getValue() * multiplier));
					}
				}
			}
			prio++;
		}
		return recommand;
	}

	private Map<String, Integer> processUseCaseWithTargetUseCase(List<String> recommand, String exprMetric,
			MetricOperation operation) {
		Map<String, Integer> result = new HashMap<>();
		this.ruleEngine.newStatement(exprMetric);
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> entry : this.useCases.entrySet()) {
			for (MethodDeclaration method : entry.getValue()) {
				String id = entry.getKey().resolve().getQualifiedName() + "." + method.getNameAsString();
				if (recommand.contains(id)) {
					LOG.debug("Test method: " + id);
					this.ruleEngine.recommand(method, x -> {
						if (x >= 0) {
							result.put(id, x);
						}
					});
				}
			}
		}
		return result;
	}

	private Map<String, Integer> processUseCaseWithTargetEntity(List<String> recommand, String exprMetric,
			MetricOperation operation) {
		Map<String, List<Integer>> result = new HashMap<>();
		this.ruleEngine.newStatement(exprMetric);
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> entry : this.useCases.entrySet()) {
			for (MethodDeclaration method : entry.getValue()) {
				String id = entry.getKey().resolve().getQualifiedName() + "." + method.getNameAsString();
				UseCase current = getUseCaseByName(id);
				if (current != null) {
					LOG.debug("Test method: " + id);
					this.ruleEngine.recommand(method, x -> {
						if (x >= 0) {
							List<Instance> list = new ArrayList<>();
							list.addAll(current.getInput());
							list.addAll(current.getPersistenceChanges());
							for (Instance instance : list) {
								String entityId = instance.getQualifiedName();
								if (recommand.contains(entityId)) {
									if (!result.containsKey(entityId)) {
										result.put(entityId, new ArrayList<>());
									}
									result.get(entityId).add(x);
								}
							}
						}
					});
				}
			}
		}
		return operation.handle(result);
	}

	private Map<String, Integer> processEntityWithTargetEntity(List<String> recommend, String exprMetric,
			MetricOperation operation) {
		Map<String, List<Integer>> result = new HashMap<>();
		this.ruleEngine.newStatement(exprMetric);
		for (AstDTO dto : this.entities) {
			for (FieldDeclaration attribute : dto.getFields()) {
				for (VariableDeclarator variable : attribute.getVariables()) {
					String id = dto.getFullName() + "." + variable.getNameAsString();
					if (recommend.contains(id)) {
						this.ruleEngine.recommand(dto.getJavaClass(), x -> {
							if (x >= 0) {
								// TODO not supported currently
							}
						});
					}
				}
			}
		}
		return operation.handle(result);
	}

	@Override
	public List<Integer> getInformation(RecommenderProcessingSteps currentStep) {
		List<Integer> limits = getLimits(currentStep);
		Collections.reverse(limits);
		return limits;
	}

	@Override
	public ArchitectureInformation convertRecommendations(List<Recommendation> list,
			RecommenderProcessingSteps currentStep) {
		List<Integer> limits = getLimits(currentStep);
		return currentStep.createInformation(this.model, list, limits);
	}

	private UseCase getUseCaseByName(String name) {
		for (UseCase useCase : this.model.getInformation().getUseCases()) {
			if (useCase.getName().equals(name)) {
				return useCase;
			}
		}
		return null;
	}

	private List<Integer> getLimits(RecommenderProcessingSteps currentStep) {
		String exprLimit = this.props.getCache().get(currentStep.name() + RECOMMEND_LOWER_BOUND);
		if (exprLimit == null) {
			return new ArrayList<>();
		}
		String[] temp = exprLimit.split(SEPERATOR);
		return Arrays.stream(temp).map(Integer::parseInt).collect(Collectors.toList());
	}
}
