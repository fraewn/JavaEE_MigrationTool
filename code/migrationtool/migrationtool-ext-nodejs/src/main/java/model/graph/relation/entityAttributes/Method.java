package model.graph.relation.entityAttributes;

import java.util.List;

import model.graph.genericAttributes.PassedParameter;

public class Method {
	private String name; 
	private String type; 
	private String body; 
	private List<String> modifiers; 
	private List<String> annotations;
	private List<String> exceptions; 
	private List<PassedParameter> parameters; 
	
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
	public List<String> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}
	public List<PassedParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<PassedParameter> parameters) {
		this.parameters = parameters;
	}
	// Die Methode ist nötig, da die neo4j-Datenbank keine Strings verarbeiten kann, in denen ' im Text vorkommt. Nur " ist erlaubt. 
	public void cleanBody(){
		if(this.body != null){
		this.body = this.body.replaceAll("'", "\"");
		String newline = System.getProperty("line.separator");
		this.body = this.body.replace(newline, "\\n");
		}
	}
	
}
