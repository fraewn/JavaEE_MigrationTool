package migration.model.erm;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ER-Diagram containing a list of entites and their relationships
 */
public class EntityRelationDiagram {

	/** list of entites */
	private List<Entity> entities;
	/** entity relationships */
	@JsonProperty("relationships")
	private List<EntityRelation> relations;

	public EntityRelationDiagram() {
		this.entities = new ArrayList<>();
		this.relations = new ArrayList<>();
	}

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return this.entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	/**
	 * @return the relations
	 */
	public List<EntityRelation> getRelations() {
		return this.relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(List<EntityRelation> relations) {
		this.relations = relations;
	}

}
