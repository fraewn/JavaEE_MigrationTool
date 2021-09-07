package validator.extension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import analyzer.Analyzer;
import analyzer.EngineOperations;
import analyzer.SetOperations;
import exceptions.MigrationToolArgumentException;
import model.erm.RelationType;
import operations.Validator;
import rules.engine.RuleEvaluator;
import utils.RuleKeys;

/**
 * Validator of the file {@link Analyzer#FILE_NAME}
 */
public class AnalyzeConfigurationValidator implements Validator {

	@Override
	public boolean isOptional() {
		return false;
	}

	@Override
	public String configFile() {
		return Analyzer.FILE_NAME;
	}

	@Override
	public void validate(Map<String, String> cache) throws MigrationToolArgumentException {
		List<String> setOperations = List.of(
				RuleKeys.USE_CASE_DEFINITION_READ_SET_OPERATION,
				RuleKeys.USE_CASE_DEFINITION_WRITE_SET_OPERATION);
		for (String op : setOperations) {
			try {
				SetOperations.valueOf(cache.get(op));
			} catch (Exception e) {
				throw new MigrationToolArgumentException(
						op + " is not a valid value; Possibilites " + Arrays.toString(SetOperations.values()));
			}
		}
		String rulePriorities = cache.get(RuleKeys.RELATIONSHIP_PRIORITY);
		for (String op : rulePriorities.split(";")) {
			try {
				RelationType.valueOf(op.toUpperCase());
			} catch (Exception e) {
				throw new MigrationToolArgumentException(
						op + " is not a valid value; Possibilites " + Arrays.toString(RelationType.values()));
			}
		}
		List<String> allStatements = new ArrayList<>();
		allStatements.add(RuleKeys.ENTITY_DEFINITION);
		allStatements.add(RuleKeys.ENTITY_COLUMN_DEFINITION_FIELD);
		allStatements.add(RuleKeys.ENTITY_COLUMN_DEFINITION_METHOD);

		allStatements.add(RuleKeys.AGGREGATION_DEFINITION);
		allStatements.add(RuleKeys.COMPOSITION_DEFINITION);
		allStatements.add(RuleKeys.INHERITENCE_DEFINITION);

		allStatements.add(RuleKeys.USE_CASE_DEFINITION_CLASS);
		allStatements.add(RuleKeys.USE_CASE_DEFINITION_METHOD);

		boolean hasPrio = true;
		int z = 1;
		while (hasPrio) {
			hasPrio = getPrioValues(
					RuleKeys.USE_CASE_DEFINITION_READ_OPERATION_PRIO + z,
					RuleKeys.USE_CASE_DEFINITION_READ_CONDITION_PRIO + z,
					RuleKeys.USE_CASE_DEFINITION_READ_VALUE_PRIO + z,
					allStatements, cache);
			z++;
		}
		hasPrio = true;
		z = 1;
		while (hasPrio) {
			hasPrio = getPrioValues(
					RuleKeys.USE_CASE_DEFINITION_WRITE_OPERATION_PRIO + z,
					RuleKeys.USE_CASE_DEFINITION_WRITE_CONDITION_PRIO + z,
					RuleKeys.USE_CASE_DEFINITION_WRITE_VALUE_PRIO + z,
					allStatements, cache);
			z++;
		}
		RuleEvaluator ruleEngine = new RuleEvaluator();
		for (String key : allStatements) {
			String expr = cache.get(key);
			if (expr == null) {
				throw new MigrationToolArgumentException(key + " has no value");
			}
			try {
				ruleEngine.newStatement(expr);
			} catch (Exception e) {
				throw new MigrationToolArgumentException(key + " " + e.getMessage());
			}

		}
	}

	private boolean getPrioValues(String operation, String condition, String value,
			List<String> allStatements, Map<String, String> cache) {
		String exprOperation = cache.get(operation);
		String exprCondition = cache.get(operation);
		String exprValue = cache.get(operation);
		if ((exprOperation == null) || (exprCondition == null) || (exprValue == null)) {
			if ((exprOperation == null) && (exprCondition == null) && (exprValue == null)) {
				return false;
			}
			throw new MigrationToolArgumentException("Configuration package is not complete; " + operation);
		}
		try {
			EngineOperations.valueOf(exprOperation.toUpperCase());
		} catch (Exception e) {
			throw new MigrationToolArgumentException(
					operation + " is not a valid value; Possibilites " + Arrays.toString(EngineOperations.values()));
		}
		allStatements.add(condition);
		allStatements.add(value);
		return true;
	}
}
