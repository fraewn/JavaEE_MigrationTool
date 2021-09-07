package rules.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import parser.Grouping;

/**
 * RuleEngine of the migrationtool. Supports a set of possible rules
 * {@link RuleDefinition} and generic data fields {@link WildCards}. It is
 * possible to define more than one rule in a statement. Currently there are
 * four connection types supported: AND, OR, NOT, (Brackets)
 */
public class RuleEvaluator {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Definition of a "and" connection between two rules */
	private static final String AND = "AND";
	/** Definition of a "or" connection between two rules */
	private static final String OR = "OR";
	/** Definition of the negotiation of a rule */
	private static final String NOT = "NOT";
	/** Definition of the used brackets in the statements */
	private static final String BRACKETS = "()";
	/** Definition of the used brackets in a rule */
	private static final String BRACKETS_RULE = "[]";
	/** Definition of the used separator in the statements */
	private static final String SEPARATOR_RULES = " ";
	/** Definition of the used separator in the rules */
	private static final String SEPARATOR_ARGS = ";";
	/** Definition of the assignment operation in the arguments of a rule */
	private static final String ASSIGNMENT_ARGS = "=";

	/** Result of the evaluation of a rule */
	private Map<String, Boolean> evaluates;
	/** Mapping between a generated id and the defined rule */
	private Map<String, Rule> rules;
	/** Mapping between the rule id and a wildcard definition */
	private Map<String, List<UnkownArg>> wildCardDefinitions;
	/** The statement */
	private String statement;

	public RuleEvaluator() {
		this.rules = new HashMap<>();
		this.evaluates = new HashMap<>();
		this.wildCardDefinitions = new HashMap<>();
	}

	public RuleEvaluator(String statement) {
		this();
		newStatement(statement);
	}

	/**
	 * Updates the rule engine to a new statement
	 *
	 * @param statement new statement
	 */
	public void newStatement(String statement) {
		try {
			if ((statement != null) && !statement.isEmpty()) {
				this.evaluates.clear();
				this.rules.clear();
				this.wildCardDefinitions.clear();
				this.statement = statement.replaceAll("[ ]*" + SEPARATOR_ARGS + "[ ]*", SEPARATOR_ARGS);
				LOG.info("Create Rule Definition of statement: " + this.statement);
				convertStatement();
			}
		} catch (RuntimeException e) {
			throw new MigrationToolInitException("Rule Format not correct: " + e.getMessage());
		}
	}

	/**
	 * Resets the result of previous computed results
	 */
	private void reset() {
		for (String id : this.evaluates.keySet()) {
			this.evaluates.put(id, null);
		}
	}

	/**
	 * Convert the statement to the internal structure
	 */
	private void convertStatement() {
		String leftBracket = "" + BRACKETS.charAt(0);
		String rightBracket = "" + BRACKETS.charAt(1);
		String tmp = this.statement.replaceAll("\\" + leftBracket + "|\\" + rightBracket, "");
		String[] list = tmp.split("[" + SEPARATOR_RULES + "]+");
		for (String string : list) {
			if (string.equals(AND) || string.equals(OR) || string.equals(NOT)) {
				continue;
			}
			String leftArgBracket = "" + BRACKETS_RULE.charAt(0);
			if (string.indexOf(leftArgBracket) < 0) {
				throw new MigrationToolInitException("Missing specification of a rule");
			}
			String operation = string.substring(0, string.indexOf(leftArgBracket));
			String id = createDefinition(RuleDefinition.valueOf(operation.toUpperCase()), string);
			this.statement = this.statement.replace(string, id);
		}
		if (this.rules.isEmpty()) {
			throw new MigrationToolInitException("No rules are defined");
		}
	}

	/**
	 * Creates the rule definition and returns the generated id
	 *
	 * @param def    searched rule defintion
	 * @param string full string in statement
	 * @return id of created rule
	 */
	private String createDefinition(RuleDefinition def, String string) {
		String id = UUID.randomUUID().toString().substring(0, 8);
		String leftArgBracket = "" + BRACKETS_RULE.charAt(0);
		String rightArgBracket = "" + BRACKETS_RULE.charAt(1);
		String args = string.substring(string.indexOf(leftArgBracket) + 1, string.indexOf(rightArgBracket));
		String[] tmp = args.split(SEPARATOR_ARGS);
		if (!args.isEmpty() && (tmp.length != def.getRequiredArgs().size())) {
			throw new MigrationToolInitException("Not all required args: " + string);
		}
		Map<String, String> arguments = new HashMap<>();
		if (!args.isEmpty()) {
			for (String s : tmp) {
				String[] keyPair = s.trim().split(ASSIGNMENT_ARGS);
				if (!def.getRequiredArgs().contains(keyPair[0])) {
					throw new MigrationToolInitException("Unknown required arg: " + keyPair[0]);
				}
				if (keyPair.length != 2) {
					throw new MigrationToolInitException("Unexpected length: " + Arrays.toString(keyPair));
				}
				String value = keyPair[1];
				if (value.startsWith(WildCards.IDENTIFIER) && value.endsWith(WildCards.IDENTIFIER)) {
					if (!this.wildCardDefinitions.containsKey(id)) {
						this.wildCardDefinitions.put(id, new ArrayList<>());
					}
					this.wildCardDefinitions.get(id).add(new UnkownArg(keyPair[0], value));
				}
				arguments.put(keyPair[0], value);
			}
		}
		this.rules.put(id, new Rule(id, def, arguments));
		this.evaluates.put(id, null);
		return id;
	}

