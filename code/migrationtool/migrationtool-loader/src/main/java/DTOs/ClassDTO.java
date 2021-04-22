package DTOs;

import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.List;

public class ClassDTO {
	public ClassOrInterfaceDeclaration javaClass = null;
	public String fullName = null;
	public List<MethodDeclaration> methodCallExprList = null;
	public List<AnnotationDeclaration> annotationExprList = null;
	public List<ConstructorDeclaration> constructorDeclarationList = null;
	public List<ClassOrInterfaceType> implementsDependencyList = null;
	public List<ClassOrInterfaceType> extendsDependencyList = null;

	public ClassOrInterfaceDeclaration getJavaClass() {
		return javaClass;
	}

	public void setJavaClass(ClassOrInterfaceDeclaration javaClass) {
		this.javaClass = javaClass;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public List<MethodDeclaration> getMethodCallExprList() {
		return methodCallExprList;
	}

	public void setMethodCallExprList(List<MethodDeclaration> methodCallExprList) {
		this.methodCallExprList = methodCallExprList;
	}

	public List<AnnotationDeclaration> getAnnotationExprList() {
		return annotationExprList;
	}

	public void setAnnotationExprList(List<AnnotationDeclaration> annotationExprList) {
		this.annotationExprList = annotationExprList;
	}

	public List<ConstructorDeclaration> getConstructorDeclarationList() {
		return constructorDeclarationList;
	}

	public void setConstructorDeclarationList(List<ConstructorDeclaration> constructorDeclarationList) {
		this.constructorDeclarationList = constructorDeclarationList;
	}

	public List<ClassOrInterfaceType> getImplementsDependencyList() {
		return implementsDependencyList;
	}

	public void setImplementsDependencyList(List<ClassOrInterfaceType> implementsDependencyList) {
		this.implementsDependencyList = implementsDependencyList;
	}

	public List<ClassOrInterfaceType> getExtendsDependencyList() {
		return extendsDependencyList;
	}

	public void setExtendsDependencyList(List<ClassOrInterfaceType> extendsDependencyList) {
		this.extendsDependencyList = extendsDependencyList;
	}
}
