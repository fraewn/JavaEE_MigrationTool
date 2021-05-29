package model.graph;

import model.graph.node.ClassNode;
import model.graph.relation.Method;

public class MethodCallRelation {
	private Method method; 
	private ClassNode callingClass;
	private ClassNode providingClass; 
	
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public ClassNode getCallingClass() {
		return callingClass;
	}
	public void setCallingClass(ClassNode callingClass) {
		this.callingClass = callingClass;
	}
	public ClassNode getProvidingClass() {
		return providingClass;
	}
	public void setProvidingClass(ClassNode providingClass) {
		this.providingClass = providingClass;
	}
	
}
