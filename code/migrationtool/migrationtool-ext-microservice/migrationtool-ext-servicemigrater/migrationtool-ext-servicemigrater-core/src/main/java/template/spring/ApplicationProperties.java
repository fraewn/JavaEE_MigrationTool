package template.spring;

import java.util.HashMap;
import java.util.Map;

import utils.PropertiesLoader;

public class ApplicationProperties {

	public static final String FILE_NAME = "templates/spring/applicationProperties.properties";

	private PropertiesLoader loader;

	public ApplicationProperties() {
		this.loader = new PropertiesLoader(FILE_NAME);
		this.loader.loadProps(false);
	}

	public Map<String, Object> getConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put("applicationName", this.loader.getCache().get("applicationName"));
		config.put("serverPort", this.loader.getCache().get("serverPort"));
		config.put("eurekaServiceUrl", this.loader.getCache().get("eurekaServiceUrl"));
		config.put("dataSourceDriver", this.loader.getCache().get("dataSourceDriver"));
		config.put("dataSourceUrl", this.loader.getCache().get("dataSourceUrl"));
		config.put("dataSourceUser", this.loader.getCache().get("dataSourceUser"));
		config.put("dataSourceUserName", this.loader.getCache().get("dataSourceUserName"));
		config.put("dataSourcePassword", this.loader.getCache().get("dataSourcePassword"));
		return config;
	}
}
