package model.graph.types;

public enum SecondLevelFunctionality {
		// https://docs.oracle.com/javaee/7/api/toc.htm
		annotation_security("javax.annotation.security"), annotation_sql("javax.annotation.sql"), 
		batch_api("javax.batch.api"), batch_operations("javax.batch.operations"),batch_runtime("javax.batch.runtime"), 
		ejb_embeddable("javax.ejb.embeddable"), ejb_spi("javax.ejb.spi"), 
		enterprise_concurrent("javax.enterprise.concurrent"), enterprise_context("javax.enterprise.context"), enterprise_deploy("javax.enterprise.deploy"), enterprise_event("javax.enterprise.event"), enterprise_inject("javax.enterprise.inject"), enterprise_util("javax.enterprise.util"), 
		faces_application("javax.faces.application"), faces_bean("javax.faces.bean"), faces_component("javax.faces.component"), faces_context("javax.faces.context"), faces_convert("javax.faces.convert"), faces_el("javax.faces.el"), faces_event("javax.faces.event"), faces_flow("javax.faces.flow"), faces_lifecycle("javax.faces.lifecycle"), faces_model("javax.faces.model"), faces_render("javax.faces.render"), faces_validator("javax.faces.validator"), faces_view("javax.faces.view"), faces_webapp("javax.faces.webapp"),  
		json_spi("javax.json.spi"), json_stream("javax.json.stream"), 
		jws_soap("jws.soap"),
		mail_event("javax.mail.event"), mail_internet("javax.mail.internet"), mail_search("javax.mail.search"), mail_util("javax.mail.util"), 
		management_j2ee("javax.management.j2ee"), 
		persistence_criteria("javax.persistence.criteria"), persistence_metamodel("javax.persistence.metamodel"), persistence_spi("javax.persistence.spi"), 
		resource_cci("javax.resource.cci"), resource_spi("javax.resource.spi"), resource_auth("javax.resource.auth"),
		security_jacc("javax.security.jacc"), 
		servlet_annotation("javax.servlet.annotation"), servlet_descriptor("javax.servlet.descriptor"), servlet_http("javax.servlet.http"), servlet("javax.servlet.jsp"), 
		transaction_xa("javax.transaction.xa"), 
		validation_bootstrap("javax.validation.bootstrap"), validation_constraints("javax.validation.constraints"), validation_constraintvalidation("javax.validation.constraintvalidation"), validation_executable("javax.validation.executable"), validation_groups("javax.validation.groups"), validation_metadata("javax.validation.metadata"), validation_spi("javax.validation.spi"),
		websocket_server("javax.websocket.server"), 
		ws_rs("javax.ws.rs"),
		xml_bind("javax.xml.bind"), xml_registry("javax.xml.registry"), xml_rpc("javax.xml.rpc"), xml_soap("javax.xml.soap"), xml_ws("javax.xml.ws");
		
		private final String module; 
		private final int level;
		
		private SecondLevelFunctionality(String module){
			this.module = module; 
			this.level = 2; 
		}
		
		public String toString(){
			return this.module;
		}
		public boolean equals(String other){
			return module.equals(other);
		}
		
		public boolean contains(String other){
			return module.contains(other);
		}
		public int getLevel(){
			return this.level; 
		}
}
