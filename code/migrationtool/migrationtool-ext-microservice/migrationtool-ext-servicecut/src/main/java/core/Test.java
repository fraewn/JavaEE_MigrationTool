package core;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import service.extension.ServiceCutterInterpreter;

public class Test {

	public static void main(String[] args)
			throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		File f = new File("src/main/resources/model.json");

		ServiceCutterInterpreter interpreter = new ServiceCutterInterpreter();
		interpreter.setCommandLineArguments(new String[] { "-visualization", "-debug" });
		interpreter.process(f.getAbsolutePath());
	}
}
