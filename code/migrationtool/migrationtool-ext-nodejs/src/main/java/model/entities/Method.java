package model.entities;

public class Method {
	private String name; 
	private String type; 
	private String body; 
	private String[] modifiers; 
	private String[] annotations;
	private String[] exceptions; 
	private PassedParameter[] parameters; 
	
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
	public String[] getExceptions() {
		return exceptions;
	}
	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
	}
	public PassedParameter[] getParameters() {
		return parameters;
	}
	public void setParameters(PassedParameter[] parameters) {
		this.parameters = parameters;
	}
}
