package core;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import service.extension.ServiceCutterInterpreter;

public class Test {

	public static void main(String[] args)
			throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		File f = new File("model.json");

		ServiceCutterInterpreter interpreter = new ServiceCutterInterpreter();
		interpreter.setCommandLineArguments(new String[] { "-visualization" });
		interpreter.process(f.getAbsolutePath());

//		Runner runner = new Runner();
//		String[] commands = { "-command=DefaultCommand", "-path=C:\\Users\\Rene\\Desktop\\ratingmgmt-deployable",
//				"-model=ServiceCutterModel", "-interpreter=ServiceCutterInterpreter", "-visualization" };
//		runner.run(commands);

	}
}
