package template;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolRuntimeException;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class ConfigTemplate {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private ConfigTemplate() {

	}

	private static Configuration config;

	private static void init() throws IOException {
		config = new Configuration(Configuration.VERSION_2_3_29);
		config.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
		// Recommended settings for new projects:
		config.setDefaultEncoding("UTF-8");
		config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		config.setLogTemplateExceptions(false);
		config.setWrapUncheckedExceptions(true);
		config.setFallbackOnNullLoopVariable(false);
	}

	public static Configuration getInstance() {
		if (config == null) {
			try {
				init();
			} catch (IOException e) {
				LOG.error(e.getMessage());
				throw new MigrationToolRuntimeException(e.getMessage());
			}
		}
		return config;
	}
}
