package model.graph.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Layer {
	persistence_layer("Persistence Layer", Collections.unmodifiableList(
		    new ArrayList<FirstLevelFunctionality>() {{
		        add(FirstLevelFunctionality.persistence);
		        add(FirstLevelFunctionality.transaction);
		        add(FirstLevelFunctionality.batch);
		        }}
		    )), 
	business_layer("Business Logic Layer", Collections.unmodifiableList(
		    new ArrayList<FirstLevelFunctionality>() {{
		        add(FirstLevelFunctionality.ejb);
		        add(FirstLevelFunctionality.enterprise);
		        add(FirstLevelFunctionality.mail);
		        add(FirstLevelFunctionality.decorator);
		        }}
		    )), 
	web_Layer("Web Layer", Collections.unmodifiableList(
		    new ArrayList<FirstLevelFunctionality>() {{
		        add(FirstLevelFunctionality.json);
		        add(FirstLevelFunctionality.jms);
		        add(FirstLevelFunctionality.jws);
		        add(FirstLevelFunctionality.xml);
		        add(FirstLevelFunctionality.ws);
		        add(FirstLevelFunctionality.websocket);
		        add(FirstLevelFunctionality.servlet);
		    	}}
		    )), 
	presentation_Layer("Presentation Layer", Collections.unmodifiableList(
						    new ArrayList<FirstLevelFunctionality>() {{
						        add(FirstLevelFunctionality.faces);
						    }}
						 )),
	service_Layer("Service Layer", Collections.unmodifiableList(
		    new ArrayList<FirstLevelFunctionality>() {{
		        add(FirstLevelFunctionality.interceptor); 
		        add(FirstLevelFunctionality.security);
		        add(FirstLevelFunctionality.crypto);
		        add(FirstLevelFunctionality.management);
		    }}));
		        
	/*annotation("javax.annotation"), batch("javax.batch"), crypto("javax.crypto"), decorator("javax.decorator"), ejb("javax.ejb"), el("javax.el"), enterprise("javax.enterprise"), faces("javax.faces"), 
	inject("javax.inject"), interceptor("javax.interceptor"), jms("javax.jms"), json("javax.json"), jws("jws"), mail("javax.mail"), management("javax.management"), persistence("javax.persistence"), resource("javax.resource"),
	security("javax.security"), servlet("javax.servlet"), transaction("javax.transaction"), validation("javax.validation"), websocket("javax.websocket"), ws("javax.ws"), xml("javax.xml"); */
	
	private final String name; 
	private final List<FirstLevelFunctionality> funcList; 
	
	private Layer(String name, List<FirstLevelFunctionality> funcList){
		this.name = name; 
		this.funcList = funcList; 

	}
	public String toString(){
		return this.name;
	}
	public boolean equals(String other){
		return name.equals(other);
	}
	public String getName(){
		return this.name;
	}
	public List<FirstLevelFunctionality> getFuncList(){
		return this.funcList; 
	}

}
