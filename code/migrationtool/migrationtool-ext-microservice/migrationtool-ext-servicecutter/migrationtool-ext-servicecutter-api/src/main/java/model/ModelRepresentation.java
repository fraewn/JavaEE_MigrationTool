package model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import model.data.ArchitectureInformation;
import model.erm.EntityRelationDiagram;

/**
 * Model represented as json
 */
@JsonPropertyOrder(value = { "name", "generatedDate" })
public class ModelRepresentation {

	/** name of this model */
	private String name;
	/** date of generation */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date generatedDate;
	/** entityRelationship information */
	private EntityRelationDiagram entityDiagram;
	/** other software system artifacts */
	private ArchitectureInformation information;

	public ModelRepresentation() {
		this.name = UUID.randomUUID().toString().substring(0, 8);
		this.generatedDate = new Date();
		this.entityDiagram = new EntityRelationDiagram();
		this.information = new ArchitectureInformation();
	}

	/**
	 * @return the entityDiagram
	 */
	public EntityRelationDiagram getEntityDiagram() {
		return this.entityDiagram;
	}

	/**
	 * @param entityDiagram the entityDiagram to set
	 */
	public void setEntityDiagram(EntityRelationDiagram entityDiagram) {
		this.entityDiagram = entityDiagram;
	}

	/**
	 * @return the information
	 */
	public ArchitectureInformation getInformation() {
		return this.information;
	}

	/**
	 * @param information the information to set
	 */
	public void setInformation(ArchitectureInformation information) {
		this.information = information;
	}

	/**
	 * @return the generatedDate
	 */
	public Date getGeneratedDate() {
		return this.generatedDate;
	}

	/**
	 * @param generatedDate the generatedDate to set
	 */
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
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
}