	/**
	 * Calls a defined function if the rule evaluation is successful
	 *
	 * @param <T>      DTO object
	 * @param input    object for rule evaluation
	 * @param function function called if evaluation is successful
	 */
	public <T extends Visitable> void evaluate(T input, Consumer<T> function) {
		evaluate(null, input, function);
	}

	/**
	 * Calls a defined function if the rule evaluation is successful
	 *
	 * @param <T>       DTO object
	 * @param wildCards possible wildCards
	 * @param input     object for rule evaluation
	 * @param function  function called if evaluation is successful
	 */
	public <T extends Visitable> void evaluate(List<WildCards> wildCards, T input, Consumer<T> function) {
		for (Entry<String, Rule> rule : this.rules.entrySet()) {
			updateWildCards(rule.getValue(), wildCards);
			GenericVisitor<Boolean, ?> visitor = rule.getValue().definition.buildVisitor(rule.getValue().args);
			boolean c = Optional.ofNullable(input.accept(visitor, null)).orElse(false);
			this.evaluates.put(rule.getKey(), c);
		}
		if (eval()) {
			function.accept(input);
		}
		reset();
	}

	/**
	 * Calls a defined function if the rule evaluation is successful, only visitors
	 * with a {@link AtomicInteger} are valid
	 *
	 * @param <T>      DTO object
	 * @param input    object for rule evaluation
	 * @param function function called if evaluation is successful
	 */
	public <T extends Visitable> void recommand(T input, Consumer<Integer> function) {
		recommand(input, new HashMap<>(), function);
	}

	/**
	 * Calls a defined function if the rule evaluation is successful, only visitors
	 * with a {@link AtomicInteger} are valid
	 *
	 * @param <T>      DTO object
	 * @param input    object for rule evaluation
	 * @param groups   current groups
	 * @param function function called if evaluation is successful
	 * @return current groups
	 */
	@SuppressWarnings("unchecked")
	public <T extends Visitable> Map<Integer, String> recommand(T input, Map<Integer, String> groups,
			Consumer<Integer> function) {
		Map<Integer, String> copy = new HashMap<>(groups);
		AtomicInteger res = new AtomicInteger(-1);
		for (Entry<String, Rule> rule : this.rules.entrySet()) {
			GenericVisitor<Boolean, AtomicInteger> visitor = (GenericVisitor<Boolean, AtomicInteger>) rule
					.getValue().definition.buildVisitor(rule.getValue().args);
			if (visitor instanceof Grouping) {
				((Grouping) visitor).setGroups(copy);
			}
			boolean c = Optional.ofNullable(input.accept(visitor, res)).orElse(false);
			this.evaluates.put(rule.getKey(), c);
			if (visitor instanceof Grouping) {
				copy.putAll(((Grouping) visitor).getGroups());
			}
		}
		if (eval()) {
			function.accept(res.get());
		}
		reset();
		return copy;
	}

	/**
	 * Replace a used wildcard with the correct value
	 *
	 * @param rule      rule defintion
	 * @param wildCards possible wildcards
	 */
	private void updateWildCards(Rule rule, List<WildCards> wildCards) {
		if ((wildCards != null) && this.wildCardDefinitions.containsKey(rule.ruleId)) {
			for (UnkownArg wildCard : this.wildCardDefinitions.get(rule.ruleId)) {
				String value = null;
				for (WildCards card : wildCards) {
					if (card.getId().equals(wildCard.wildCard)) {
						value = card.getValue();
						break;
					}
				}
				if (value == null) {
					throw new MigrationToolRuntimeException(
							"Cannot replaced wildcard " + wildCard.wildCard + " at key " + wildCard.key);
				}
				rule.args.put(wildCard.key, value);
			}
		}
	}

	/**
	 * Uses the graal engine to determine the validation of a defined statement
	 *
	 * @return statement valid
	 */
	private boolean eval() {
		for (Boolean val : this.evaluates.values()) {
			if (val == null) {
				throw new MigrationToolRuntimeException("Not all expressions evaluated: " + this.statement);
			}
		}
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("graal.js");
		String myExpression = this.statement.replaceAll(AND, "&&").replaceAll(OR, "||").replaceAll(NOT, "!");
		for (Map.Entry<String, Boolean> entry : this.evaluates.entrySet()) {
			myExpression = myExpression.replace(entry.getKey(), "" + entry.getValue());
		}
		try {
			boolean res = (boolean) se.eval(myExpression);
			return res;
		} catch (ScriptException e) {
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}

	/**
	 * Wrapper class of a rule
	 */
	class Rule {
		/** id of rule */
		String ruleId;
		/** corresponding definition */
		RuleDefinition definition;
		/** id of rule */
		Map<String, String> args;

		public Rule(String ruleId, RuleDefinition definition, Map<String, String> args) {
			this.ruleId = ruleId;
			this.definition = definition;
			this.args = new HashMap<>(args);
		}
	}

	/**
	 * Wrapper class of a wildcard
	 */
	class UnkownArg {
		/** id of rule */
		String key;
		/** name of wildcard */
		String wildCard;

		public UnkownArg(String key, String wildCard) {
			this.key = key;
			this.wildCard = wildCard;
		}
	}
}
