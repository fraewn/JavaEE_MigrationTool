package visualization;

import graph.processing.GraphProcessingSteps;

public interface ChildController {

	void setParentController(ParentController parent);

	void reachedProcessStep(GraphProcessingSteps step);

	<T> void refreshModel(T dto);

	<T> T getModel();
}
