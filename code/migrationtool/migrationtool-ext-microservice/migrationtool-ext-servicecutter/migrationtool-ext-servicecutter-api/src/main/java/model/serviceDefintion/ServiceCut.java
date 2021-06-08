package model.serviceDefintion;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import model.data.UseCase;

public class ServiceCut {

	private List<Service> services;

	private List<ServiceRelation> relations;

	private SortedMap<Service, List<UseCase>> relatedUseCases;

	public ServiceCut() {
		this.services = new ArrayList<>();
		this.relations = new ArrayList<>();
		this.relatedUseCases = new TreeMap<>();
	}

	/**
	 * @return the services
	 */
	public List<Service> getServices() {
		return this.services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(List<Service> services) {
		this.services = services;
	}

	/**
	 * @return the relations
	 */
	public List<ServiceRelation> getRelations() {
		return this.relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(List<ServiceRelation> relations) {
		this.relations = relations;
	}

	/**
	 * @return the relatedUseCases
	 */
	public SortedMap<Service, List<UseCase>> getRelatedUseCases() {
		return this.relatedUseCases;
	}

	/**
	 * @param relatedUseCases the relatedUseCases to set
	 */
	public void setRelatedUseCases(SortedMap<Service, List<UseCase>> relatedUseCases) {
		this.relatedUseCases = relatedUseCases;
	}
}
