package dto;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.google.common.collect.HashBasedTable;

public class MethodDependencyDTO {
	protected ClassOrInterfaceDeclaration methodCallingClass = null;
	protected ClassOrInterfaceDeclaration classWithMethod = null;
	protected MethodCallExpr mce = null;

	protected HashBasedTable<ClassOrInterfaceDeclaration, MethodCallExpr, ClassOrInterfaceDeclaration> methodCall = null;

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

	public MethodCallExpr getMce() {
		return mce;
	}

	public void setMce(MethodCallExpr mce) {
		this.mce = mce;
	}

	public HashBasedTable<ClassOrInterfaceDeclaration, MethodCallExpr, ClassOrInterfaceDeclaration> getMethodCall() {
		return methodCall;
	}

	public void addMethodCall(ClassOrInterfaceDeclaration methodCallingClass , MethodCallExpr mce,
							ClassOrInterfaceDeclaration classWithMethod) {
		this.methodCall.put(methodCallingClass, mce, classWithMethod);
	}

	public void setMethodCall(HashBasedTable<ClassOrInterfaceDeclaration, MethodCallExpr, ClassOrInterfaceDeclaration> methodCall){
		this.methodCall = methodCall;
	}


}
