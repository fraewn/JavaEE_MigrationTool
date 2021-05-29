package model.graph.relation;

import model.graph.node.ClassNode;
import model.graph.node.JavaImplementation;
import model.graph.relation.entityAttributes.Method;

public class MethodCallRelation {
	// the method entity contains all attributes (annotations, parameters...) 
	private Method method; 
	// Methods can be provided and invoked by classes, interfaces or abstract classes
	private JavaImplementation callingClass;
	private JavaImplementation providingClass; 
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public JavaImplementation getCallingClass() {
		return callingClass;
	}
	public void setCallingClass(JavaImplementation callingClass) {
		this.callingClass = callingClass;
	}
	public JavaImplementation getProvidingClass() {
		return providingClass;
	}
	public void setProvidingClass(JavaImplementation providingClass) {
		this.providingClass = providingClass;
	}
	
}
