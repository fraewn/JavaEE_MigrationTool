package core;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Test {

	public static void main(String[] args)
			throws JsonParseException, JsonMappingException, IOException, InterruptedException {
		File f = new File("servicecut_model.json");

//		ServiceCutInterpreter interpreter = new ServiceCutInterpreter();
//		interpreter.setCommandLineArguments(new String[] { "-visualization" });
//		interpreter.process(f.getAbsolutePath());

		Runner runner = new Runner();
		String[] commands = { "-command=DefaultCommand", "-path=C:\\Users\\Rene\\Desktop\\ratingmgmt-deployable",
				"-model=ServiceCutModel", "-interpreter=ServiceCutInterpreter", "-debug", "-visualization",
				"-recommendation", "-editModel" };
		runner.run(commands);

//		Recommender rec = new Recommender();
//		rec.importArchitecture(new ModelRepresentation(), null, null);
//		Visualizer visual = new RecommanderVisualizer(rec);
//		Map<String, Recommendation> map = new HashMap<>();
//		map.put("test", new Recommendation("Hello.attr", 1, "Rarely(CC_8_LOW)", false));
//		map.put("test2", new Recommendation("Hello21.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test23", new Recommendation("Hello22.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test25", new Recommendation("Hello23.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test26", new Recommendation("Hello24.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test27", new Recommendation("Hello25.attr", 12, "Often(CC_8_HIGH)", true));
//		map.put("test28", new Recommendation("Hello26.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test12", new Recommendation("Hello27.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test22", new Recommendation("Hello28.attr", 12, "Often(CC_8_HIGH)", false));
//		map.put("test32", new Recommendation("Hello29.attr", 12, "Often(CC_8_HIGH)", true));
//		map.put("test42", new Recommendation("Hello12.attr", 12, "Often(CC_8_HIGH)", false));
//		visual.visualizeModel(map, RecommenderProcessingSteps.COMPATIBILITY_CONTENT_VOLATILITY);

//		Recommender recommender = new Recommender();
//		recommender.importArchitecture(new ModelRepresentation(), null, null);
//		RecommanderVisualizer test = new RecommanderVisualizer(recommender);
//		test.visualizeModel(new HashMap<String, Recommendation>(), RecommenderProcessingSteps.LATENCY);
//		Thread.sleep(500);
//		test.stop();
//
//		ModelRepresentation model = JsonConverter.readJsonFromFile(f, ModelRepresentation.class);
//		ServiceSnipperVisualizer t = new ServiceSnipperVisualizer();
//		t.visualizeModel(JsonConverter.toJsonString(model), JsonConverter.toJsonString(model));
	}
}
