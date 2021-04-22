package operations.dto;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class ClassDTO {

	private String fullName;

	private ClassOrInterfaceDeclaration javaClass;

	private List<TypeParameter> typeParameters;

	private List<FieldDeclaration> fields;

	private List<MethodDeclaration> methods;

	private List<ConstructorDeclaration> constructors;

	private List<ClassOrInterfaceType> implementations;

	private List<ClassOrInterfaceType> extensions;

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the javaClass
	 */
	public ClassOrInterfaceDeclaration getJavaClass() {
		return this.javaClass;
	}

	/**
	 * @param javaClass the javaClass to set
	 */
	public void setJavaClass(ClassOrInterfaceDeclaration javaClass) {
		this.javaClass = javaClass;
	}

	/**
	 * @return the typeParameters
	 */
	public List<TypeParameter> getTypeParameters() {
		return this.typeParameters;
	}

	/**
	 * @param typeParameters the typeParameters to set
	 */
	public void setTypeParameters(List<TypeParameter> typeParameters) {
		this.typeParameters = typeParameters;
	}

	/**
	 * @return the fields
	 */
	public List<FieldDeclaration> getFields() {
		return this.fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<FieldDeclaration> fields) {
		this.fields = fields;
	}

	/**
	 * @return the methods
	 */
	public List<MethodDeclaration> getMethods() {
		return this.methods;
	}

	/**
	 * @param methods the methods to set
	 */
	public void setMethods(List<MethodDeclaration> methods) {
		this.methods = methods;
	}

	/**
	 * @return the constructors
	 */
	public List<ConstructorDeclaration> getConstructors() {
		return this.constructors;
	}

	/**
	 * @param constructors the constructors to set
	 */
	public void setConstructors(List<ConstructorDeclaration> constructors) {
		this.constructors = constructors;
	}

	/**
	 * @return the implementations
	 */
	public List<ClassOrInterfaceType> getImplementations() {
		return this.implementations;
	}

	/**
	 * @param implementations the implementations to set
	 */
	public void setImplementations(List<ClassOrInterfaceType> implementations) {
		this.implementations = implementations;
	}

	/**
	 * @return the extensions
	 */
	public List<ClassOrInterfaceType> getExtensions() {
		return this.extensions;
	}

	/**
	 * @param extensions the extensions to set
	 */
	public void setExtensions(List<ClassOrInterfaceType> extensions) {
		this.extensions = extensions;
	}
}
