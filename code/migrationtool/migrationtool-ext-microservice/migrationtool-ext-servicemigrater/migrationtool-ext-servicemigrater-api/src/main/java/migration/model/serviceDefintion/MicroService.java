package migration.model.serviceDefintion;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import migration.model.data.UseCase;
import migration.model.erm.Entity;

/**
 * Class representing the definition of a microservice
 */
public class MicroService implements Comparator<MicroService>, Comparable<MicroService> {

	/** name of the service */
	private String name;
	/** all related entities to the service */
	private Set<Entity> entities;
	/** All connected use cases of this decomposition */
	private Set<UseCase> relatedUseCases;
	/** All services relationships of this decomposition */
	private Set<ServiceRelation> relatedRelations;

	public MicroService() {
		this.entities = new HashSet<>();
		this.relatedUseCases = new HashSet<>();
		this.relatedRelations = new HashSet<>();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the entities
	 */
	public Set<Entity> getEntities() {
		return this.entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

	/**
	 * @return the relatedUseCases
	 */
	public Set<UseCase> getRelatedUseCases() {
		return this.relatedUseCases;
	}

	/**
	 * @param relatedUseCases the relatedUseCases to set
	 */
	public void setRelatedUseCases(Set<UseCase> relatedUseCases) {
		this.relatedUseCases = relatedUseCases;
	}

	/**
	 * @return the relatedRelations
	 */
	public Set<ServiceRelation> getRelatedRelations() {
		return this.relatedRelations;
	}

	/**
	 * @param relatedRelations the relatedRelations to set
	 */
	public void setRelatedRelations(Set<ServiceRelation> relatedRelations) {
		this.relatedRelations = relatedRelations;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		final MicroService other = (MicroService) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if ((this.entities == null) ? (other.entities != null) : !this.entities.equals(other.entities)) {
			return false;
		}
		if ((this.relatedUseCases == null) ? (other.relatedUseCases != null)
				: !this.relatedUseCases.equals(other.relatedUseCases)) {
			return false;
		}
		if ((this.relatedRelations == null) ? (other.relatedRelations != null)
				: !this.relatedRelations.equals(other.relatedRelations)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = (53 * hash) + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public int compare(MicroService i1, MicroService i2) {
		return i1.getName().compareTo(i2.getName());
	}

	@Override
	public int compareTo(MicroService o) {
		return getName().compareTo(o.getName());
	}
}
