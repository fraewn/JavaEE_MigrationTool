package model.graph.node;

import java.util.List;

import model.graph.types.NodeType;

public class InterfaceNode extends JavaImplementation {
	protected final NodeType nodeType = NodeType.Interface; 
	
	public String toString(){
		return "javaImplementationName: " + javaImplementationName + " className: " + className + " Type: " + nodeType.toString() + " Path: " + path + " moduleDeclaration: " + moduleDeclaration
				+ "modules: " + modules.toString() + " Annotations: " + annotationsAsJsonObjectStrings + " implemented Interfaces: " + implementedInterfaces.toString()
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
	public NodeType getNodeType() {
		return nodeType;
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
	public List<String> getAnnotationsAsJsonObjectStrings() {
		return annotationsAsJsonObjectStrings;
	}
	public void setAnnotationsAsJsonObjectStrings(List<String> annotations) {
		this.annotationsAsJsonObjectStrings = annotations;
	}
}
