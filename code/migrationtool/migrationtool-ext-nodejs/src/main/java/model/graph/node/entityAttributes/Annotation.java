package model.graph.node.entityAttributes;

import java.util.List;

public class Annotation {
	private String annotation; 
	private List<AnnotationNameValuePair> parameters;
	
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public List<AnnotationNameValuePair> getParameters() {
		return parameters;
	}
	public void setParameters(List<AnnotationNameValuePair> parameters) {
		this.parameters = parameters;
	} 
	
	
}
