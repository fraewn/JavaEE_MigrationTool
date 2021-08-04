package application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import exceptions.MigrationToolVisualException;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Default Class to start an application
 *
 * @param <E> process step
 */
public class StartUp {
	/** LOGGER */
	private static final Logger LOG = LogManager.getLogger();

	private static boolean firstStart = false;

	public static <E extends Enum<E>> Appl<E> startUp(Appl<E> appl) {
		if (!firstStart) {
			firstStart = true;
			Platform.setImplicitExit(false);
			Platform.startup(() -> {
				start(appl);
			});
		} else {
			Platform.runLater(() -> {
				start(appl);
			});
		}
		return appl;
	}

	private static <E extends Enum<E>> void start(Appl<E> appl) {
		try {
			appl.start(new Stage());
		} catch (MigrationToolVisualException e) {
			LOG.error(e.getMessage());
			appl.shutdown();
		} catch (Exception e) {
			appl.shutdown();
			throw new RuntimeException(e);
		}
	}

	public static void shutdown() {
		Platform.exit();
	}
}
