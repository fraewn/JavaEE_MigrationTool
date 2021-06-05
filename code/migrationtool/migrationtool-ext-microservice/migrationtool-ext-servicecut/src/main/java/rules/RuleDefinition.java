package rules;

import java.util.Arrays;
import java.util.List;

public enum RuleDefinition {
	/**
	 *
	 */
	TAUTOLOGY(),
	/**
	 *
	 */
	ANNOTATION("name", "type"),
	/**
	 *
	 */
	INHERITENCE("name"),
	/**
	 *
	 */
	COMPOSITION("name"),
	/**
	 *
	 */
	AGGREGATION("name", "type"),
	/**
	 *
	 */
	MODIFIER("type"),
	/**
	 *
	 */
	ASSIGN("name"),
	/**
	 *
	 */
	TYPED_CALL("name", "methods"),
	/**
	 *
	 */
	METHOD("class", "name"),
	/**
	 *
	 */
	METHOD_CALL("class", "name", "arg"),
	/**
	 *
	 */
	METHOD_RETURN("name"),
	/**
	 *
	 */
	METHOD_ARG("name"),
	/**
	 *
	 */
	METHOD_CALL_ARG("name");

	private List<String> requiredArgs;

	RuleDefinition(String... requiredArgs) {
		this.requiredArgs = Arrays.asList(requiredArgs);
	}

	/**
	 * @return the requiredArgs
	 */
	public List<String> getRequiredArgs() {
		return this.requiredArgs;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
