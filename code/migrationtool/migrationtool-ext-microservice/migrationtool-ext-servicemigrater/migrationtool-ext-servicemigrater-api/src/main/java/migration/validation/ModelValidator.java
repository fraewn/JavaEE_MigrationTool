package migration.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolInitException;

/**
 * Helper class to validate the model
 */
public class ModelValidator {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Validates the object
	 *
	 * @param <T>    model object
	 * @param object object
	 * @return valid
	 */
	public static <T> boolean validate(T object) {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		for (ConstraintViolation<T> constraintViolation : violations) {
			LOG.error(constraintViolation.getMessage());
			throw new MigrationToolInitException("Model validation FAILED");
		}
		return violations.isEmpty();
	}
}
