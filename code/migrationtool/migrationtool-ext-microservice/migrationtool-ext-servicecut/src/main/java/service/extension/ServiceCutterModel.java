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
import operations.dto.GenericDTO;
import utils.JsonConverter;

public class ServiceCutterModel extends ModelService {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(ServiceCutterModel.class);

	@Option(name = "-output", usage = "value for defining the location of the json file")
	private String modelPath;

	@Option(name = "-outputName", usage = "value for defining the name of the json file")
	private String modelName;

	private Analyzer analyzer;

	private File resultFile;

	@SuppressWarnings("unchecked")
	@Override
	public void setDTO(GenericDTO<?> dto) {
		GenericDTO<List<ClassDTO>> classes = (GenericDTO<List<ClassDTO>>) dto;
		this.analyzer = new Analyzer(classes.getObject());
	}

	@Override
	public GenericDTO<?> buildDTO() {
		if (this.resultFile == null) {
			return null;
		}
		return new GenericDTO<>(this.resultFile.getAbsolutePath());
	}

	@Override
	public void save() {
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
	}
}
