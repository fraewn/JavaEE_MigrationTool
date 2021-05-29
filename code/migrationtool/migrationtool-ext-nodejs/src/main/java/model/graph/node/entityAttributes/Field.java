package model.graph.node.entityAttributes;

import java.util.ArrayList;
import java.util.List;

import model.graph.genericAttributes.PassedParameter;

public class Field {
	private List<String> names = new ArrayList(); 
	private String type = ""; 
	private String initializer = ""; 
	private List<String> modifiers = new ArrayList(); 
	private List<String> annotations = new ArrayList(); 
	private List<PassedParameter> parameters = new ArrayList(); 
	
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getInitializer() {
		return initializer;
	}
	public void setInitializer(String initializer) {
		this.type = initializer;
	}
	public List<String> getModifiers() {
		return modifiers;
	}
	public void setModifiers(List<String> modifiers) {
		this.modifiers = modifiers;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	public List<PassedParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<PassedParameter> parameters) {
		this.parameters = parameters;
	}
	
}
