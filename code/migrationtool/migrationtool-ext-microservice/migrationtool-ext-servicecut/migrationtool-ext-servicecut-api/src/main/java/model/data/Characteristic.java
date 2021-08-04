package model.data;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import model.criteria.CompatibitlyCharacteristics;

/**
 * Characteristics describes the priority of a compatibility coupling criteria
 */
@JsonPropertyOrder({ "compabilityCharacteristics" })
@JsonIgnoreProperties({ "name", "characteristic" })
public class Characteristic extends ContextGroup {

	/** characteristic */
	@NotNull
	private CompatibitlyCharacteristics characteristic;

	/**
	 * @return the characteristic
	 */
	public CompatibitlyCharacteristics getCharacteristic() {
		return this.characteristic;
	}

	/**
	 * @return the characteristic
	 */
	@JsonGetter("compabilityCharacteristics")
	public String getCompabilityCharacteristics() {
		if (this.characteristic == null) {
			return null;
		}
		return this.characteristic.getName() + "(" + this.characteristic + ")";
	}

	/**
	 * @param characteristic the characteristic to set
	 */
	public void setCharacteristic(CompatibitlyCharacteristics characteristic) {
		this.characteristic = characteristic;
	}

	/**
	 * @param characteristic the characteristic to set
	 */
	@JsonSetter("compabilityCharacteristics")
	public void setCompabilityCharacteristics(String characteristic) {
		String tmp = characteristic.split("\\(")[1];
		tmp = tmp.substring(0, tmp.length() - 1);
		this.characteristic = CompatibitlyCharacteristics.valueOf(tmp);
	}
}
