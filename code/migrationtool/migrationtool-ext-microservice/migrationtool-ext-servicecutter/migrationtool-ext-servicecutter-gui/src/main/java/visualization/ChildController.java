package visualization;

import processing.GraphProcessingSteps;

public interface ChildController<T> {

	void setParentController(ParentController parent);

	void reachedProcessStep(GraphProcessingSteps step);

	void refreshContent(T dto);
}
