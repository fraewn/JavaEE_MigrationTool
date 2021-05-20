package operations.dto;

import java.util.List;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

/**
 * General DTO object, which flattens the structure of a analyzed java object by
 * javaparser
 */
public class ClassDTO {

	/** Full Quailifed class name */
	private String fullName;
	/** Module declaration used in Java 9+ */
	private String moduleDeclaration;
	/** Package name of a class */
	private String packageDeclaration;
	/** imports of the corresponding class */
	private List<ImportDeclaration> imports;
	/** java class */
	private ClassOrInterfaceDeclaration javaClass;
	/** type parameters of the class */
	private List<TypeParameter> typeParameters;
	/** all attributes of the class */
	private List<FieldDeclaration> fields;
	/** all methods of the class */
	private List<MethodDeclaration> methods;
	/** all constructors of the class */
	private List<ConstructorDeclaration> constructors;
	/** all interfaces of the class */
	private List<ClassOrInterfaceType> implementations;
	/** all mother classes of the class */
	private List<ClassOrInterfaceType> extensions;
	/** all used enums of the class */
	private List<EnumDeclaration> enums;
	/** all declarated annotations of the class */
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

	/**
	 * @return the moduleDeclaration
	 */
	public String getModuleDeclaration() {
		return this.moduleDeclaration;
	}

	/**
	 * @param moduleDeclaration the moduleDeclaration to set
	 */
	public void setModuleDeclaration(String moduleDeclaration) {
		this.moduleDeclaration = moduleDeclaration;
	}

	/**
	 * @return the packageDeclaration
	 */
	public String getPackageDeclaration() {
		return this.packageDeclaration;
	}

	/**
	 * @param packageDeclaration the packageDeclaration to set
	 */
	public void setPackageDeclaration(String packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	/**
	 * @return the imports
	 */
	public List<ImportDeclaration> getImports() {
		return this.imports;
	}

	/**
	 * @param imports the imports to set
	 */
	public void setImports(List<ImportDeclaration> imports) {
		this.imports = imports;
	}

	/**
	 * @return the enums
	 */
	public List<EnumDeclaration> getEnums() {
		return this.enums;
	}

	/**
	 * @param enums the enums to set
	 */
	public void setEnums(List<EnumDeclaration> enums) {
		this.enums = enums;
	}

	/**
	 * @return the annotationDeclarationList
	 */
	public List<AnnotationExpr> getAnnotationDeclarationList() {
		return this.annotationDeclarationList;
	}

	/**
	 * @param annotationDeclarationList the annotationDeclarationList to set
	 */
	public void setAnnotationDeclarationList(List<AnnotationExpr> annotationDeclarationList) {
		this.annotationDeclarationList = annotationDeclarationList;
	}
}
