package appFX.appUI.appViews;

import appFX.appPreferences.AppPreferences.Booleans;
import appFX.appUI.appViews.gateChooser.CircuitBoardChooserView;
import appFX.appUI.appViews.gateChooser.CustomGateChooserView;
import appFX.appUI.appViews.gateChooser.PresetGatesChooserView;

public enum ConcreteView {
	CONSOLE(Booleans.CONSOLE_OPEN),
	PRESET_GATES_VIEW(Booleans.PRESET_GATES_OPEN),
	CUSTOM_GATES_VIEW(Booleans.CUSTOM_GATES_OPEN),
	CIRCUITBOARD_VIEW(Booleans.CIRCUITBOARDS_OPEN),
	PROJECT_HIERARCHY(Booleans.PROJECT_HIERARCHY_OPEN);
	;
	
	private AppView appView;
	private Booleans wasOpen;
	
	public static void initializeViews() {
		CONSOLE.appView 			= new ConsoleView();
		PRESET_GATES_VIEW.appView  	= new PresetGatesChooserView();
		CUSTOM_GATES_VIEW.appView  	= new CustomGateChooserView();
		CIRCUITBOARD_VIEW.appView  	= new CircuitBoardChooserView();
		PROJECT_HIERARCHY.appView  	= new ProjectHierarchy();
	}
	
	private ConcreteView(Booleans wasOpen) {
		this.wasOpen = wasOpen;
	}
	
	public AppView getView() {
		return appView;
	}
	
	public Booleans wasOpen() {
		return wasOpen;
	}
}
