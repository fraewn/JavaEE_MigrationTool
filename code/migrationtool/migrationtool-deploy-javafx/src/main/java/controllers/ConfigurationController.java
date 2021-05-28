package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import cmd.CommandLineParser;
import cmd.CommandLineValidator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.DefaultStringConverter;
import models.Configurations;
import operations.CommandExtension;
import operations.ProcessingStep;
import service.MigrationToolExecuter;
import utils.ComponentFactory;
import utils.LayoutUtils;
import utils.PluginManager;
import visualization.ChildController;
import visualization.ParentController;

public class ConfigurationController implements ChildController {

	@FXML
	private JFXComboBox<String> commandSelection;

	@FXML
	private GridPane content;

	@FXML
	private JFXButton execute;

	@FXML
	private ResourceBundle resources;

	private Map<Integer, String> currentServices;

	private Configurations model;

	public ConfigurationController() {
		this.model = new Configurations();
		this.currentServices = new LinkedHashMap<>();
	}

	@FXML
	public void initialize() throws IOException {
		this.execute.setDisable(true);
		this.commandSelection.setPromptText(this.resources.getString("args.commandSelection"));
		List<CommandExtension> commands = new ArrayList<>();
		commands.addAll(PluginManager.findPluginCommands(CommandExtension.class));
		List<String> temp = commands.stream().map(CommandExtension::getName).collect(Collectors.toList());
		this.commandSelection.getItems().addAll(temp);
		StringProperty prop = new SimpleStringProperty();
		Bindings.bindBidirectional(prop, this.commandSelection.valueProperty(), new DefaultStringConverter());
		this.model.getBindValues().put("-command", prop);

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setHgrow(Priority.ALWAYS);
		this.content.getColumnConstraints().addAll(col1, col2);
	}

	public void commandAction() {
		CommandExtension ce = PluginManager.findPluginCommand(CommandExtension.class, this.commandSelection.getValue());
		createOptionsOfCommand(ce);
		this.model.setLoadedValues();
		checkValid();
	}

	public void executeAction() {
		new Thread(() -> {
			MigrationToolExecuter.execute(this.model.buildArgs().split(" "));
		}).run();
	}

	public void refreshService() {
		CommandExtension ce = PluginManager.findPluginCommand(CommandExtension.class, this.commandSelection.getValue());
		createOptionsOfService(ce);
	}

	public void checkValid() {
		this.execute.setDisable(!this.model.isFormValid());
	}

	@SuppressWarnings({ "rawtypes" })
	private void createOptionsOfCommand(CommandExtension command) {
		this.content.getChildren().clear();
		this.currentServices.clear();
		this.model.getRequiredFields().clear();
		this.model.getBindValues().keySet().removeIf(x -> !x.equals("-command"));

		List<OptionHandler> list = CommandLineValidator.getAllArguments(command);
		String hLabel = this.resources.getString("args.commandArgsHeader");
		String hToolTip = this.resources.getString("args.commandArgsToolTip");
		GridPane form = createForm(command.getName() + " " + hLabel, hToolTip, list);
		createOptionsOfService(command);
		LayoutUtils.setGridPaneConst(form, 0, 0, this.currentServices.size(), 1);
		this.content.getChildren().add(form);
		Pane hidden = new Pane();
		LayoutUtils.setGridPaneConst(hidden, this.currentServices.size() + 1, 0, Integer.MAX_VALUE, 1);
		this.content.getChildren().add(hidden);
	}

	@SuppressWarnings("rawtypes")
	private void createOptionsOfService(CommandExtension command) {
		List<String> definedSteps = new ArrayList<>();
		CommandLineParser.parseIgnoreErrors(this.model.buildArgs().split(" "), command);
		command.defineSteps(definedSteps);
		// Change new services
		String hLabel = this.resources.getString("args.serviceArgsHeader");
		String hToolTip = this.resources.getString("args.serviceArgsToolTip");
		int x = 0;
		for (String step : definedSteps) {
			ProcessingStep<?, ?> tmp = PluginManager.findPluginService(step);
			// valid step
			if (tmp != null) {
				int pos = this.currentServices.size();
				if (this.currentServices.containsKey(x)) {
					// replace step?
					if (!this.currentServices.get(x).equals(step)) {
						// delete old required field
						ProcessingStep<?, ?> old = PluginManager.findPluginService(this.currentServices.get(x));
						List<OptionHandler> list = CommandLineValidator.getAllArguments(old);
						list.forEach(option -> {
							if (option.option.required()) {
								this.model.getRequiredFields().remove(option.option.toString());
							}
						});
						this.content.getChildren().remove(x);
						pos = x;
					} else {
						// same service
						x++;
						continue;
					}
				}
				// new field
				List<OptionHandler> list = CommandLineValidator.getAllArguments(tmp);
				String className = tmp.getClass().getSimpleName();
				GridPane form = createForm(hLabel + " " + className, hToolTip, list);
				LayoutUtils.setGridPaneConst(form, pos, 1);
				this.content.getChildren().add(pos, form);
				this.currentServices.put(pos, step);
				List<String> options = list.stream().map(y -> y.option.toString()).collect(Collectors.toList());
				this.model.setLoadedValues(options);
			}
			x++;
		}
	}

