package rules;

import static rules.WildCards.GET_METHOD;
import static rules.WildCards.SET_METHOD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.erm.Entity;
import rules.engine.Case;
import rules.engine.RuleEvaluator;

public enum EngineOperations {

	/**
	 *
	 */
	LINEAR {
		@Override
		public void successCondition(Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e,
				RuleEvaluator ruleEngineValue, MethodDeclaration originMethod, Entity analyzedClass,
				List<WildCards> wildCards, BiConsumer<Case, String> function) {
			for (String attr : analyzedClass.getAttributes()) {
				List<WildCards> cardsCopy = new ArrayList<>(wildCards);
				String get = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
				String set = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
				cardsCopy.add(GET_METHOD.setValue(get));
				cardsCopy.add(SET_METHOD.setValue(set));
				ruleEngineValue.evaluate(cardsCopy, originMethod, y -> {
					function.accept(null, analyzedClass.getName() + "." + attr);
				});
			}
		}
	},
	/**
	 *
	 */
	CROSS_PRODUCT {
		@Override
		public void successCondition(Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e,
				RuleEvaluator ruleEngineValue, MethodDeclaration originMethod, Entity analyzedClass,
				List<WildCards> wildCards, BiConsumer<Case, String> function) {
			for (Entry<ClassOrInterfaceDeclaration, List<MethodDeclaration>> entry : e.entrySet()) {
				for (MethodDeclaration m : entry.getValue()) {
					for (String attr : analyzedClass.getAttributes()) {
						List<WildCards> cardsCopy = new ArrayList<>(wildCards);
						String get = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
						String set = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
						cardsCopy.add(GET_METHOD.setValue(get));
						cardsCopy.add(SET_METHOD.setValue(set));
						ruleEngineValue.evaluate(cardsCopy, m, y -> {
							function.accept(new Case(entry.getKey(), m.getNameAsString()),
									analyzedClass.getName() + "." + attr);
						});
					}
				}
			}
		}
	};

	public abstract void successCondition(Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e,
			RuleEvaluator ruleEngineValue, MethodDeclaration originMethod, Entity analyzedClass,
			List<WildCards> wildCards, BiConsumer<Case, String> function);
}
