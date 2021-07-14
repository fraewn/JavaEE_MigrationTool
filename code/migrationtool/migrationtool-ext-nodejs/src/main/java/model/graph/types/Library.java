package model.graph.types;

public enum Library {
		swagger("io.swagger", "Documentation for REST APIs"), joda("org.joda.time.LocalDate", ""), log4j("org.apache.log4j","Logging Library for java by Apache"), java_uuid("java.util.UUID", ""), java_date("java.util.Date", ""),
		java_ioexcep("java.io.IOException", ""), java_serializable("java.io.Serializable", ""), java_list("java.util.List", ""), java_locale("java.util.Locale", ""), java_collection("java.util.Collections", ""),
		java_set("java.util.Set", ""), java_hashmap("java.util.HashMap", ""), java_map("java.util.Map", ""), java_calender("java.util.Calendar", ""), java_reflection("java.lang.reflect", ""), java_regex("java.util.regex", ""),
		java_hashset("java.util.Hashset", ""), primefaces("org.primefaces.PrimeFaces", ""), java_arraylist("java.util.ArrayList", ""), java_security("java.security", "");
		
		private final String module; 
		private final String description; 
		
		private Library(String module, String description){
			this.module = module; 
			this.description = description; 
		}
		public String toString(){
			return this.module;
		}
		public boolean equals(String other){
			return module.equals(other);
		}
		public String getName(){
			return this.module; 
		}
		public String getDescription(){
			return description; 
		}
}
