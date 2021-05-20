package rules;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import exceptions.MigrationToolRuntimeException;

public class RuleEvaluator {
	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(RuleEvaluator.class);

	private static final String AND = "AND";
	private static final String OR = "OR";
	private static final String NOT = "NOT";

	private Map<String, Boolean> evaluates;
	private Map<String, RuleDefinition> rules;

	private String statement;

	public RuleEvaluator(String statement) {
		this.statement = statement.replaceAll(", ", ",");
		this.rules = new HashMap<>();
		this.evaluates = new HashMap<>();
		convertStatement();
	}

	private void convertStatement() {
		String tmp = this.statement.replaceAll("\\(|\\)", "");
		String[] list = tmp.split(" ");
		for (String string : list) {
			if (string.equals(AND) || string.equals(OR) || string.equals(NOT)) {
				continue;
			}
			switch (string.substring(0, string.indexOf("["))) {
			case "annotation":
				String id = createDefinition(RuleDefinition.ANNOTATION, string);
				this.statement = this.statement.replace(string, id);
				break;
			default:
				break;
			}
		}
	}

	private String createDefinition(RuleDefinition def, String string) {
		String id = UUID.randomUUID().toString().substring(0, 8);
		String args = string.substring(string.indexOf("[") + 1, string.indexOf("]"));
		String[] tmp = args.split(",");
		if (tmp.length != def.getRequiredArgs().size()) {
			throw new MigrationToolRuntimeException("Not all required args: " + string);
		}
		for (String s : tmp) {
			String[] keyPair = s.trim().split("=");
			if (!def.getRequiredArgs().contains(keyPair[0])) {
				throw new MigrationToolRuntimeException("Unknown required arg: " + keyPair[0]);
			}
			def.getArgs().put(keyPair[0], keyPair[1]);
		}
		this.rules.put(id, def);
		this.evaluates.put(id, null);
		return id;
	}

	public void updateExpression(String id, boolean value) {
		this.evaluates.put(id, Boolean.valueOf(value));
	}

	/**
	 * @return the rules
	 */
	public Map<String, RuleDefinition> getRules() {
		return this.rules;
	}

	public boolean eval() {
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
		LOG.info("Evaluate statement: " + this.statement);
		try {
			return (boolean) se.eval(myExpression);
		} catch (ScriptException e) {
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}
}
