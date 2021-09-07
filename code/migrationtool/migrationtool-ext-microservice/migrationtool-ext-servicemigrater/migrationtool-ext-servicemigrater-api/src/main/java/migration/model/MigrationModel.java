package migration.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import migration.model.serviceDefintion.MicroService;

/**
 * Model represented as json
 */
@JsonPropertyOrder(value = { "name", "generatedDate" })
public class MigrationModel {

	/** name of this model */
	@NotBlank
	private String name;
	/** date of generation */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	@NotNull
	@PastOrPresent
	private Date generatedDate;
	/** entityRelationship information */
	@NotNull
	private List<MicroService> services;

	public MigrationModel() {
		this.name = UUID.randomUUID().toString().substring(0, 8);
		this.generatedDate = new Date();
		this.services = new ArrayList<>();
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
	 * @return the services
	 */
	public List<MicroService> getServices() {
		return this.services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(List<MicroService> services) {
		this.services = services;
	}
}
