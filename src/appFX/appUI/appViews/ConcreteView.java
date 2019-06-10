package appFX.appUI.appViews;

import appFX.appPreferences.AppPreferences.Booleans;
import appFX.appUI.appViews.gateChooser.CircuitBoardChooser;
import appFX.appUI.appViews.gateChooser.CustomGateChooser;
import appFX.appUI.appViews.gateChooser.CustomOracleChooser;
import appFX.appUI.appViews.gateChooser.PresetGatesChooser;

public enum ConcreteView {
	CONSOLE(Booleans.CONSOLE_OPEN),
	PRESET_GATES_VIEW(Booleans.PRESET_GATES_OPEN),
	CUSTOM_GATES_VIEW(Booleans.CUSTOM_GATES_OPEN),
	CUSTOM_ORACLES_VIEW(Booleans.CUSTOM_ORACLES_OPEN),
	CIRCUITBOARD_VIEW(Booleans.CIRCUITBOARDS_OPEN),
	PROJECT_HIERARCHY(Booleans.PROJECT_HIERARCHY_OPEN);
	;
	
	private AppView appView;
	private Booleans wasOpen;
	
	public static void initializeViews() {
		CONSOLE.appView 			= new Console();
		PRESET_GATES_VIEW.appView  	= new PresetGatesChooser();
		CUSTOM_GATES_VIEW.appView  	= new CustomGateChooser();
		CUSTOM_ORACLES_VIEW.appView = new CustomOracleChooser();
		CIRCUITBOARD_VIEW.appView  	= new CircuitBoardChooser();
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
