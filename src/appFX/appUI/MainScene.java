package appFX.appUI;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appPreferences.AppPreferences;
import appFX.appUI.appViews.AppViewManager;
import appFX.appUI.appViews.ConcreteView;
import appFX.appUI.menuBar.AppMenuBar;
import appFX.appUI.utils.AppFXMLComponent;
import appFX.appUI.utils.AppToolManager;
import appFX.framework.AppCommand;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import main.Main;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public class MainScene extends AppFXMLComponent implements Initializable, AppPreferences, ReceivedEvent {
	
	@FXML private SplitPane verticalSplitPane, horizontalSplitPane;
	@FXML private BorderPane frame, leftBorderPane, bottomBorderPane, rightBorderPane;
	@FXML private TabPane leftTabPane, centerTabPane, rightTabPane, bottomTabPane;
	@FXML private ToggleButton selectTool, solderTool, pryTool, editTool, linkTool, controlTool, controlNotTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool;
	@FXML private MenuBar menuBar;
	@FXML private Label appNameLabel;
	@FXML private Button undoButton, redoButton, startSimulationButton;
	
	
	private AppViewManager viewManager;
	private AppToolManager toolManager;
	
	public MainScene() {
		super("MainScene.fxml");
	}
	
	public MenuBar getMenuBar() {
		return menuBar;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ConcreteView.initializeViews();
		AppMenuBar.initializeMenuBar(this);
		
		toolManager = new AppToolManager(selectTool, solderTool, pryTool, editTool, linkTool, controlTool, controlNotTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool);
		toolManager.initializeTools();
		initializeButtons();
		
		viewManager = new AppViewManager(leftTabPane, centerTabPane, rightTabPane, bottomTabPane);
		viewManager.initializeViews(leftBorderPane, rightBorderPane, bottomBorderPane, verticalSplitPane, horizontalSplitPane);
		
		
		appNameLabel.setText(Main.APP_NAME);
	}

	public AppViewManager getViewManager() {
		return viewManager;
	}
	
	public AppToolManager getToolManager() {
		return toolManager;
	}
	
	private void initializeButtons() {
		initializeButton(undoButton, AppCommand.UNDO_FOCUSED_CB);
		initializeButton(redoButton, AppCommand.REDO_FOCUSED_CB);
		initializeButton(startSimulationButton, AppCommand.RUN_SIMULATION);
	}
	
	private void initializeButton(Button button, AppCommand command) {
		button.setOnAction((e)-> AppCommand.doAction(command));
	}
	
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		viewManager.receive(source, methodName, args);
	}
}
