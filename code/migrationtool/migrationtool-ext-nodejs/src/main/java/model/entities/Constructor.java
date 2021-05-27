package model.entities;

public class Constructor {
	private String name; 
	private String body; 
	private String[] modifiers; 
	private String[] annotations; 
	private Parameter[] parameters; 
	
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
	public Parameter[] getParameters() {
		return parameters;
	}
	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
}
