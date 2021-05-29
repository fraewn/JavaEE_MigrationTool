package model.graph.node.entityAttributes;

import java.util.ArrayList;
import java.util.List;

import model.graph.genericAttributes.PassedParameter;

public class Constructor {
	private String name; 
	private String body; 
	private List<String> modifiers = new ArrayList(); 
	private List<String> annotations = new ArrayList(); 
	private List<PassedParameter> parameters = new ArrayList();
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
