package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Appl;
import javafx.beans.property.StringProperty;

public class Configurations {

	private Map<String, StringProperty> bindValues;

	private List<String> requiredFields;

	public Configurations() {
		this.bindValues = new HashMap<>();
		this.requiredFields = new ArrayList<>();
	}

	/**
	 * @return the bindValues
	 */
	public Map<String, StringProperty> getBindValues() {
		return this.bindValues;
	}

	/**
	 * @return the requiredFields
	 */
	public List<String> getRequiredFields() {
		return this.requiredFields;
	}

	public boolean isFormValid() {
		String s = buildArgs();
		for (String required : this.requiredFields) {
			if (!s.contains(required)) {
				return false;
			}
		}
		return true;
	}

	public void setLoadedValues() {
		setLoadedValues(new ArrayList<>(this.bindValues.keySet()));
	}

	public void setLoadedValues(List<String> whiteList) {
		for (Entry<String, StringProperty> entry : this.bindValues.entrySet()) {
			for (String string : Appl.loadedArgs) {
				if (string.contains(entry.getKey()) && whiteList.contains(entry.getKey())) {
					String val = string.contains("=") ? string.split("=")[1] : "true";
					entry.getValue().setValue(val);
					break;
				}
			}
		}
	}

	public String buildArgs() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, StringProperty> binding : this.bindValues.entrySet()) {
			String value = binding.getValue().getValue();
			if (!value.isEmpty()) {
				sb.append(binding.getKey() + "=" + value + " ");
			}
		}
		return sb.length() != 0 ? sb.toString().substring(0, sb.length() - 1) : "";
	}
}
