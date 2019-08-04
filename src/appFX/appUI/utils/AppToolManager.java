package appFX.appUI.utils;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class AppToolManager {
	private static int toolCount = 0;
	public static enum AppTool {
		SELECT_TOOL,
		SOLDER_TOOL,
		PRY_TOOL,
		EDIT_TOOL,
		LINK_TOOL,
		CONTROL_TOOL,
		CONTROL_NOT_TOOL,
		ADD_COLUMN_TOOL,
		REMOVE_COLUMN_TOOL,
		ADD_ROW_TOOL,
		REMOVE_ROW_TOOL
		;
		
		private final int toolIndex;
		private AppTool() {
			toolIndex = toolCount++;
		}
	}
	
	private ToggleButton[] toolToggleButtons;
	private ToggleGroup tools;
	private boolean initialized = false;
	
	public AppToolManager(ToggleButton ... toolToggleButtons) {
		this.toolToggleButtons = toolToggleButtons;
	}
	
	public ToggleButton getToolButton(AppTool tool) {
		return toolToggleButtons[tool.toolIndex];
	}
	
	public void initializeTools() {
		if (! initialized ) {
			initialized = true;
			tools  = new ToggleGroup();
			for (ToggleButton button : toolToggleButtons)
				button.setToggleGroup(tools);
		}
	}
	
	public ToggleButton getSelectedTool() {
		return (ToggleButton) tools.getSelectedToggle();
	}
		
	public void addToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().addListener(listener);
	}
	
	public void removeToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().removeListener(listener);
	}
	
}
