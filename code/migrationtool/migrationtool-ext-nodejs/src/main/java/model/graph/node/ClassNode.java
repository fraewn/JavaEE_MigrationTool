package model.graph.node;

import java.util.ArrayList;
import java.util.List;

import model.graph.genericAttributes.NodeType;

public class ClassNode extends JavaImplementation {
	protected final NodeType nodeType = NodeType.Class; 
	
	// variables only necessary in java classes 
	private List<String> constructorsAsJsonObjectStrings = new ArrayList();
	private List<String> extensions;
	
	public List<String> getConstructorsAsJsonObjectStrings() {
		return constructorsAsJsonObjectStrings;
	}
	public void setConstructorsAsJsonObjectStrings(List<String> constructorsAsJsonObjectStrings) {
		this.constructorsAsJsonObjectStrings = constructorsAsJsonObjectStrings;
	}
	public List<String> getExtensions() {
		return extensions;
	}
	public void setExtensions(List<String> extensions) {
		this.extensions = extensions;
	}
	
	// variables used in classes, abstract classes and interfaces
	@Override
	public String getClassName() {
		return null;
	} 
	public void setClassName(String className) {
		this.className = className;
	}
	public NodeType getNodeType() {
		return nodeType;
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
}
