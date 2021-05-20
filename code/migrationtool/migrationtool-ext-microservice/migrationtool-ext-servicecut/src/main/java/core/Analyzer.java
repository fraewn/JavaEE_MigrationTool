package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import model.ModelRepresentation;
import model.erm.Entity;
import model.erm.EntityRelationDiagram;
import operations.dto.ClassDTO;
import rules.RuleDefinition;
import rules.RuleEvaluator;
import rules.VisitorFactory;
import utils.PropertiesLoader;
import utils.PropertyKeys;

public class Analyzer {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(Analyzer.class);

	private static final String FILE_NAME = "servicecutter/servicecutter.properties";

	private List<ClassDTO> classes;

	private PropertiesLoader props;

	private ModelRepresentation rep;

	public Analyzer(List<ClassDTO> classes) {
		this.classes = classes;
		this.props = new PropertiesLoader(FILE_NAME);
		this.props.loadProps();
		this.rep = new ModelRepresentation();
	}

	public void convertInput() {
		Map<String, String> cache = this.props.getCache();
		convertEntities(cache);
	}

	private void convertEntities(Map<String, String> cache) {
		EntityRelationDiagram erm = new EntityRelationDiagram();
		this.rep.setEntityDiagram(erm);
		List<ClassDTO> entity = new ArrayList<>();
		String expr = cache.get(PropertyKeys.ENTITY_DEFINITION);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		// Find all entites
		for (ClassDTO classDTO : this.classes) {
			LOG.info("Testing class " + classDTO.getFullName() + " for entity definition");
			evaluate(ruleEngine, classDTO.getJavaClass(), x -> {
				entity.add(classDTO);
			});
		}
		for (ClassDTO classDTO : entity) {
			Entity e = new Entity(classDTO.getFullName());
			List<String> attributes = new ArrayList<>();
			attributes.addAll(convertColumnsOfField(cache, classDTO));
			attributes.addAll(convertColumnsOfMethod(cache, classDTO));
			e.setAttributes(attributes);
			erm.getEntities().add(e);
		}
	}

	private List<String> convertColumnsOfField(Map<String, String> cache, ClassDTO entity) {
		List<String> attributes = new ArrayList<>();
		String expr = cache.get(PropertyKeys.ENTITY_COLUMN_DEFINITION_FIELD);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		for (FieldDeclaration decl : entity.getFields()) {
			evaluate(ruleEngine, decl, x -> {
				for (VariableDeclarator var : x.getVariables()) {
					attributes.add(var.getNameAsString());
				}
			});
		}
		return attributes;
	}

	private List<String> convertColumnsOfMethod(Map<String, String> cache, ClassDTO entity) {
		List<String> attributes = new ArrayList<>();
		String expr = cache.get(PropertyKeys.ENTITY_COLUMN_DEFINITION_METHOD);
		RuleEvaluator ruleEngine = new RuleEvaluator(expr);
		for (MethodDeclaration decl : entity.getMethods()) {
			evaluate(ruleEngine, decl, x -> {
				// Name from getter
				String name = x.getNameAsString().substring(3);
				char c[] = name.toCharArray();
				c[0] = Character.toLowerCase(c[0]);
				name = new String(c);
				attributes.add(name);
			});
		}
		return attributes;
	}

	private <T extends Visitable> void evaluate(RuleEvaluator ruleEngine, T input, Consumer<T> function) {
		for (Entry<String, RuleDefinition> rule : ruleEngine.getRules().entrySet()) {
			GenericVisitor<Boolean, ?> visitor = VisitorFactory.buildVisitor(rule.getValue());
			boolean c = Optional.ofNullable(input.accept(visitor, null)).orElse(false);
			ruleEngine.updateExpression(rule.getKey(), c);
		}
		if (ruleEngine.eval()) {
			LOG.info("Success");
			function.accept(input);
		} else {
			LOG.info("FAILED");
		}
	}

	public ModelRepresentation getOutput() {
		return this.rep;
	}
}
