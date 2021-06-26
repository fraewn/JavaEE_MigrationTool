package model.graph.types;

public enum NodeType {
	JavaImplementation("JavaImplementation"), Class("Class"), Interface("Interface"), AbstractClass("AbstractClass"), Enum("Enum"), Entity("Entity"), Functionality("Functionality"), Resource("Resource"), 
	Layer("Layer"), SpecificLayer("SpecificLayer"), Library("Library"), InjectedExternal("InjectedExternal");
	
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
