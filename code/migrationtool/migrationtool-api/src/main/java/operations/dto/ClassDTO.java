package operations.dto;

import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class ClassDTO {

	// meta data
	private String fullName;
	private String moduleDeclaration;
	private String packageDeclaration;

	// imports (to the corresponding file)
	private List<ImportDeclaration> imports;

	// class
	private ClassOrInterfaceDeclaration javaClass;

	// all declared parameters in class
	private List<TypeParameter> typeParameters;

	// all attributes in a class
	private List<FieldDeclaration> fields;

	// all methods in a class
	private List<MethodDeclaration> methods;

	// all constructors of a class
	private List<ConstructorDeclaration> constructors;

	// all interfaces the class implements
	private List<ClassOrInterfaceType> implementations;

	// all other classes the class extends from
	private List<ClassOrInterfaceType> extensions;

	// all enums used in the class
	private List<EnumDeclaration> enums;

	// all annotations in a class
	private List<AnnotationExpr> annotationDeclarationList;


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

	public List<EnumDeclaration> getEnums() {
		return enums;
	}

	public void setEnums(List<EnumDeclaration> enums) {
		this.enums = enums;
	}

	public List<ImportDeclaration> getImports() {
		return imports;
	}

	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}

	public String getModuleDeclaration() {
		return moduleDeclaration;
	}

	public void setModuleDeclaration(String moduleDeclaration) {
		this.moduleDeclaration = moduleDeclaration;
	}

	public String getPackageDeclaration() {
		return packageDeclaration;
	}

	public void setPackageDeclaration(String packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	public List<AnnotationExpr> getAnnotationDeclarationList() {
		return annotationDeclarationList;
	}

	public void setAnnotationDeclarationList(List<AnnotationExpr> annotationDeclarationList) {
		this.annotationDeclarationList = annotationDeclarationList;
	}
}
