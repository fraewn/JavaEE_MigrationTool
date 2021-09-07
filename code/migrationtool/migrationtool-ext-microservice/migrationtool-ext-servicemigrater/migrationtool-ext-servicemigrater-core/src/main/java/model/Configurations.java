package model;

import java.util.ArrayList;
import java.util.List;

import operations.dto.AstDTO;

public class Configurations {

	private List<AstDTO> input;

	private String modelFile;

	public Configurations() {
		this.input = new ArrayList<>();
	}

	public Configurations(List<AstDTO> input, String modelFile) {
		this.input = input;
		this.modelFile = modelFile;
	}

	/**
	 * @return the input
	 */
	public List<AstDTO> getInput() {
		return this.input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(List<AstDTO> input) {
		this.input = input;
	}

	/**
	 * @return the modelFile
	 */
	public String getModelFile() {
		return this.modelFile;
	}

	/**
	 * @param modelFile the modelFile to set
	 */
	public void setModelFile(String modelFile) {
		this.modelFile = modelFile;
	}
}
