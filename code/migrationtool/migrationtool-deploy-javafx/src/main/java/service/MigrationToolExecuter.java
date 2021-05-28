package service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javafx.application.Platform;

public class MigrationToolExecuter {

	public static void execute(String[] args) {
		try (OutputStream output = new FileOutputStream("migrationtool.properties")) {
			Properties prop = new Properties();
			for (String string : args) {
				String[] temp = string.split("=");
				prop.setProperty(temp[0].replace("-", ""), temp[1]);
			}
			// save properties to project root folder
			prop.store(output, null);
			Platform.exit();
			Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "cmd", "/k",
					"java -cp \"migrationtool.jar;plugins/*\" core.Main exec" });
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
