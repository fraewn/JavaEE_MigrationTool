package analyzer;

import static model.erm.RelationType.AGGREGATION;
import static model.erm.RelationType.COMPOSITION;
import static model.erm.RelationType.INHERITANCE;
import static rules.engine.WildCards.ENTITY_NAME;
import static rules.engine.WildCards.ORIGIN_CLASS;
import static rules.engine.WildCards.ORIGIN_USECASE;
import static utils.RuleKeys.AGGREGATION_DEFINITION;
import static utils.RuleKeys.COMPOSITION_DEFINITION;
import static utils.RuleKeys.ENTITY_COLUMN_DEFINITION_FIELD;
import static utils.RuleKeys.ENTITY_COLUMN_DEFINITION_METHOD;
import static utils.RuleKeys.ENTITY_DEFINITION;
import static utils.RuleKeys.INHERITENCE_DEFINITION;
import static utils.RuleKeys.RELATIONSHIP_PRIORITY;
import static utils.RuleKeys.USE_CASE_DEFINITION_CLASS;
import static utils.RuleKeys.USE_CASE_DEFINITION_METHOD;
import static utils.RuleKeys.USE_CASE_DEFINITION_READ_CONDITION_PRIO;
import static utils.RuleKeys.USE_CASE_DEFINITION_READ_OPERATION_PRIO;
import static utils.RuleKeys.USE_CASE_DEFINITION_READ_SET_OPERATION;
import static utils.RuleKeys.USE_CASE_DEFINITION_READ_VALUE_PRIO;
import static utils.RuleKeys.USE_CASE_DEFINITION_WRITE_CONDITION_PRIO;
import static utils.RuleKeys.USE_CASE_DEFINITION_WRITE_OPERATION_PRIO;
import static utils.RuleKeys.USE_CASE_DEFINITION_WRITE_SET_OPERATION;
import static utils.RuleKeys.USE_CASE_DEFINITION_WRITE_VALUE_PRIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.MethodUsage;

import exceptions.MigrationToolRuntimeException;
import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import model.data.Instance;
import model.data.UseCase;
import model.erm.Entity;
import model.erm.EntityRelation;
import model.erm.EntityRelationDiagram;
import model.erm.RelationType;
import operations.dto.AstDTO;
import rules.engine.RuleEvaluator;
import rules.engine.WildCards;
import utils.PropertiesLoader;

/**
 * Filters the Ast-Tree for the specified definition of an entity/useCase.
 * Generates the model.
 */
public class Analyzer {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Location of configurations file */
	public static final String FILE_NAME = "servicesnipper/servicesnipper.properties";
	/** Properties loader */
	private PropertiesLoader props;
	/** Parsed model */
	private ModelRepresentation model;
	/** All defined entities */
	private List<AstDTO> entities;
	/** All defined useCases */
	private Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases;
	/** The rule engine */
	private RuleEvaluator ruleEngine;

	public Analyzer() {
		this.props = new PropertiesLoader(FILE_NAME);
		this.props.loadProps(false);
		this.model = new ModelRepresentation();
		this.ruleEngine = new RuleEvaluator();
	}

	public void analyze(List<AstDTO> classes) {
		List<AstDTO> filtered = classes.stream().filter(x -> !x.isEnum()).collect(Collectors.toList());
		findAllDefinedEntities(filtered);
		convertAllDefinedEntitiesToModel();
		findAllDefinedEntityRelationships();
		findAllDefinedUseCases(filtered);
		analyzeImplementationOfUseCases();
	}

	private void findAllDefinedEntities(List<AstDTO> classes) {
		LOG.info("Testing all classes for entity definition");
		this.ruleEngine.newStatement(this.props.getCache().get(ENTITY_DEFINITION));
		this.entities = new ArrayList<>();
		for (AstDTO classDTO : classes) {
			LOG.debug("Testing class {}", classDTO.getFullName());
			this.ruleEngine.evaluate(classDTO.getJavaClass(), x -> {
				LOG.debug("{} is an entity", classDTO.getFullName());
				this.entities.add(classDTO);
			});
		}
		LOG.info("Result: {} classes has the defined entity definition", this.entities.size());
	}

