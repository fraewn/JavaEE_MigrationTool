package parser.visitors;

import java.util.List;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import dto.MethodDependencyDTO;
import exceptions.MigrationToolInitException;

public class MethodCallResolver extends VoidVisitorAdapter<List<MethodDependencyDTO>> {
	public void visit(MethodDeclaration methodDeclaration, List<MethodDependencyDTO> methodDependencyDTOList)
			throws MigrationToolInitException {

		super.visit(methodDeclaration, methodDependencyDTOList);

		MethodDependencyDTO methodDependencyDTO = new MethodDependencyDTO();
		String method = methodDeclaration.getDeclarationAsString();

		/*
		 * methodDependencyDTO.setMethodDeclaration(methodDeclaration.
		 * asMethodDeclaration());
		 * methodDependencyDTO.setClassWithMethod(methodDeclaration.resolve().
		 * getQualifiedSignature());
		 * methodDependencyDTO.setMethodCallingClass(methodDeclaration.
		 * asClassOrInterfaceDeclaration());
		 */
		// if gucken wo method call expression hin geht
		// schauen ob das iwie im nativen projekt ist oder ob das ne lib ist
		// entweder mit blacklist
		// alles was nicht mit .java anfängt exclude
		// methodDependencyDTOList.add(methodDependencyDTO);
	}

	@Override
	public void visit(MethodCallExpr n, List<MethodDependencyDTO> methodDependencyDTOList) {
		super.visit(n, methodDependencyDTOList);

		String method = n.getNameAsString();

		try {
			// calling Class vorher abfragen wo cu benutzt wird und zu dem zeitpunkt in dto
			// schreiben
			String callingClass = "";
			// die anderen beiden hier:
			String methodClass = n.resolve().getQualifiedSignature();
			System.out.println(callingClass + " uses " + method + " from " + methodClass);
		} catch (Exception e) {
			System.out.println("+ EXCEPTION for method: " + method);
		}
	}
}
