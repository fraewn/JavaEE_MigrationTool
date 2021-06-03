package model.graph.node.entityAttributes;

import java.util.List;

public class Annotation {
	private String name; 
	private List<AnnotationNameValuePair> parameters;
	
	public String getAnnotation() {
		return name;
	}
	public void setAnnotation(String annotation) {
		this.name = annotation;
	}
	public List<AnnotationNameValuePair> getParameters() {
		return parameters;
	}
	public void setParameters(List<AnnotationNameValuePair> parameters) {
		this.parameters = parameters;
	} 
	
	
}