	private void convertAllDefinedEntitiesToModel() {
		EntityRelationDiagram erm = this.model.getEntityDiagram();
		for (int i = 0; i < this.entities.size(); i++) {
			AstDTO classDTO = this.entities.get(i);
			Entity e = new Entity(classDTO.getFullName());
			LOG.info("Search for columns of entity: {}", e.getName());
			List<String> attributes = new ArrayList<>();
			attributes.addAll(findAllColumnsByField(classDTO));
			attributes.addAll(findAllColumnsByMethod(classDTO));
			LOG.info("{} columns found", attributes.size());
			e.setAttributes(attributes);
			erm.getEntities().add(e);
		}
	}

	private List<String> findAllColumnsByField(AstDTO entity) {
		this.ruleEngine.newStatement(this.props.getCache().get(ENTITY_COLUMN_DEFINITION_FIELD));
		List<String> attributes = new ArrayList<>();
		LOG.info("Search for column definition in the attributes of {}", entity.getFullName());
		for (FieldDeclaration decl : entity.getFields()) {
			this.ruleEngine.evaluate(decl, x -> {
				for (VariableDeclarator var : x.getVariables()) {
					LOG.debug("{} is an column by the specified definition", var);
					attributes.add(var.getNameAsString());
				}
			});
		}
		return attributes;
	}

	private List<String> findAllColumnsByMethod(AstDTO entity) {
		this.ruleEngine.newStatement(this.props.getCache().get(ENTITY_COLUMN_DEFINITION_METHOD));
		List<String> attributes = new ArrayList<>();
		LOG.info("Search for column definition in the methos of {}", entity.getFullName());
		for (MethodDeclaration decl : entity.getMethods()) {
			this.ruleEngine.evaluate(decl, x -> {
				// Only Supported on getter methods
				String name = x.getNameAsString().substring(3);
				name = name.substring(0, 1).toLowerCase() + name.substring(1);
				LOG.debug("{} is an column by the specified definition", name);
				attributes.add(name);
			});
		}
		return attributes;
	}

	private void findAllDefinedEntityRelationships() {
		EntityRelationDiagram erm = this.model.getEntityDiagram();
		Map<RuleEvaluator, RelationType> engines = new HashMap<>();
		engines.put(new RuleEvaluator(this.props.getCache().get(INHERITENCE_DEFINITION)), INHERITANCE);
		engines.put(new RuleEvaluator(this.props.getCache().get(COMPOSITION_DEFINITION)), COMPOSITION);
		engines.put(new RuleEvaluator(this.props.getCache().get(AGGREGATION_DEFINITION)), AGGREGATION);
		LOG.info("Testing all classes for relationship definition");
		List<EntityRelation> relations = new ArrayList<>();
		for (AstDTO astDTO : this.entities) {
			Entity origin = erm.getEntities().stream().filter(x -> x.getName().equals(astDTO.getFullName()))
					.findFirst().orElseGet(null);
			for (Entity searchedEntity : erm.getEntities()) {
				LOG.debug("Searching relationships of class {} with {}", origin.getName(), searchedEntity.getName());
				List<WildCards> wildCards = List.of(WildCards.ENTITY_NAME.setValue(searchedEntity.getName()));
				for (Entry<RuleEvaluator, RelationType> entry : engines.entrySet()) {
					entry.getKey().evaluate(wildCards, astDTO.getJavaClass(), x -> {
						LOG.debug("Found {} {} ---> {}", entry.getValue(), origin.getName(), searchedEntity.getName());
						relations.add(new EntityRelation(origin, searchedEntity, entry.getValue()));
					});
				}
			}
		}
		// Remove unnecessary duplicates
		removeDuplicatesOfRelationsShips(relations);
		LOG.info("Result: " + relations.size() + " relationships has the defined definition");
		erm.setRelations(relations);
	}

	private void removeDuplicatesOfRelationsShips(List<EntityRelation> relations) {
		List<EntityRelation> duplicates = new ArrayList<>();
		for (EntityRelation relation : relations) {
			for (EntityRelation compare : relations) {
				if (!relation.equals(compare) && relation.getOrigin().equals(compare.getOrigin())
						&& relation.getDestination().equals(compare.getDestination())) {
					// not same object, but same origin and destination
					duplicates.add(relation);
				}
			}
		}
		List<RelationType> orderdList = getRelationShipPriorities();
		List<EntityRelation> toRemove = new ArrayList<>();
		for (EntityRelation relation : duplicates) {
			// lowest value => highest priority
			int priority = orderdList.indexOf(relation.getType());
			for (int j = priority + 1; j < orderdList.size(); j++) {
				for (EntityRelation compare : duplicates) {
					if (compare.getType().equals(orderdList.get(j)) && relation.getOrigin().equals(compare.getOrigin())
							&& relation.getDestination().equals(compare.getDestination())) {
						// not same object, but same origin and destination
						toRemove.add(relation);
					}
				}
			}
		}
		for (EntityRelation entityRelation : toRemove) {
			relations.remove(entityRelation);
		}
	}

