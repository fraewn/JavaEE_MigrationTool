package parser.visitors;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class ComplexityVisitor extends GenericVisitorAdapter<Boolean, AtomicInteger> {

	private static final List<Operator> VALID = List.of(
			Operator.AND,
			Operator.OR,
			Operator.OR,
			Operator.AND,
			Operator.BINARY_OR,
			Operator.BINARY_AND,
			Operator.XOR,
			Operator.EQUALS,
			Operator.NOT_EQUALS,
			Operator.LESS,
			Operator.GREATER,
			Operator.LESS_EQUALS,
			Operator.GREATER_EQUALS,
			Operator.LEFT_SHIFT,
			Operator.SIGNED_RIGHT_SHIFT,
			Operator.UNSIGNED_RIGHT_SHIFT);

	@Override
	public Boolean visit(MethodDeclaration n, AtomicInteger arg) {
		int complexity = 1;
		List<IfStmt> ifs = n.findAll(IfStmt.class);
		complexity += ifs.size();
		for (IfStmt ifStmt : ifs) {
			complexity += ifStmt.getCondition().findAll(BinaryExpr.class).stream()
					.filter(x -> VALID.contains(x.getOperator())).count();
		}
		List<SwitchStmt> switchs = n.findAll(SwitchStmt.class);
		complexity += switchs.size();
		for (SwitchStmt switchStmt : switchs) {
			complexity += switchStmt.getEntries().size();
		}
		complexity += n.findAll(ForStmt.class).size();
		complexity += n.findAll(ForEachStmt.class).size();
		List<WhileStmt> whiles = n.findAll(WhileStmt.class);
		complexity += whiles.size();
		for (WhileStmt whileStmt : whiles) {
			complexity += whileStmt.getCondition().findAll(BinaryExpr.class).stream()
					.filter(x -> VALID.contains(x.getOperator())).count();
		}
		List<DoStmt> dos = n.findAll(DoStmt.class);
		complexity += dos.size();
		for (DoStmt doStmt : dos) {
			complexity += doStmt.getCondition().findAll(BinaryExpr.class).stream()
					.filter(x -> VALID.contains(x.getOperator())).count();
		}
		complexity += n.findAll(ThrowStmt.class).size();
		complexity += n.findAll(CatchClause.class).size();
		complexity += n.findAll(LambdaExpr.class).size();
		complexity += n.findAll(ConditionalExpr.class).size();
		arg.set(complexity);
		return Boolean.TRUE;
	}
}
