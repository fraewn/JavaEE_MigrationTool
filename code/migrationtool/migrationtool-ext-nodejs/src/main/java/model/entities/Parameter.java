package model.entities;

public class Parameter {
	private String name; 
	private String type; 
	private String[] modifiers; 
	private String[] annotations; 
	
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
	public String[] getModifiers() {
		return modifiers;
	}
	public void setModifiers(String[] modifiers) {
		this.modifiers = modifiers;
	}
	public String[] getAnnotations() {
		return annotations;
	}
	public void setAnnotations(String[] annotations) {
		this.annotations = annotations;
	}
	

	
}