	@SuppressWarnings("rawtypes")
	private GridPane createForm(String header, String toolTip, List<OptionHandler> list) {
		GridPane form = ComponentFactory.createForm();
		form.getStyleClass().add("args-form");
		// create header
		Label title = ComponentFactory.createLabelAndToolTip(header, toolTip);
		title.setContentDisplay(ContentDisplay.RIGHT);
		title.getStyleClass().add("args-form-header");
		LayoutUtils.setGridPaneConst(title, 0, 0, 1, 2);
		form.getChildren().add(title);
		// create controls
		for (int i = 0; i < list.size(); i++) {
			OptionDef option = list.get(i).option;
			String key = option.toString();
			if (option.required() && !this.model.getRequiredFields().contains(key)) {
				this.model.getRequiredFields().add(key);
			}
			Label info = ComponentFactory.createLabelAndToolTip(key, list.get(i).option.usage());
			LayoutUtils.setGridPaneConst(info, i + 1, 0);
			AnchorPane component = createComponent(key, getTypeOfOption(list.get(i)));
			LayoutUtils.setGridPaneConst(component, i + 1, 1);
			form.getChildren().addAll(info, component);
		}
		return form;
	}

	private Type getTypeOfOption(OptionHandler<?> option) {
		if (option.setter.getType().equals(boolean.class)) {
			return Type.BOOL;
		}
		if (option.setter.getType().equals(int.class)) {
			return Type.NUMBER;
		}
		if (option.setter.getType().equals(String.class) && option.option.toString().contains("path")) {
			return Type.PATH;
		}
		if (option.setter.getType().equals(String.class) && (option.option.toString().contains("loader")
				|| option.option.toString().contains("model") || option.option.toString().contains("interpreter"))) {
			return Type.STEP;
		}
		return Type.TEXT;
	}

	@Override
	public void setParentController(ParentController parent) {

	}

	private AnchorPane createComponent(String key, Type component) {
		component.currentController = this;
		String requiredMsg = key + this.resources.getString("args.requiredMessage");
		AnchorPane pane = component.createComp(this.model, key,
				this.model.getRequiredFields().contains(key) ? requiredMsg : null);
		return pane;
	}

	enum Type {
		TEXT() {
			@Override
			public AnchorPane createComp(Configurations model, String key, String requiredMsg) {
				AnchorPane pane = new AnchorPane();
				JFXTextField field = new JFXTextField();
				if (requiredMsg != null) {
					LayoutUtils.createRequiredValidator(field, requiredMsg);
					field.textProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							this.currentController.checkValid();
						}
					});
				}
				model.getBindValues().put(key, field.textProperty());
				LayoutUtils.setAnchorPaneConst(field);
				pane.getChildren().add(field);
				return pane;
			}
		},
		NUMBER() {
			@Override
			public AnchorPane createComp(Configurations model, String key, String requiredMsg) {
				AnchorPane pane = new AnchorPane();
				JFXTextField field = ComponentFactory.createNumericField();
				if (requiredMsg != null) {
					LayoutUtils.createRequiredValidator(field, requiredMsg);
					field.textProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							this.currentController.checkValid();
						}
					});
				}
				model.getBindValues().put(key, field.textProperty());
				LayoutUtils.setAnchorPaneConst(field);
				pane.getChildren().add(field);
				return pane;
			}
		},
		BOOL() {
			@Override
			public AnchorPane createComp(Configurations model, String key, String requiredMsg) {
				AnchorPane pane = new AnchorPane();
				JFXToggleButton button = new JFXToggleButton();
				StringProperty temp = new SimpleStringProperty();
				Bindings.bindBidirectional(temp, button.selectedProperty(), new BooleanStringConverter());
				model.getBindValues().put(key, temp);
				AnchorPane.setTopAnchor(button, 0d);
				AnchorPane.setRightAnchor(button, 0d);
				AnchorPane.setBottomAnchor(button, 0d);
				pane.getChildren().add(button);
				return pane;
			}
		},
		PATH() {
			@Override
			public AnchorPane createComp(Configurations model, String key, String requiredMsg) {
				AnchorPane pane = new AnchorPane();
				JFXTextField field = new JFXTextField();
				if (requiredMsg != null) {
					LayoutUtils.createRequiredValidator(field, requiredMsg);
					field.textProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							this.currentController.checkValid();
						}
					});
				}
				model.getBindValues().put(key, field.textProperty());
				field.setPadding(new Insets(0, 30, 0, 0));
				LayoutUtils.setAnchorPaneConst(field);
				JFXButton pathButton = new JFXButton();
				pathButton.setOnAction(event -> {
					DirectoryChooser fileChooser = new DirectoryChooser();
					fileChooser.setTitle(this.currentController.resources.getString("args.pathChooser"));
					fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
					File selectedDirectory = fileChooser.showDialog(null);
					field.textProperty().setValue(selectedDirectory != null ? selectedDirectory.getAbsolutePath() : "");
				});
				pathButton.setButtonType(ButtonType.RAISED);
				StackPane stackPane = new StackPane();
				stackPane.getChildren().add(new FontIcon(FontAwesomeSolid.FILE_ALT));
				pathButton.setGraphic(stackPane);
				AnchorPane.setRightAnchor(pathButton, 0d);
				pane.getChildren().addAll(field, pathButton);
				return pane;
			}
		},
		STEP() {
			@Override
			public AnchorPane createComp(Configurations model, String key, String requiredMsg) {
				AnchorPane pane = new AnchorPane();
				JFXTextField field = new JFXTextField();
				if (requiredMsg != null) {
					LayoutUtils.createRequiredValidator(field, requiredMsg);
					field.textProperty().addListener((observable, oldValue, newValue) -> {
						if (newValue != null) {
							this.currentController.checkValid();
						}
					});
				}
				field.textProperty().addListener((observable, oldValue, newValue) -> {
					if ((newValue != null) && !newValue.isEmpty()) {
						this.currentController.refreshService();
					}
				});
				model.getBindValues().put(key, field.textProperty());
				LayoutUtils.setAnchorPaneConst(field);
				pane.getChildren().add(field);
				return pane;
			}
		};

		public ConfigurationController currentController;

		public abstract AnchorPane createComp(Configurations model, String key, String requiredMsg);
	}
}
