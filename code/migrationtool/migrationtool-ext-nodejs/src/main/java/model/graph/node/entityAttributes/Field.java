package model.graph.node.entityAttributes;

import java.util.ArrayList;
import java.util.List;

public class Field {
	private List<String> names = new ArrayList<String>(); 
	private String type = ""; 
	private String initializer = ""; 
	private List<String> modifiers = new ArrayList<String>(); 
	private List<Annotation> annotations = new ArrayList<Annotation>(); 
	
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
	public List<Annotation> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}
	
}
