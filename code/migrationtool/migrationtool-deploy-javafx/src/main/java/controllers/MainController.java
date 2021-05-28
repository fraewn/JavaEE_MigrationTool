package controllers;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.jfoenix.controls.JFXListView;

import application.Appl;
import exceptions.MigrationToolRuntimeException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.AnchorPane;
import visualization.ParentController;

public class MainController implements ParentController {

	/** LOGGER */
	private static final Logger LOG = Logger.getLogger(MainController.class);

	@FXML
	private JFXListView<Label> progressList;

	@FXML
	private AnchorPane content;

	@FXML
	private ResourceBundle resources;

	@FXML
	public void initialize() throws IOException {
		this.progressList.setSelectionModel(new NoSelectionModel<Label>());
		this.progressList.setOrientation(Orientation.HORIZONTAL);
		addProgressStep(this.resources.getString("args.title"));
		changeContent("configurator");
	}

	@Override
	public void addProgressStep(String... steps) {
		for (String step : steps) {
			this.progressList.getItems().add(new Label(step));
		}
	}

	@Override
	public void changeContent(String contentFXML) {
		FXMLLoader loader = new FXMLLoader(
				getClass().getClassLoader().getResource("gui/fxml/" + contentFXML + ".fxml"));
		loader.setResources(ResourceBundle.getBundle(Appl.RESOURCE_BUNDLE, Appl.language));
		try {
			Parent content = loader.load();
			AnchorPane.setTopAnchor(content, 0d);
			AnchorPane.setRightAnchor(content, 0d);
			AnchorPane.setLeftAnchor(content, 0d);
			AnchorPane.setBottomAnchor(content, 0d);
			this.content.getChildren().addAll(content);
		} catch (IOException e) {
			e.printStackTrace();
			LOG.info(e.getMessage());
			throw new MigrationToolRuntimeException(e.getMessage());
		}
	}

	class NoSelectionModel<T> extends MultipleSelectionModel<T> {

		@Override
		public ObservableList<Integer> getSelectedIndices() {
			return FXCollections.emptyObservableList();
		}

		@Override
		public ObservableList<T> getSelectedItems() {
			return FXCollections.emptyObservableList();
		}

		@Override
		public void selectIndices(int index, int... indices) {
		}

		@Override
		public void selectAll() {
		}

		@Override
		public void selectFirst() {
		}

		@Override
		public void selectLast() {
		}

		@Override
		public void clearAndSelect(int index) {
		}

		@Override
		public void select(int index) {
		}

		@Override
		public void select(T obj) {
		}

		@Override
		public void clearSelection(int index) {
		}

		@Override
		public void clearSelection() {
		}

		@Override
		public boolean isSelected(int index) {
			return false;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public void selectPrevious() {
		}

		@Override
		public void selectNext() {
		}
	}
}
