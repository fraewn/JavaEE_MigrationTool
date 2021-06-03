package model.graph.genericAttributes;

import java.util.List;

import model.graph.node.entityAttributes.Annotation;

public class PassedParameter {
	private String name; 
	private String type; 
	private List<String> modifiers; 
	private List<Annotation> annotations; 
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getModifiers() {
		return modifiers;
	}
	public void setModifiers(List<String> modifiers) {
		this.modifiers = modifiers;
	}
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}
	
	

	
}
