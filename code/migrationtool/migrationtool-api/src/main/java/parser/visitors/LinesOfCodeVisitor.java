package parser.visitors;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class LinesOfCodeVisitor extends GenericVisitorAdapter<Boolean, AtomicInteger> {

	@Override
	public Boolean visit(MethodDeclaration n, AtomicInteger arg) {
		if (n.getBody().get() == null) {
			return super.visit(n, arg);
		}
		int totalLinesOfBody = (n.getBody().get().getEnd().get().line - n.getBody().get().getBegin().get().line) + 1;
		// Remove comments
		int linesOfComments = 0;
		for (Comment comment : n.getAllContainedComments()) {
			int diff = comment.getEnd().get().line - comment.getBegin().get().line;
			linesOfComments += diff == 0 ? 1 : diff;
		}
		// Remove blank lines
		String[] tmp = n.getBody().get().toString().split(System.lineSeparator());
		int blankLines = totalLinesOfBody - tmp.length;
		arg.set(totalLinesOfBody - linesOfComments - blankLines - 2); // Fix method brake
		return Boolean.TRUE;
	}
}
