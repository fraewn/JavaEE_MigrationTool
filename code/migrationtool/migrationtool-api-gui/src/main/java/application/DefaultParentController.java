package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import controllers.ChildController;
import controllers.MainControl;
import controllers.ParentController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

/**
 * Default Implementation of a parent controller containing all important
 * functions
 *
 * @param <E> processing step
 */
public abstract class DefaultParentController<E extends Enum<E>> implements MainControl<E>, ParentController<E> {

	/** label to show the current processing step */
	@FXML
	protected Label labelStep;

	/** label to show the indicator of the progressbar */
	@FXML
	protected Label progressbarIndicator;

	/** progressbar to show the current process */
	@FXML
	protected ProgressBar progressbar;

	/** Current resource bundle */
	@FXML
	protected ResourceBundle resources;
	/** Current step */
	protected E currentStep;
	/** List of all childcontrollers */
	protected List<ChildController<E>> children;
	/** Container to save the state of user important user approvals */
	private Map<E, Approval> approvementMap;

	public DefaultParentController() {
		this.approvementMap = new HashMap<>();
		this.children = new ArrayList<>();
	}

	@Override
	public void callbackEvent() {

	}

	@Override
	public void callbackEvent(ChildController<E> caller) {

	}

	@Override
	public void setProgress(String label, int progress) {
		this.labelStep.setText(label);
		KeyFrame kfBar = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbar.progressProperty(), progress / 100.));
		KeyFrame kfLabel = new KeyFrame(new Duration(3000),
				new KeyValue(this.progressbarIndicator.textProperty(), "" + progress + "%"));
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().addAll(kfBar, kfLabel);
		timeline.play();
	}

	@Override
	public void awaitApproval(E step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			a.approved = false;
			a.awaitApproval = true;
		} else {
			Approval a = new Approval();
			a.awaitApproval = true;
			this.approvementMap.put(step, a);
		}
		for (ChildController<E> childController : this.children) {
			childController.reachedProcessStep(step);
		}
	}

	@Override
	public void approve(E step, boolean undo) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval) {
				a.approved = true;
				if (undo) {
					a.undo = true;
				}
			}
		}
	}

	@Override
	public boolean isUndo(E step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval && a.undo) {
				a.undo = false; // fix Gui thread to slow
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isApproved(E step) {
		if (this.approvementMap.containsKey(step)) {
			Approval a = this.approvementMap.get(step);
			if (a.awaitApproval && a.approved) {
				a.approved = false; // fix Gui thread to slow
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper class
	 */
	class Approval {
		boolean awaitApproval;
		boolean approved;
		boolean undo;
	}
}
