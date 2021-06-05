package visualization;

import graph.processing.GraphProcessingSteps;

public interface ParentController {

	void approve(GraphProcessingSteps step, boolean undo);
}
