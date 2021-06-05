package rules.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import exceptions.MigrationToolInitException;
import exceptions.MigrationToolRuntimeException;
import rules.RuleDefinition;
import rules.WildCards;

public class RuleEvaluator {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(RuleEvaluator.class);

	private static final String AND = "AND";
	private static final String OR = "OR";
	private static final String NOT = "NOT";

	private Map<String, Boolean> evaluates;
	private Map<String, Rule> rules;
	private Map<String, List<UnkownArg>> wildCardDefinitions;

	private String statement;

	public RuleEvaluator(String statement) {
		if ((statement != null) && !statement.isEmpty()) {
			this.statement = statement.replaceAll("; ", ";");
			this.rules = new HashMap<>();
			this.evaluates = new HashMap<>();
			this.wildCardDefinitions = new HashMap<>();
			LOG.info("Create Rule Definition of statement: " + this.statement);
			convertStatement();
			LOG.info("Converted Definition of statement: " + this.statement);
		}
	}

	private void convertStatement() {
		String tmp = this.statement.replaceAll("\\(|\\)", "");
		String[] list = tmp.split(" ");
		for (String string : list) {
			if (string.equals(AND) || string.equals(OR) || string.equals(NOT)) {
				continue;
			}
			String operation = string.substring(0, string.indexOf("["));
			String id = createDefinition(RuleDefinition.valueOf(operation.toUpperCase()), string);
			this.statement = this.statement.replace(string, id);
		}
	}

	private String createDefinition(RuleDefinition def, String string) {
		String id = UUID.randomUUID().toString().substring(0, 8);
		String args = string.substring(string.indexOf("[") + 1, string.indexOf("]"));
		String[] tmp = args.split(";");
		if (!args.isEmpty() && (tmp.length != def.getRequiredArgs().size())) {
			throw new MigrationToolInitException("Not all required args: " + string);
		}
		Map<String, String> arguments = new HashMap<>();
		if (!args.isEmpty()) {
			for (String s : tmp) {
				String[] keyPair = s.trim().split("=");
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

	public <T extends Visitable> void evaluate(T input, Consumer<T> function) {
		evaluate(null, input, function);
	}

	public <T extends Visitable> void evaluate(List<WildCards> wildCards, T input, Consumer<T> function) {
		for (Entry<String, Rule> rule : this.rules.entrySet()) {
			updateWildCards(rule.getValue(), wildCards);
			GenericVisitor<Boolean, ?> visitor = VisitorFactory.buildVisitor(rule.getValue().definition,
					rule.getValue().args);
			boolean c = Optional.ofNullable(input.accept(visitor, null)).orElse(false);
			this.evaluates.put(rule.getKey(), c);
		}
		if (eval()) {
			function.accept(input);
		}
	}

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
//			LOG.debug("Evaluate statement: " + this.statement);
//			LOG.debug("Updated statement: " + myExpression);
			boolean res = (boolean) se.eval(myExpression);
//			LOG.debug("Result: " + (res ? "SUCCESS" : "FAILED"));
			return res;
		} catch (ScriptException e) {
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}

	class Rule {
		String ruleId;
		RuleDefinition definition;
		Map<String, String> args;

		public Rule(String ruleId, RuleDefinition definition, Map<String, String> args) {
			this.ruleId = ruleId;
			this.definition = definition;
			this.args = new HashMap<>(args);
		}
	}

	class UnkownArg {
		String key;
		String wildCard;

		public UnkownArg(String key, String wildCard) {
			this.key = key;
			this.wildCard = wildCard;
		}
	}
}
