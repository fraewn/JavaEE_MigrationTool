package service.extension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.Option;

import core.Analyzer;
import model.ModelRepresentation;
import operations.ModelService;
import operations.dto.ClassDTO;
import utils.JsonConverter;

public class ServiceCutterModel extends ModelService<List<ClassDTO>, String> {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(ServiceCutterModel.class);

	@Option(name = "-output", usage = "value for defining the location of the json file")
	private String modelPath;

	@Option(name = "-outputName", usage = "value for defining the name of the json file")
	private String modelName;

	private Analyzer analyzer;

	private File resultFile;

	@Override
	public String save(List<ClassDTO> input) {
		this.analyzer = new Analyzer(input);
		this.analyzer.convertInput();
		ModelRepresentation rep = this.analyzer.getOutput();
		this.modelName = this.modelName == null ? "servicecut_model" : this.modelName;
		this.modelPath = this.modelPath == null ? "" : this.modelPath + "/";
		this.resultFile = new File(this.modelPath + this.modelName + ".json");
		if (this.resultFile.exists()) {
			LOG.warn("Override: " + this.resultFile.getName());
			this.resultFile.delete();
		}
		try {
			this.resultFile.createNewFile();
			JsonConverter.getMapper().writeValue(this.resultFile, rep);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		return this.resultFile == null ? null : this.resultFile.getAbsolutePath();
	}
}