	private List<RelationType> getRelationShipPriorities() {
		String temp = this.props.getCache().get(RELATIONSHIP_PRIORITY);
		List<RelationType> res = new ArrayList<>();
		if ((temp != null) && !temp.isBlank()) {
			String[] orderdList = temp.split(";");
			try {
				for (String relationship : orderdList) {
					res.add(RelationType.valueOf(relationship.toUpperCase()));
				}
			} catch (Exception e) {
				throw new MigrationToolRuntimeException(e.getMessage());
			}
		}
		return res;
	}

	private void findAllDefinedUseCases(List<AstDTO> classes) {
		this.ruleEngine.newStatement(this.props.getCache().get(USE_CASE_DEFINITION_CLASS));
		List<AstDTO> useCaseClasses = new ArrayList<>();
		LOG.info("Testing all classes for usecase service class definition");
		for (AstDTO classDTO : classes) {
			LOG.debug("Testing class {}", classDTO.getFullName());
			this.ruleEngine.evaluate(classDTO.getJavaClass(), x -> {
				LOG.debug("{} is an usecase class", classDTO.getFullName());
				useCaseClasses.add(classDTO);
			});
		}
		LOG.info("Result: {} classes has the defined entity definition", useCaseClasses.size());
		// Search correct methods
		this.ruleEngine.newStatement(this.props.getCache().get(USE_CASE_DEFINITION_METHOD));
		this.useCases = new HashMap<>();
		for (AstDTO usecaseClass : useCaseClasses) {
			ClassOrInterfaceDeclaration decl = findImplementationOfClass(usecaseClass, classes);
			if (decl == null) {
				// No available implementation
				continue;
			}
			// Find all methods
			List<MethodDeclaration> allMethods = new ArrayList<>(decl.getMethods());
			String originName = decl.resolve().getQualifiedName();
			// Find all inherited methods
			if (!decl.getExtendedTypes().isEmpty()) {
				LOG.debug("Class {} inheritets classes", originName);
				long methodCount = 0;
				for (MethodDeclaration method : decl.getMethods()) {
					String declaringType = method.resolve().declaringType().getQualifiedName();
					if (!declaringType.equals("java.lang.Object")) {
						methodCount++;
					}
				}
				long methodCountWithInherited = decl.resolve().getAllMethods().stream()
						.filter(x -> !x.declaringType().getQualifiedName().equals("java.lang.Object")).count();
				LOG.debug(
						"Class {} has {} methods, but inheriteds {} methods, which are not from the declaring class object",
						originName, methodCount, methodCountWithInherited);
				if (methodCount < methodCountWithInherited) {
					findInheritedMethods(decl, methodCount, methodCountWithInherited, allMethods, classes);
				}
			}
			// Test all methods against definition

			for (MethodDeclaration method : allMethods) {
				LOG.debug("Testing method {} of class {}", method.getNameAsString(), originName);
				this.ruleEngine.evaluate(method, i -> {
					LOG.debug("{} is an use case method", i.getNameAsString());
					if (!this.useCases.containsKey(decl)) {
						this.useCases.put(decl, new ArrayList<>());
					}
					this.useCases.get(decl).add(i);
				});
			}
		}
	}

	private ClassOrInterfaceDeclaration findImplementationOfClass(AstDTO usecaseClass, List<AstDTO> classes) {
		ClassOrInterfaceDeclaration decl = usecaseClass.getJavaClass();
		if (decl.isInterface()) {
			LOG.debug("{} is an interface, search implementation", usecaseClass.getFullName());
			for (AstDTO classDTO : classes) {
				for (ClassOrInterfaceType impl : classDTO.getImplementations()) {
					if (impl.resolve().getQualifiedName().equals(usecaseClass.getFullName())) {
						return classDTO.getJavaClass();
					}
				}
			}
			LOG.warn("No Implementation found for Interface, drop class {}", decl.getNameAsString());
			return null;
		}
		return decl;
	}

