package core;

import static rules.WildCards.ENTITY_NAME;
import static rules.WildCards.ORIGIN_CLASS;
import static rules.WildCards.ORIGIN_USECASE;
import static utils.PropertyKeys.USE_CASE_DEFINITION_READ_CONDITION_PRIO;
import static utils.PropertyKeys.USE_CASE_DEFINITION_READ_OPERATION_PRIO;
import static utils.PropertyKeys.USE_CASE_DEFINITION_READ_SET_OPERATION;
import static utils.PropertyKeys.USE_CASE_DEFINITION_READ_VALUE_PRIO;
import static utils.PropertyKeys.USE_CASE_DEFINITION_WRITE_CONDITION_PRIO;
import static utils.PropertyKeys.USE_CASE_DEFINITION_WRITE_OPERATION_PRIO;
import static utils.PropertyKeys.USE_CASE_DEFINITION_WRITE_SET_OPERATION;
import static utils.PropertyKeys.USE_CASE_DEFINITION_WRITE_VALUE_PRIO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.MethodUsage;

import cmd.progressbar.CommandLineProgressbar;
import model.ModelRepresentation;
import model.data.ArchitectureInformation;
import model.data.Instance;
import model.data.UseCase;
import model.erm.Entity;
import model.erm.EntityRelation;
import model.erm.EntityRelationDiagram;
import model.erm.RelationType;
import operations.dto.ClassDTO;
import rules.EngineOperations;
import rules.SetOperations;
import rules.WildCards;
import rules.engine.Case;
import rules.engine.RuleEvaluator;
import utils.PropertiesLoader;
import utils.PropertyKeys;

