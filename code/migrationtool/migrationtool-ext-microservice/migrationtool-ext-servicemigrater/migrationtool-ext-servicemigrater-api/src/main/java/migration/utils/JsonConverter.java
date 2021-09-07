package migration.utils;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper class to convert between json and java objects and vice versa
 */
public class JsonConverter {

	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();
	/** mapper class */
	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
	}

	/**
	 * @return the used mapper
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * Converts from a java object to json string
	 *
	 * @param <T> json object
	 * @param o   object
	 * @return json string
	 */
	public static <T> String toJsonString(T o) {
		String res = "";
		try {
			res = JsonConverter.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage());
		}
		return res;
	}

	/**
	 * Converts from a java object to json string. Saves the result in the defined
	 * file
	 *
	 * @param <T> json object
	 * @param o   object
	 * @param f   used file
	 * @return json string
	 */
	public static <T> void toJsonString(File f, T o) {
		try {
			JsonConverter.getMapper().writerWithDefaultPrettyPrinter().writeValue(f, o);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Converts from a json string to java object
	 *
	 * @param <T> json object
	 * @param t   mapping class
	 * @param f   used file
	 * @return java object
	 */
	public static <T> T readJsonFromFile(File file, Class<T> t) {
		T res = null;
		try {
			res = mapper.readValue(file, t);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return res;
	}

	/**
	 * Converts from a json string to java object
	 *
	 * @param <T>  json object
	 * @param t    mapping class
	 * @param json used string
	 * @return java object
	 */
	public static <T> T readJson(String json, Class<T> t) {
		T res = null;
		try {
			res = mapper.readValue(json, t);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return res;
	}
}
