package model.graph.node;

import java.util.ArrayList;
import java.util.List;

public abstract class JavaImplementation {
	protected String className = "";
	protected String javaClassName = "";
	protected String path = "";
	protected String moduleDeclaration;
	protected String completeClassCode = "";

	protected List<String> modules;
	private List<String> constructorsAsJsonObjectStrings;
	protected List<String> implementedInterfaces; 
	protected List<String> extensions; 
	protected List<String> imports;
	protected List<String> fieldsAsJsonObjectStrings;
	
	public String getClassName(){
		return className; 
	};
	public void setClassName(String className) {
		this.className = className;
	}
	public String getJavaClassName() {
		return javaClassName;
	}
	public void setJavaClassName(String javaClassName) {
		this.javaClassName = javaClassName;
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
}
