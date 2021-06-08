package model.graph.node;

import java.util.ArrayList;
import java.util.List;

import model.graph.types.NodeType;

public abstract class JavaImplementation {
	
	// id 
	protected String javaImplementationName = "";
	// Type 
	protected NodeType nodeType;
	// String attributes 
	protected String className = "";
	protected String path = "";
	protected String moduleDeclaration = "";
	protected String completeClassCode = "";
	// List<String> attributes 
	protected List<String> modules = new ArrayList<String>();
	private List<String> constructorsAsJsonObjectStrings = new ArrayList<String>();
	protected List<String> implementedInterfaces = new ArrayList<String>(); 
	protected List<String> extensions = new ArrayList<String>(); 
	protected List<String> imports = new ArrayList<String>();
	protected List<String> fieldsAsJsonObjectStrings = new ArrayList<String>();
	protected List<String> annotationsAsJsonObjectStrings = new ArrayList<String>(); 
	protected List<String> methodsAsJsonObjectStrings = new ArrayList<String>(); 
	
	public String toString(){
		return "javaImplementationName: " + javaImplementationName + " className: " + className + " path: " + path + " moduleDeclaration: " + moduleDeclaration
				+ "modules: " + modules.toString() + " constructors: " + constructorsAsJsonObjectStrings.toString() + " implemented Interfaces: " + implementedInterfaces.toString()
				+ " Extensions: " + extensions + " Imports:" + imports.toString() + " Fields:" + fieldsAsJsonObjectStrings.toString() + "\nbody: " + completeClassCode;
	}
	
	public String getClassName(){
		return className; 
	};
	public void setClassName(String className) {
		this.className = className;
	}
	public String getJavaClassName() {
		return javaImplementationName;
	}
	public void setJavaClassName(String javaClassName) {
		this.javaImplementationName = javaClassName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getModuleDeclaration() {
		return moduleDeclaration;
	}
	public void setModuleDeclaration(String moduleDeclaration) {
		this.moduleDeclaration = moduleDeclaration;
	}
	public String getCompleteClassCode() {
		return completeClassCode;
	}
	public void setCompleteClassCode(String completeClassCode) {
		this.completeClassCode = completeClassCode;
	}
	public List<String> getModules() {
		return modules;
	}
	public void setModules(List<String> modules) {
		this.modules = modules;
	}
	public List<String> getImplementedInterfaces() {
		return implementedInterfaces;
	}
	public void setImplementedInterfaces(List<String> implementedInterfaces) {
		this.implementedInterfaces = implementedInterfaces;
	}
	public List<String> getImports() {
		return imports;
	}
	public void setImports(List<String> imports) {
		this.imports = imports;
	}
	public List<String> getFieldsAsJsonObjectStrings() {
		return fieldsAsJsonObjectStrings;
	}
	public void setFieldsAsJsonObjectStrings(List<String> fieldsAsJsonObjectStrings) {
		this.fieldsAsJsonObjectStrings = fieldsAsJsonObjectStrings;
	}
	public List<String> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	public List<String> getConstructorsAsJsonObjectStrings() {
		return constructorsAsJsonObjectStrings;
	}
	public void setConstructorsAsJsonObjectStrings(List<String> constructorsAsJsonObjectStrings) {
		this.constructorsAsJsonObjectStrings = constructorsAsJsonObjectStrings;
	}

	public List<String> getAnnotationsAsJsonObjectStrings() {
		return annotationsAsJsonObjectStrings;
	}

	public void setAnnotationsAsJsonObjectStrings(List<String> annotations) {
		this.annotationsAsJsonObjectStrings = annotations;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public List<String> getMethodsAsJsonObjectStrings() {
		return methodsAsJsonObjectStrings;
	}

	public void setMethodsAsJsonObjectStrings(List<String> methodsAsJsonObjectStrings) {
		this.methodsAsJsonObjectStrings = methodsAsJsonObjectStrings;
	}

	// Die Methode ist nötig, da die neo4j-Datenbank keine Strings verarbeiten kann, in denen ' im Text vorkommt. Nur " ist erlaubt. 
	public void cleanBody(){
		this.completeClassCode = this.completeClassCode.replaceAll("'", "\"");
	}
	
	
	
	
	
}
