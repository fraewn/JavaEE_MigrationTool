package model.graph.node.entityAttributes;

public class AnnotationNameValuePair {
	private String name; 
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	} 
	public void clear(){
		this.value = this.value.replaceAll("\"", "'");
	}
}
