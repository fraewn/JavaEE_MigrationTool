package DTOs;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodDependencyDTO {
	protected ClassOrInterfaceDeclaration methodCallingClass = null;
	protected ClassOrInterfaceDeclaration classWithMethod = null;
	protected MethodDeclaration mce = null;

	public ClassOrInterfaceDeclaration getMethodCallingClass() {
		return methodCallingClass;
	}

	public void setMethodCallingClass(ClassOrInterfaceDeclaration methodCallingClass) {
		this.methodCallingClass = methodCallingClass;
	}

	public ClassOrInterfaceDeclaration getClassWithMethod() {
		return classWithMethod;
	}

	public void setClassWithMethod(ClassOrInterfaceDeclaration classWithMethod) {
		this.classWithMethod = classWithMethod;
	}

	public MethodDeclaration getMce() {
		return mce;
	}

	public void setMethodDeclaration(MethodDeclaration mce) {
		this.mce = mce;
	}



}
