package model.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import model.data.Instance;

public class ServiceCut {

	private Collection<Service> services;

	private Map<Instance, Service> serviceCache;

	public ServiceCut() {
		this.services = new ArrayList<>();
		this.serviceCache = new HashMap<>();
	}

	/**
	 * @return the services
	 */
	public Collection<Service> getServices() {
		return this.services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(Collection<Service> services) {
		this.services = services;
	}

	/**
	 * @return the serviceCache
	 */
	public Map<Instance, Service> getServiceCache() {
		return this.serviceCache;
	}

	/**
	 * @param serviceCache the serviceCache to set
	 */
	public void setServiceCache(Map<Instance, Service> serviceCache) {
		this.serviceCache = serviceCache;
	}
}
