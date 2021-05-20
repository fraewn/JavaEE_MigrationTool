package model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import model.service.Service;
import model.service.ServiceRelation;

public class Result {

	private Set<Service> services;

	private List<ServiceRelation> relations;

	private Map<String, List<String>> useCaseResponsibility;
}
