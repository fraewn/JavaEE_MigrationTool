package model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import model.service.ServiceCut;

public class Result {

	/** name of this model */
	private String name;
	/** date of generation */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private Date generatedDate;
	/** result object */
	private ServiceCut isolatedServices;

	public Result() {
		this.name = UUID.randomUUID().toString().substring(0, 8);
		this.generatedDate = new Date();
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
	 * @return the isolatedServices
	 */
	public ServiceCut getIsolatedServices() {
		return this.isolatedServices;
	}

	/**
	 * @param isolatedServices the isolatedServices to set
	 */
	public void setIsolatedServices(ServiceCut isolatedServices) {
		this.isolatedServices = isolatedServices;
	}
}