	private void findInheritedMethods(ClassOrInterfaceDeclaration origin, long methodCount, long methodCountInherited,
			List<MethodDeclaration> allMethods, List<AstDTO> classes) {
		int newMethods = 0;
		ClassOrInterfaceDeclaration current = origin;
		do {
			// only classes so there is only one extended type
			if (current.getExtendedTypes().isEmpty()) {
				// No Parent class
				LOG.error("Cannot resolve all inherited methods of class {}", origin.getFullyQualifiedName());
				return;
			}
			ClassOrInterfaceType parentType = current.getExtendedTypes(0);
			for (AstDTO parentClass : classes) {
				if (parentType.resolve().getQualifiedName().equals(parentClass.getFullName())) {
					for (MethodUsage m : origin.resolve().getAllMethods()) {
						// Check if origin has method or is inherited
						if (origin.getMethodsByName(m.getName()).isEmpty()) {
							for (MethodDeclaration method : parentClass.getMethods()) {
								// If not add use case from parent to child
								if (m.getName().equals(method.getNameAsString())) {
									allMethods.add(method);
									newMethods++;
									break;
								}
							}
						}
					}
					current = parentClass.getJavaClass();
					break;
				}
			}
			methodCount += newMethods;
		} while (methodCount < methodCountInherited);
	}

	private void analyzeImplementationOfUseCases() {
		LOG.info("Testing all methods for input/output values");
		Map<Case, Set<String>> listRead = analyzeWithPriorization(
				USE_CASE_DEFINITION_READ_SET_OPERATION,
				USE_CASE_DEFINITION_READ_OPERATION_PRIO,
				USE_CASE_DEFINITION_READ_CONDITION_PRIO,
				USE_CASE_DEFINITION_READ_VALUE_PRIO);
		Map<Case, Set<String>> listWrite = analyzeWithPriorization(
				USE_CASE_DEFINITION_WRITE_SET_OPERATION,
				USE_CASE_DEFINITION_WRITE_OPERATION_PRIO,
				USE_CASE_DEFINITION_WRITE_CONDITION_PRIO,
				USE_CASE_DEFINITION_WRITE_VALUE_PRIO);
		// enhance used usecases
		Set<Case> allCases = new HashSet<>();
		allCases.addAll(listRead.keySet());
		allCases.addAll(listWrite.keySet());
		enhanceWithCalledUseCases(allCases, listRead, listWrite);
		LOG.info("Final state of analyzed use cases");
		ArchitectureInformation ai = this.model.getInformation();
		List<UseCase> results = new ArrayList<>();
		for (Case key : allCases) {
			LOG.info("++++++++++++++++++++++");
			LOG.info("UseCase " + key.getId());
			UseCase useCase = new UseCase();
			useCase.setName(key.getId());
			for (String input : listRead.get(key)) {
				useCase.getInput().add(new Instance(input));
			}
			for (String output : listWrite.get(key)) {
				useCase.getPersistenceChanges().add(new Instance(output));
			}
			if (useCase.getInput().isEmpty() && useCase.getPersistenceChanges().isEmpty()) {
				LOG.info("Drop usecase " + key.getId() + ". No definitions found.");
				continue;
			}
			LOG.info("Read :   {}", useCase.getInputAsString());
			LOG.info("Write:   {}", useCase.getPersistenceChangesAsString());
			results.add(useCase);
		}
		ai.setUseCases(results);
	}

	private Map<Case, Set<String>> analyzeWithPriorization(String setOperation, String operation,
			String condition, String value) {
		Map<Case, List<Set<String>>> temp = new HashMap<>();
		Map<String, String> cache = this.props.getCache();
		int prio = 1;
		while (true) {
			String exprOperation = cache.get(operation + prio);
			String exprCondition = cache.get(condition + prio);
			String exprValue = cache.get(value + prio);
			if ((exprOperation == null) || (exprCondition == null) || (exprValue == null)) {
				break;
			}
			LOG.info("Analyze Use Cases for defintion {}", prio);
			EngineOperations engineOperation = EngineOperations.valueOf(exprOperation.toUpperCase());
			int fixIndex = prio - 1;
			createUseCasesList(prio, temp);
			analyzeUseCaseBody(fixIndex, engineOperation, exprCondition, exprValue, temp);
			prio++;
		}
		LOG.debug("State before combination of priorities");
		for (Entry<Case, List<Set<String>>> entry : temp.entrySet()) {
			LOG.debug("++++++++++++++++++++++");
			LOG.debug("UseCase {}", entry.getKey().getId());
			LOG.debug("Definitions: {}", entry.getValue());
		}
		LOG.debug("State after combination of priorities");
		String exprSetOperation = cache.get(setOperation);
		Map<Case, Set<String>> result = new HashMap<>();
		temp.forEach((x, y) -> {
			result.put(x, SetOperations.valueOf(exprSetOperation).handle(y));
		});
		LOG.debug("Current state after combination of priorities");
		for (Entry<Case, Set<String>> entry : result.entrySet()) {
			LOG.debug("++++++++++++++++++++++");
			LOG.debug("UseCase {}", entry.getKey().getId());
			LOG.debug("Definitions: {}", entry.getValue());
		}
		return result;
	}

