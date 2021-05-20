package rules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RuleDefinition {
	/**
	 *
	 */
	ANNOTATION("name", "type"),
	/**
	 *
	 */
	TYPEDEFINITION("type"),
	/**
	 *
	 */
	MODIFIER("type");

	private List<String> requiredArgs;

	private Map<String, String> args;

	private RuleDefinition(String... requiredArgs) {
		this.requiredArgs = Arrays.asList(requiredArgs);
		this.args = new HashMap<>();
	}

	/**
	 * @return the args
	 */
	public Map<String, String> getArgs() {
		return this.args;
	}

	/**
	 * @param args the args to set
	 */
	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	/**
	 * @return the requiredArgs
	 */
	public List<String> getRequiredArgs() {
		return this.requiredArgs;
	}

	@Override
	public String toString() {
		return super.toString().toLowerCase() + this.args.toString();
	}
}
