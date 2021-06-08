package utils;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(JsonConverter.class);

	private static ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
	}

	public static ObjectMapper getMapper() {
		return mapper;
	}

	public static String toJsonString(Object o) {
		String res = "";
		try {
			res = JsonConverter.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage());
		}
		return res;
	}

	public static <T> T readJsonFromFile(File file, Class<T> t) {
		T res = null;
		try {
			res = mapper.readValue(file, t);
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		return res;
	}
}
