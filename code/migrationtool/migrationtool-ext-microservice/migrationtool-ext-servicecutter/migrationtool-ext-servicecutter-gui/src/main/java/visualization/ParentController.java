package visualization;

import data.CurrentView;
import data.GenericDTO;
import processing.GraphProcessingSteps;

public interface ParentController {

	void setProgressStep(String step, int procent);

	void changeContent(CurrentView view);

	void refreshContent(GenericDTO<?> dto);

	void needApproval(GraphProcessingSteps step);

	boolean isApproved(GraphProcessingSteps step);

	void approve(GraphProcessingSteps step);
}
