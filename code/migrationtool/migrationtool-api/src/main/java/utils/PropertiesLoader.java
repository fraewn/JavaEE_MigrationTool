package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolInitException;

/**
 * General class for loading a properties file, intern or extern
 */
public class PropertiesLoader {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	/** content of properties file */
	private Map<String, String> cache;
	/** path to properties file */
	private String fileName;

	public PropertiesLoader(String fileName) {
		this.cache = new HashMap<>();
		this.fileName = fileName;
	}

	/**
	 * Loads the specified properties file
	 */
	public void loadProps(boolean optional) {
		try (InputStream input = new FileInputStream(this.fileName)) {
			Properties prop = new Properties();
			prop.load(input);
			for (Map.Entry<Object, Object> e : prop.entrySet()) {
				this.cache.put(e.getKey().toString(), e.getValue().toString());
			}
		} catch (IOException ex) {
			LOG.info("No properties file found, try local");
			try (InputStream inputLocal = PropertiesLoader.class.getResourceAsStream("/" + this.fileName)) {
				Properties prop = new Properties();
				prop.load(inputLocal);
				for (Map.Entry<Object, Object> e : prop.entrySet()) {
					this.cache.put(e.getKey().toString(), e.getValue().toString());
				}
			} catch (IOException | NullPointerException e) {
				if (!optional) {
					LOG.error("No properties file found, " + e.getMessage());
					throw new MigrationToolInitException(e.getMessage());
				}
			}
		}
	}

	/**
	 * Searched for specific key starting with specified string
	 *
	 * @param startString searched string
	 * @return all keys starting with string
	 */
	public String[] getAllKeysWithStartString(String startString) {
		if (this.cache == null) {
			return null;
		}
		List<String> keys = new ArrayList<>();
		for (String key : this.cache.keySet()) {
			if (key.startsWith(startString)) {
				keys.add(key);
			}
		}
		return keys.toArray(new String[0]);
	}

	/**
	 * @return the cache
	 */
	public Map<String, String> getCache() {
		return this.cache;
	}
}
