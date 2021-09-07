package parser.visitors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.printer.YamlPrinter;

import parser.Grouping;
import parser.enums.TargetTypes;
import parser.utils.AnnotationResolver;
import utils.PrimeChecker;

public class AnnotationGroupingVisitor extends GenericVisitorAdapter<Boolean, AtomicInteger> implements Grouping {

	private static int lastPrime = 1;
	/** searched annotation, fully qualified name */
	private String searchedAnnotation;
	/** target type of searched annotation */
	private TargetTypes type;
	/** groups */
	private Map<Integer, String> groups;
	/** reference value */
	private int lastCombinedPrime;

	public AnnotationGroupingVisitor(String searchedAnnotation, TargetTypes type) {
		this.searchedAnnotation = searchedAnnotation;
		this.type = type;
		this.groups = new HashMap<>();
	}

	@Override
	public Boolean visit(MethodDeclaration n, AtomicInteger container) {
		if (this.type.equals(TargetTypes.METHOD)) {
			if (check(n)) {
				container.set(this.lastCombinedPrime);
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ClassOrInterfaceDeclaration n, AtomicInteger container) {
		if (this.type.equals(TargetTypes.TYPE)) {
			if (check(n)) {
				container.set(this.lastCombinedPrime);
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(FieldDeclaration n, AtomicInteger container) {
		if (this.type.equals(TargetTypes.FIELD)) {
			if (check(n)) {
				container.set(this.lastCombinedPrime);
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	@Override
	public Boolean visit(ConstructorDeclaration n, AtomicInteger container) {
		if (this.type.equals(TargetTypes.CONSTRUCTOR)) {
			if (check(n)) {
				container.set(this.lastCombinedPrime);
				return Boolean.TRUE;
			}
		}
		return super.visit(n, container);
	}

	private <T extends NodeWithAnnotations<?>> boolean check(T obj) {
		this.lastCombinedPrime = 1;
		for (AnnotationExpr expr : obj.getAnnotations()) {
			String qualifiedName = expr.resolve().getQualifiedName();
			if (qualifiedName.equals(this.searchedAnnotation) && (expr instanceof SingleMemberAnnotationExpr)) {
				Expression exp = AnnotationResolver.getValueParameter(expr);
				if (exp.isArrayInitializerExpr()) {
					for (Expression temp : exp.asArrayInitializerExpr().getValues()) {
						if (!this.groups.containsValue(temp.asStringLiteralExpr().getValue())) {
							lastPrime = PrimeChecker.nextPrime(lastPrime);
							this.groups.put(lastPrime, temp.asStringLiteralExpr().getValue());
						}
						this.lastCombinedPrime *= lastPrime;
					}
				} else if (exp.isStringLiteralExpr()) {
					if (!this.groups.containsValue(exp.asStringLiteralExpr().getValue())) {
						lastPrime = PrimeChecker.nextPrime(lastPrime);
						this.groups.put(lastPrime, exp.asStringLiteralExpr().getValue());
					}
					this.lastCombinedPrime *= lastPrime;
				} else {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	@Override
	public Map<Integer, String> getGroups() {
		return this.groups;
	}

	@Override
	public void setGroups(Map<Integer, String> list) {
		this.groups.clear();
		this.groups.putAll(list);
	}
}
