package service.extension;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.Option;

import exceptions.MigrationToolRuntimeException;
import migration.model.MigrationModel;
import migration.utils.JsonConverter;
import model.Configurations;
import model.transform.Transformation;
import operations.ModelService;
import operations.dto.AstDTO;

/**
 * Implementation of the process step to create the decomposition model
 */
public class ServiceMigraterModel extends ModelService<List<AstDTO>, Configurations> {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** Default file name */
	private static final String DEFAULT_INPUT_RESULT_FILE_NAME = "result";
	/** Default file name */
	private static final String DEFAULT_MODEL_FILE_NAME = "servicemigrater_model";

	@Option(name = "-inputResultPath", usage = "value for defining the location of the json file")
	private String inputResultPath;

	@Option(name = "-inputResultName", usage = "value for defining the name of the json file")
	private String inputResultName;

	@Option(name = "-output", usage = "value for defining the location of the json file")
	private String modelPath;

	@Option(name = "-outputName", usage = "value for defining the name of the json file")
	private String modelName;

	@Option(name = "-skipModel", usage = "value for enabling the model analyzation")
	private boolean skip;
	/** The result file */
	private File inputResultFile;
	/** The result file */
	private File resultFile;

	@Override
	public Configurations save(List<AstDTO> input) {
		// init default values
		this.inputResultName = this.inputResultName == null ? DEFAULT_INPUT_RESULT_FILE_NAME : this.inputResultName;
		this.inputResultPath = this.inputResultPath == null ? "" : this.inputResultPath + "/";
		this.inputResultFile = new File(this.inputResultPath + this.inputResultName + ".json");

		this.modelName = this.modelName == null ? DEFAULT_MODEL_FILE_NAME : this.modelName;
		this.modelPath = this.modelPath == null ? "" : this.modelPath + "/";
		this.resultFile = new File(this.modelPath + this.modelName + ".json");
		// skip analyzation process
		if (!this.skip) {
			if (!this.inputResultFile.exists()) {
				throw new MigrationToolRuntimeException("Input files needed");
			}
			Transformation converter = new Transformation(this.inputResultFile);
			converter.convert(input);
			MigrationModel rep = converter.getModel();
			// write result file
			if (this.resultFile.exists()) {
				LOG.warn("Override: " + this.resultFile.getName());
				this.resultFile.delete();
			}
			try {
				this.resultFile.createNewFile();
				JsonConverter.toJsonString(this.resultFile, rep);
			} catch (IOException e) {
				throw new MigrationToolRuntimeException(e.getMessage());
			}
		}
		return new Configurations(input, this.resultFile.getAbsolutePath());
	}
}
