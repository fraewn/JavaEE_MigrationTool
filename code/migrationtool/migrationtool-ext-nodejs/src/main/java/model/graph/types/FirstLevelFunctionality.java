package model.graph.types;

public enum FirstLevelFunctionality {
	// https://docs.oracle.com/javaee/7/api/toc.htm
	annotation("javax.annotation"), batch("javax.batch"), crypto("javax.crypto"), decorator("javax.decorator"), ejb("javax.ejb"), el("javax.el"), enterprise("javax.enterprise"), faces("javax.faces"), 
	inject("javax.inject"), interceptor("javax.interceptor"), jms("javax.jms"), json("javax.json"), jws("jws"), mail("javax.mail"), management("javax.management"), persistence("javax.persistence"), resource("javax.resource"),
	security("javax.security"), servlet("javax.servlet"), transaction("javax.transaction"), validation("javax.validation"), websocket("javax.websocket"), ws("javax.ws"), xml("javax.xml"); 
	
	private final String module; 
	private final int level;
	
	private FirstLevelFunctionality(String module){
		this.module = module; 
		this.level = 1; 
	}
	
	public String toString(){
		return this.module;
	}
	public boolean equals(String other){
		return module.equals(other);
	}
	public int getLevel(){
		return this.level; 
	}
	

}
