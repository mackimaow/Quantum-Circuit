package appFX.appUI;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appPreferences.AppPreferences;
import appFX.appUI.appViews.AppViewManager;
import appFX.appUI.appViews.ConcreteView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import main.Main;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public class MainScene extends AppFXMLComponent implements Initializable, AppPreferences, ReceivedEvent {

	public SplitPane verticalSplitPane, horizontalSplitPane;
	public BorderPane leftBorderPane, bottomBorderPane, rightBorderPane;
	public TabPane leftTabPane, centerTabPane, rightTabPane, bottomTabPane;
	public ToggleButton selectTool, solderTool, editTool, controlTool, controlNotTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool;
	public MenuBar menuBar;
	public Label appNameLabel;
	
	private AppViewManager viewManager;
	private AppToolManager toolManager;
	
	public MainScene() {
		super("MainScene.fxml");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ConcreteView.initializeViews();
		AppMenuBar.initializeMenuBar(this);
		
		toolManager = new AppToolManager(selectTool, solderTool, editTool, controlTool, controlNotTool, addColumnTool, removeColumnTool, addRowTool, removeRowTool);
		toolManager.initializeTools();
		
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
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		viewManager.receive(source, methodName, args);
	}
}
