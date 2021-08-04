package analyzer;

import static rules.engine.WildCards.GET_METHOD;
import static rules.engine.WildCards.SET_METHOD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.erm.Entity;
import rules.engine.RuleEvaluator;
import rules.engine.WildCards;

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
				cardsCopy.addAll(buildWildCards(attr));
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
						cardsCopy.addAll(buildWildCards(attr));
						ruleEngineValue.evaluate(cardsCopy, m, y -> {
							function.accept(new Case(entry.getKey(), m.getNameAsString()),
									analyzedClass.getName() + "." + attr);
						});
					}
				}
			}
		}
	};

	private static List<WildCards> buildWildCards(String attribute) {
		List<WildCards> res = new ArrayList<>();
		String get = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
		String set = "set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
		res.add(GET_METHOD.setValue(get));
		res.add(SET_METHOD.setValue(set));
		return res;
	}

	public abstract void successCondition(Map<ClassOrInterfaceDeclaration, List<MethodDeclaration>> e,
			RuleEvaluator ruleEngineValue, MethodDeclaration originMethod, Entity analyzedClass,
			List<WildCards> wildCards, BiConsumer<Case, String> function);
}
