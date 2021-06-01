package model.graph.genericAttributes;

public enum NodeType {
	Class("Class"), Interface("Interface"), AbstractClass("AbstractClass"), Entity("Entity"), Functionality("Functionality"), Resource("Resource"), 
	Layer("Layer"), SpecificLayer("SpecificLayer"), Library("Library");
	
	private final String name; 
	
	private NodeType(String name){
		this.name = name; 
	}
	
	public String toString(){
		return this.name;
	}
	public boolean equals(String other){
		return name.equals(other);
	}
}
