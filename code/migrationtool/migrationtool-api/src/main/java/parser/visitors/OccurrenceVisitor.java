package parser.visitors;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class OccurrenceVisitor extends GenericVisitorAdapter<Boolean, AtomicInteger> {

	@Override
	public Boolean visit(MethodDeclaration n, AtomicInteger arg) {
		arg.set(1);
		return Boolean.TRUE;
	}
}
