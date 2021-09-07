package template.spring;

import java.util.HashMap;
import java.util.Map;

import utils.PropertiesLoader;

public class PomMaven {

	public static final String FILE_NAME = "templates/spring/pomMaven.properties";

	private PropertiesLoader loader;

	public PomMaven() {
		this.loader = new PropertiesLoader(FILE_NAME);
		this.loader.loadProps(false);
	}

	public Map<String, Object> getConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put("groupId", this.loader.getCache().get("groupId"));
		config.put("artifactId", this.loader.getCache().get("artifactId"));
		config.put("version", this.loader.getCache().get("version"));
		return config;
	}
}
