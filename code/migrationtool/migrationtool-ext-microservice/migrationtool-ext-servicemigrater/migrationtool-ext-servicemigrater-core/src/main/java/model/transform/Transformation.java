package model.transform;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import migration.model.MigrationModel;
import migration.model.Result;
import migration.model.data.Instance;
import migration.model.data.UseCase;
import migration.model.erm.Entity;
import migration.model.serviceDefintion.MicroService;
import migration.model.serviceDefintion.Service;
import migration.model.serviceDefintion.ServiceCut;
import migration.model.serviceDefintion.ServiceRelation;
import migration.utils.JsonConverter;

import java.util.Set;

import operations.dto.AstDTO;

public class Transformation {

	private Result inputResult;

	private MigrationModel model;

	public Transformation(File inputResultFile) {
		this.inputResult = JsonConverter.readJsonFromFile(inputResultFile, Result.class);
	}

	public void convert(List<AstDTO> input) {
		this.model = new MigrationModel();
		ServiceCut serviceCut = this.inputResult.getIsolatedServices();
		for (Service service : serviceCut.getServices()) {
			MicroService newService = new MicroService();
			newService.setName(service.getName());
			// Build entities
			Set<Instance> instances = service.getInstances();
			Set<Entity> entites = new HashSet<>();
			for (Instance instance : instances) {
				String context = instance.getContext();
				Entity e = null;
				for (Entity entity : entites) {
					if (entity.getName().equals(context)) {
						e = entity;
						break;
					}
				}
				if (e != null) {
					e.getAttributes().add(instance.getName());
				} else {
					Entity newEntity = new Entity(context);
					newEntity.getAttributes().add(instance.getName());
					entites.add(newEntity);
				}
			}
			newService.setEntities(entites);
			// use cases
			for (Entry<String, List<UseCase>> entry : serviceCut.getRelatedUseCases().entrySet()) {
				if (entry.getKey().equals(service.getName())) {
					newService.getRelatedUseCases().addAll(entry.getValue());
				}
			}
			// service relations
			for (ServiceRelation relation : serviceCut.getRelations()) {
				if (relation.getServiceIdA().equals(service.getName())
						|| relation.getServiceIdB().equals(service.getName())) {
					newService.getRelatedRelations().add(relation);
				}
			}

			this.model.getServices().add(newService);
		}
	}

	/**
	 * @return the model
	 */
	public MigrationModel getModel() {
		return this.model;
	}
}
