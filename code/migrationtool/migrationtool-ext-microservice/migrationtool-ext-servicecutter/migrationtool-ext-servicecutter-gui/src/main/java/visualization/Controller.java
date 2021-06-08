package visualization;

import data.CurrentView;
import graph.processing.GraphProcessingSteps;
import service.gui.Visualizer;

public interface Controller extends Visualizer {

	@Override
	default void stop() {

	}

	void changeContent(CurrentView view);

	boolean isApproved(GraphProcessingSteps step);

	boolean isUndo(GraphProcessingSteps step);
}
