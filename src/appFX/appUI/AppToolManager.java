package appFX.appUI;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class AppToolManager {
	public static final int 
		SELECT_TOOL_BUTTON = 0,
		SOLDER_TOOL_BUTTON = 0,
		EDIT_TOOL_BUTTON = 0,
		CONTROL_TOOL_BUTTON = 0,
		CONTROL_NOT_TOOL_BUTTON = 0,
		ADD_COLUMN_TOOL_BUTTON = 0,
		REMOVE_COLUMN_TOOL_BUTTON = 0,
		ADD_ROW_TOOL_BUTTON = 0,
		REMOVE_ROW_TOOL_BUTTON = 0;
	
	private ToggleButton[] toolToggleButtons;
	private ToggleGroup tools;
	private boolean initialized = false;
	
	public AppToolManager(ToggleButton ... toolToggleButtons) {
		this.toolToggleButtons = toolToggleButtons;
	}
	
	public ToggleButton getToolButton(int buttonName) {
		return toolToggleButtons[buttonName];
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
	
	public boolean doesSelectedToolMatch(int buttonName) {
		int i = 0;
		for(ToggleButton toggleButton : toolToggleButtons)
			if (toggleButton == toolToggleButtons[i++]) return true;
		return false;
	}
		
	public void addToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().addListener(listener);
	}
	
	public void removeToolButtonListener(ChangeListener<? super Toggle> listener) {
		tools.selectedToggleProperty().removeListener(listener);
	}
	
}
