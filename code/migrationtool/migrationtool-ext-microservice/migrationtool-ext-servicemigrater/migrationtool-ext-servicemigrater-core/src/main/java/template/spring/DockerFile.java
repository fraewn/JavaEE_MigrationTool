package template.spring;

import java.util.HashMap;
import java.util.Map;

import utils.PropertiesLoader;

public class DockerFile {

	public static final String FILE_NAME = "templates/spring/dockerFile.properties";

	private PropertiesLoader loader;

	public DockerFile() {
		this.loader = new PropertiesLoader(FILE_NAME);
		this.loader.loadProps(false);
	}

	public Map<String, Object> getConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put("port", this.loader.getCache().get("port"));
		return config;
	}
}