public class Analyzer {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Analyzer.class);

	private static final String FILE_NAME = "servicecutter/servicecutter.properties";

	private List<ClassDTO> classes;

	private PropertiesLoader props;

	private ModelRepresentation rep;

	private List<ClassDTO> entity;

	public Analyzer(List<ClassDTO> classes) {
		this.classes = classes;
		this.props = new PropertiesLoader(FILE_NAME);
		this.props.loadProps(false);
		this.rep = new ModelRepresentation();
		EntityRelationDiagram erm = new EntityRelationDiagram();
		this.rep.setEntityDiagram(erm);
		ArchitectureInformation info = new ArchitectureInformation();
		this.rep.setInformation(info);
	}

	public void convertInput() {
		this.entity = findEntitis();
		convertEntities();
		convertEntityRelations();
		convertUseCases();
	}

	public ModelRepresentation getOutput() {
		return this.rep;
	}

	private List<ClassDTO> findEntitis() {
		String expr = this.props.getCache().get(PropertyKeys.ENTITY_DEFINITION);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		// Find all entites
		List<ClassDTO> entity = new ArrayList<>();
		LOG.info("Testing all classes for entity definition");
		for (ClassDTO classDTO : this.classes) {
			LOG.debug("Testing class " + classDTO.getFullName());
			ruleEngine.evaluate(classDTO.getJavaClass(), x -> {
				LOG.debug(classDTO.getFullName() + " is an entity");
				entity.add(classDTO);
			});
		}
		LOG.info("Result: " + this.classes.size() + " classes has the defined entity definition");
		return entity;
	}

	private void convertEntities() {
		EntityRelationDiagram erm = this.rep.getEntityDiagram();
		CommandLineProgressbar progress = new CommandLineProgressbar();
		for (int i = 0; i < this.entity.size(); i++) {
			ClassDTO classDTO = this.entity.get(i);
			Entity e = new Entity(classDTO.getFullName());
			LOG.info("Search for columns of entity: " + e.getName());
			List<String> attributes = new ArrayList<>();
			attributes.addAll(convertColumnsOfField(classDTO));
			attributes.addAll(convertColumnsOfMethod(classDTO));
			e.setAttributes(attributes);
			erm.getEntities().add(e);
			progress.setProgressbar((int) (((double) i / this.classes.size()) * 100));
		}
	}

	private List<String> convertColumnsOfField(ClassDTO entity) {
		List<String> attributes = new ArrayList<>();
		String expr = this.props.getCache().get(PropertyKeys.ENTITY_COLUMN_DEFINITION_FIELD);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		LOG.info("Testing " + entity.getFullName() + " for column definition of field");
		for (FieldDeclaration decl : entity.getFields()) {
			ruleEngine.evaluate(decl, x -> {
				for (VariableDeclarator var : x.getVariables()) {
					LOG.debug(var + " is an column");
					attributes.add(var.getNameAsString());
				}
			});
		}
		LOG.info(attributes.size() + " columns found");
		return attributes;
	}

	private List<String> convertColumnsOfMethod(ClassDTO entity) {
		List<String> attributes = new ArrayList<>();
		String expr = this.props.getCache().get(PropertyKeys.ENTITY_COLUMN_DEFINITION_METHOD);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		LOG.info("Testing " + entity.getFullName() + " for column definition of method");
		for (MethodDeclaration decl : entity.getMethods()) {
			ruleEngine.evaluate(decl, x -> {
				// Name from getter
				String name = x.getNameAsString().substring(3);
				char c[] = name.toCharArray();
				c[0] = Character.toLowerCase(c[0]);
				name = new String(c);
				LOG.debug(name + " is an column");
				attributes.add(name);
			});
		}
		LOG.info(attributes.size() + " columns found");
		return attributes;
	}

	private void convertEntityRelations() {
		EntityRelationDiagram erm = this.rep.getEntityDiagram();
		List<EntityRelation> relations = new ArrayList<>();
		String exprAggr = this.props.getCache().get(PropertyKeys.AGGREGATION_DEFINITION);
		RuleEvaluator ruleEngineAggr = new RuleEvaluator(exprAggr);
		String exprComp = this.props.getCache().get(PropertyKeys.COMPOSITION_DEFINITION);
		RuleEvaluator ruleEngineComp = new RuleEvaluator(exprComp);
		String exprInhe = this.props.getCache().get(PropertyKeys.INHERITENCE_DEFINITION);
		RuleEvaluator ruleEngineInhe = new RuleEvaluator(exprInhe);
		LOG.info("Testing all classes for relationship definition");
		CommandLineProgressbar progress = new CommandLineProgressbar();
		for (int i = 0; i < this.entity.size(); i++) {
			ClassDTO classDTO = this.entity.get(i);
			Entity origin = erm.getEntities().stream().filter(x -> x.getName().equals(classDTO.getFullName()))
					.findFirst().orElseGet(null);
			for (Entity searchedEntity : erm.getEntities()) {
				LOG.debug("Searching relationships of class " + origin.getName() + " with " + searchedEntity.getName());
				WildCards w = WildCards.ENTITY_NAME;
				w.setValue(searchedEntity.getName());
				List<WildCards> cards = new ArrayList<>(Collections.singletonList(w));
				// COMPOSITION
				ruleEngineComp.evaluate(cards, classDTO.getJavaClass(), x -> {
					LOG.debug("Found composition " + origin.getName() + " ---> " + searchedEntity.getName());
					relations.add(new EntityRelation(origin, searchedEntity, RelationType.COMPOSITION));
				});
				// INHERITANCE
				ruleEngineInhe.evaluate(cards, classDTO.getJavaClass(), x -> {
					LOG.debug("Found inheritance " + origin.getName() + " ---> " + searchedEntity.getName());
					relations.add(new EntityRelation(origin, searchedEntity, RelationType.INHERITANCE));
				});
				// AGGREGATION
				ruleEngineAggr.evaluate(cards, classDTO.getJavaClass(), x -> {
					LOG.debug("Found aggregation " + origin.getName() + " ---> " + searchedEntity.getName());
					if (relations.contains(new EntityRelation(origin, searchedEntity, RelationType.COMPOSITION))) {
						LOG.debug("Found composition of same relationship, drop aggregation");
					} else {
						relations.add(new EntityRelation(origin, searchedEntity, RelationType.AGGREGATION));
					}
				});
			}
			progress.setProgressbar((int) (((double) i / this.classes.size()) * 100));
		}
		LOG.info("Result: " + relations.size() + " relationships has the defined definition");
		erm.setRelations(relations);
	}

	private void convertUseCases() {
		// Search possible classes
		String expr = this.props.getCache().get(PropertyKeys.USE_CASE_DEFINITION_CLASS);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		List<ClassDTO> useCaseClasses = new ArrayList<>();
		LOG.info("Testing all classes for use case definition");
		for (ClassDTO classDTO : this.classes) {
			LOG.debug("Testing class " + classDTO.getFullName());
			ruleEngine.evaluate(classDTO.getJavaClass(), x -> {
				LOG.debug(classDTO.getFullName() + " is an use case class");
				useCaseClasses.add(classDTO);
			});
		}
		// Search correct methods
		String exprMethod = this.props.getCache().get(PropertyKeys.USE_CASE_DEFINITION_METHOD);
		RuleEvaluator ruleEngineMethod = new RuleEvaluator(exprMethod);
		Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases = new HashMap<>();
		for (ClassDTO useCaseClass : useCaseClasses) {
			ClassOrInterfaceDeclaration decl = useCaseClass.getJavaClass();
			// Check Interface definition and find implementation
			if (decl.isInterface()) {
				LOG.debug(useCaseClass.getFullName() + " is an interface, search implementation");
				boolean findImpl = false;
				for (ClassDTO classDTO : this.classes) {
					for (ClassOrInterfaceType impl : classDTO.getImplementations()) {
						if (impl.resolve().getQualifiedName().equals(useCaseClass.getFullName())) {
							findImpl = true;
							decl = classDTO.getJavaClass();
							break;
						}
					}
				}
				if (!findImpl) {
					LOG.warn("No Implementation found for Interface, drop class " + decl.getNameAsString());
					continue;
				}
			}
			// Find all methods
			ClassOrInterfaceDeclaration origin = decl;
			List<MethodDeclaration> allMethods = new ArrayList<>(origin.getMethods());
			String originName = origin.resolve().getQualifiedName();
			// Find all inherited methods
			if (!origin.getExtendedTypes().isEmpty()) {
				LOG.debug("Class " + originName + " inheritets classes");
				// For each parent class
				int methodCount = origin.getMethods().size();
				int methodCountInherited = origin.resolve().getAllMethods().size();
				if (methodCount < methodCountInherited) {
					findInheritedMethods(origin, methodCount, methodCountInherited, allMethods);
				}
			}
			// Test methods against definition
			for (MethodDeclaration method : allMethods) {
				LOG.debug("Testing method " + method.getNameAsString() + " of class " + originName);
				ruleEngineMethod.evaluate(method, i -> {
					LOG.debug(i.getNameAsString() + " is an use case method");
					if (!useCases.containsKey(origin)) {
						useCases.put(origin, new ArrayList<>());
					}
					useCases.get(origin).add(i);
				});
			}
		}
		analyzeImplOfUseCases(useCases);
	}

	private void findInheritedMethods(ClassOrInterfaceDeclaration origin, int methodCount, int methodCountInherited,
			List<MethodDeclaration> allMethods) {
		int newMethods = 0;
		List<ClassOrInterfaceDeclaration> validParents = new ArrayList<>();
		for (ClassOrInterfaceType parent : origin.getExtendedTypes()) {
			for (ClassDTO classDTO : this.classes) {
				if (parent.resolve().getQualifiedName().equals(classDTO.getFullName())) {
					// Check if method is overriden
					for (MethodUsage m : origin.resolve().getAllMethods()) {
						if (origin.getMethodsByName(m.getName()).isEmpty()) {
							for (MethodDeclaration method : classDTO.getMethods()) {
								// If not add use case to from parent to child
								if (m.getName().equals(method.getNameAsString())) {
									allMethods.add(method);
									newMethods++;
									break;
								}
							}
						}
					}
					break;
				}
			}
		}
		methodCount += newMethods;
		if (methodCount < methodCountInherited) {
			for (ClassOrInterfaceDeclaration c : validParents) {
				findInheritedMethods(c, methodCount, methodCountInherited, allMethods);
			}
		}
	}

	private void analyzeImplOfUseCases(Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases) {
		LOG.info("Testing all methods for input/output values");
		Map<String, String> cache = this.props.getCache();
		// muliple definitions possible; starting with prio 1
		int prio = 1;
		boolean hasReadPrio = true;
		boolean hasWritePrio = true;
		// save list
		Map<Case, List<Set<String>>> listRead = new HashMap<>();
		Map<Case, List<Set<String>>> listWrite = new HashMap<>();
		// Define set operation
		String exprReadSetOperation = cache.get(USE_CASE_DEFINITION_READ_SET_OPERATION);
		String exprWriteSetOperation = cache.get(USE_CASE_DEFINITION_WRITE_SET_OPERATION);
		while (hasReadPrio || hasWritePrio) {
			// Update rules
			String exprReadOperation = cache.get(USE_CASE_DEFINITION_READ_OPERATION_PRIO + prio);
			String exprReadCondition = cache.get(USE_CASE_DEFINITION_READ_CONDITION_PRIO + prio);
			String exprReadValue = cache.get(USE_CASE_DEFINITION_READ_VALUE_PRIO + prio);
			if ((exprReadOperation == null) || (exprReadCondition == null) || (exprReadValue == null)) {
				hasReadPrio = false;
			}
			if (hasReadPrio) {
				LOG.info("Analyze Use Cases for read defintion " + prio);
				analyzeUseCaseBody(prio, EngineOperations.valueOf(exprReadOperation.toUpperCase()), exprReadCondition,
						exprReadValue, useCases, listRead);
			}
			String exprWriteOperation = cache.get(USE_CASE_DEFINITION_WRITE_OPERATION_PRIO + prio);
			String exprWriteCondition = cache.get(USE_CASE_DEFINITION_WRITE_CONDITION_PRIO + prio);
			String exprWriteValue = cache.get(USE_CASE_DEFINITION_WRITE_VALUE_PRIO + prio);
			if ((exprWriteOperation == null) || (exprWriteCondition == null) || (exprWriteValue == null)) {
				hasWritePrio = false;
			}
			if (hasWritePrio) {
				LOG.info("Analyze Use Cases for write defintion " + prio);
				analyzeUseCaseBody(prio, EngineOperations.valueOf(exprWriteOperation.toUpperCase()), exprWriteCondition,
						exprWriteValue, useCases, listWrite);
			}
			prio++;
		}

		Map<Case, Set<String>> readPerUseCase = new HashMap<>();
		Map<Case, Set<String>> writePerUseCase = new HashMap<>();
		LOG.debug("State before combination of priorities");
		Set<Case> keys = new HashSet<>(listRead.keySet());
		keys.addAll(listWrite.keySet());
		for (Case key : keys) {
			LOG.debug("++++++++++++++++++++++");
			LOG.debug("UseCase " + key.getId());
			LOG.debug("Read:   " + listRead.get(key));
			LOG.debug("Write:  " + listWrite.get(key));
		}
		listRead.forEach((x, y) -> {
			readPerUseCase.put(x, SetOperations.valueOf(exprReadSetOperation).handle(y));
		});
		listWrite.forEach((x, y) -> {
			writePerUseCase.put(x, SetOperations.valueOf(exprWriteSetOperation).handle(y));
		});
		LOG.debug("Current state after combination of priorities");
		for (Case key : keys) {
			LOG.debug("++++++++++++++++++++++");
			LOG.debug("UseCase " + key.getId());
			LOG.debug("Read:   " + readPerUseCase.get(key));
			LOG.debug("Write:  " + writePerUseCase.get(key));
		}
		LOG.info("Enhance with used use cases");
		boolean hasChanged = false;
		do {
			hasChanged = false;
			for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : useCases.entrySet()) {
				for (MethodDeclaration m : e.getValue()) {
					AtomicBoolean flag = new AtomicBoolean(hasChanged);
					m.findAll(MethodCallExpr.class).forEach(x -> {
						for (Case c : keys) {
							String searched = c.getId();
							if (x.getNameAsString().equals(c.getUseCase())) {
								if (x.resolve().getQualifiedName().equals(searched)) {
									LOG.debug("Method: " + m.getNameAsString() + " contains: " + x.getNameAsString());
									Case origin = new Case(e.getKey(), m.getNameAsString());
									if (readPerUseCase.get(origin) != null) {
										readPerUseCase.get(origin).addAll(readPerUseCase.get(c));
										flag.set(true);
									}
									if (writePerUseCase.get(origin) != null) {
										writePerUseCase.get(origin).addAll(writePerUseCase.get(c));
										flag.set(true);
									}
								}
							}
						}
					});
				}
			}
		} while (hasChanged);
		LOG.info("Final state of analyzed use cases");
		ArchitectureInformation ai = this.rep.getInformation();
		List<UseCase> results = new ArrayList<>();
		for (Case key : keys) {
			LOG.info("++++++++++++++++++++++");
			LOG.info("UseCase " + key.getId());
			if (readPerUseCase.get(key).isEmpty() && writePerUseCase.get(key).isEmpty()) {
				LOG.info("Drop usecase " + key.getId() + ". No definitions found.");
				continue;
			}
			LOG.info("Read:   " + readPerUseCase.get(key));
			LOG.info("Write:  " + writePerUseCase.get(key));
			UseCase useCase = new UseCase();
			useCase.setName(key.getId());
			for (String input : readPerUseCase.get(key)) {
				useCase.getInput().add(new Instance(input));
			}
			for (String output : writePerUseCase.get(key)) {
				useCase.getPersistenceChanges().add(new Instance(output));
			}
			results.add(useCase);
		}
		ai.setUseCases(results);
	}

	private void analyzeUseCaseBody(int current, EngineOperations operation, String condition, String value,
			Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases,
			Map<Case, List<Set<String>>> container) {
		EntityRelationDiagram er = this.rep.getEntityDiagram();
		int z = current - 1;
		RuleEvaluator ruleEngineCond = new RuleEvaluator(condition);
		RuleEvaluator ruleEngineValue = new RuleEvaluator(value);
		createAllUseCasesInList(current, container, useCases);
		CommandLineProgressbar progress = new CommandLineProgressbar();
		int i = 0;
		// apply rules
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : useCases.entrySet()) {
			// check each method
			for (MethodDeclaration m : e.getValue()) {
				String mName = m.getNameAsString();
				Case useCase = new Case(e.getKey(), mName);
				// check for each possible entity
				for (Entity classDTO : er.getEntities()) {
					String name = e.getKey().resolve().getQualifiedName();
					List<WildCards> cards = new ArrayList<>();
					cards.add(ENTITY_NAME.setValue(classDTO.getName()));
					cards.add(ORIGIN_CLASS.setValue(name));
					cards.add(ORIGIN_USECASE.setValue(mName));
					// each use case
					ruleEngineCond.evaluate(cards, m, x -> {
						LOG.debug("Condition successfull / method " + mName + " of class " + name);
						operation.successCondition(useCases, ruleEngineValue, m, classDTO, cards, (id, res) -> {
							LOG.debug("Definition found: " + res);
							container.get(id != null ? id : useCase).get(z).add(res);
						});
					});
				}
			}
			progress.setProgressbar((int) (((double) i / useCases.size()) * 100));
			i++;
		}
	}

	private void createAllUseCasesInList(int current, Map<Case, List<Set<String>>> container,
			Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> useCases) {
		for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e : useCases.entrySet()) {
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
}
