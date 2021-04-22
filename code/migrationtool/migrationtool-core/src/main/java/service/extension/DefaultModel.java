package service.extension;

import java.util.List;

import org.apache.log4j.Logger;

import operations.ModelService;
import operations.dto.ClassDTO;
import operations.dto.GenericDTO;

public class DefaultModel extends ModelService {

	private static final Logger LOG = Logger.getLogger(DefaultModel.class);

	private List<ClassDTO> classes;

	@SuppressWarnings("unchecked")
	@Override
	public void setDTO(GenericDTO<?> dto) {
		this.classes = (List<ClassDTO>) dto.getObject();
	}

	@Override
	public GenericDTO<?> buildDTO() {
		// Nothing
		return null;
	}

	@Override
	public void save() {
		LOG.info("Run DefaultModel, implement your own variant");
		this.classes.forEach(x -> LOG.info("Consumed class: " + x.getFullName()));
	}
}
