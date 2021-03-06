package model.graph.relation;

import model.graph.types.NodeType;

public class MethodCallRelation {
	// the method entity contains all attributes (annotations, parameters...) 
	private String methodName; 
	// Methods can be provided and invoked by classes, interfaces or abstract classes
	// die hab ich aber dann noch nicht deshalb String
	// außerdem muss ich mir hier noch die Typen merken (Klasse, Interface, Abstract class)
	private String callingJavaImplementationName;
	private String providingJavaImplementationName; 
	
	private NodeType callingJavaImplementationType;
	private NodeType providingJavaImplementationType; 
	
	public String getMethod() {
		return methodName;
	}
	public void setMethod(String methodName) {
		this.methodName = methodName;
	}
	public String getCallingJavaImplementation() {
		return callingJavaImplementationName;
	}
	public void setCallingJavaImplementation(String callingJavaImplementation) {
		this.callingJavaImplementationName = callingJavaImplementation;
	}
	public String getProvidingJavaImplementation() {
		return providingJavaImplementationName;
	}
	public void setProvidingJavaImplementation(String providingJavaImplementation) {
		this.providingJavaImplementationName = providingJavaImplementation;
	}
	public NodeType getCallingJavaImplementationType() {
		return callingJavaImplementationType;
	}
	public void setCallingJavaImplementationType(NodeType callingJavaImplementationType) {
		this.callingJavaImplementationType = callingJavaImplementationType;
	}
	public NodeType getProvidingJavaImplementationType() {
		return providingJavaImplementationType;
	}
	public void setProvidingJavaImplementationType(NodeType providingJavaImplementationType) {
		this.providingJavaImplementationType = providingJavaImplementationType;
	}
}