	private void enhanceWithCalledUseCases(Set<Case> allCases, Map<Case, Set<String>> listRead,
			Map<Case, Set<String>> listWrite) {
		LOG.info("Enhance with used use cases");
		boolean hasChanged = false;
		do {
			hasChanged = false;
			for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : this.useCases.entrySet()) {
				for (MethodDeclaration method : e.getValue()) {
					AtomicBoolean flag = new AtomicBoolean();
					BlockStmt body = method.getBody().orElse(null);
					if (body != null) {
						body.findAll(MethodCallExpr.class).forEach(x -> {
							for (Case c : allCases) {
								if (x.getNameAsString().equals(c.getUseCase())
										&& x.resolve().getQualifiedName().equals(c.getId())) {
									LOG.debug("Method: {} contains: {}", method.getNameAsString(), x.getNameAsString());
									Case origin = new Case(e.getKey(), method.getNameAsString());
									if (listRead.get(origin) != null) {
										int before = listRead.get(origin).size();
										listRead.get(origin).addAll(listRead.get(c));
										flag.set(listRead.get(origin).size() != before);
									}
									if (listWrite.get(origin) != null) {
										int before = listRead.get(origin).size();
										listWrite.get(origin).addAll(listWrite.get(c));
										flag.set(listRead.get(origin).size() != before);
									}
								}
							}
						});
					}
					hasChanged = hasChanged ? hasChanged : flag.get();
				}
			}
		} while (hasChanged);
	}

	// analyzeUseCaseBody(prio, engineOperation, exprCondition, exprValue,
	// this.useCases, temp);

	private void analyzeUseCaseBody(int current, EngineOperations operation, String condition, String value,
			Map<Case, List<Set<String>>> container) {
		EntityRelationDiagram er = this.model.getEntityDiagram();
		this.ruleEngine.newStatement(condition);
		RuleEvaluator ruleEngineValue = new RuleEvaluator(value);
		// apply rules
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : this.useCases.entrySet()) {
			// check each method
			for (MethodDeclaration m : e.getValue()) {
				String name = e.getKey().resolve().getQualifiedName();
				String mName = m.getNameAsString();
				Case useCase = new Case(e.getKey(), mName);
				// check for each possible entity
				for (Entity classDTO : er.getEntities()) {
					List<WildCards> cards = new ArrayList<>();
					cards.add(ENTITY_NAME.setValue(classDTO.getName()));
					cards.add(ORIGIN_CLASS.setValue(name));
					cards.add(ORIGIN_USECASE.setValue(mName));
					// each use case
					this.ruleEngine.evaluate(cards, m, x -> {
						LOG.debug("Condition successfull / method " + mName + " of class " + name);
						operation.successCondition(this.useCases, ruleEngineValue, m, classDTO, cards, (id, res) -> {
							LOG.debug("Definition found: " + res);
							container.get(id != null ? id : useCase).get(current).add(res);
						});
					});
				}
			}
		}
	}

	private void createUseCasesList(int current, Map<Case, List<Set<String>>> container) {
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : this.useCases.entrySet()) {
			for (MethodDeclaration m : e.getValue()) {
				String mName = m.getNameAsString();
				Case useCase = new Case(e.getKey(), mName);
				if (!container.containsKey(useCase)) {
					container.put(useCase, new ArrayList<>());
				}
				if (container.containsKey(useCase) && (container.get(useCase).size() != current)) {
					container.get(useCase).add(new HashSet<>());
				}
			}
		}
	}

	/**
	 * @return the model
	 */
	public ModelRepresentation getModel() {
		return this.model;
	}

	/**
	 * @return the entities
	 */
	public List<AstDTO> getEntities() {
		return this.entities;
	}

	/**
	 * @return the useCases
	 */
	public Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> getUseCases() {
		return this.useCases;
	}
}
