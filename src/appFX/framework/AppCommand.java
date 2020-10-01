package appFX.framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.stream.Stream;

import appFX.appUI.MainScene;
import appFX.appUI.appViews.BasicGateModelView;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.AppFXMLComponent;
import appFX.appUI.utils.AppFileIO;
import appFX.appUI.utils.AppTab;
import appFX.appUI.wizards.BasicGateModelEditWizard;
import appFX.appUI.wizards.CircuitBoardPropertiesEditWizard;
import appFX.appUI.wizards.CircuitBoardToPNGWizard;
import appFX.appUI.wizards.Wizard;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;
import appFX.framework.exportGates.GateManager.ExportException;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.CircuitBoardModel.RowType;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.GateComputingType;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.simulator.quickSim.QuickSim;
import appFX.framework.solderedGates.SolderedControlPin;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import appFX.framework.solderedGates.SpacePin;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utils.PrintStream;
import utils.PrintStream.SystemPrintStream;
import utils.customCollections.CommandParameterList;
import utils.customCollections.Pair;

/**
 * All application Commands are listed here.
 * @author quantumresearch
 *
 */
public enum AppCommand {
	HELP((commandResponse, parameters)-> {
		commandResponse.println("Command List (Commands are not Case Sensitive): \n", Color.BLUE);			
		for(AppCommand command : AppCommand.values())
			commandResponse.println(command.name(), Color.GREEN);
		return null;
	}),
	
	
	
	
	OPEN_USER_PREFERENCES((commandResponse, parameters)-> {
		
		return null;
	}),
	
	
	
	
	EXPORT_TO_PNG_IMAGE((commandResponse, parameters)-> {
		Wizard<Pair<BufferedImage, File>> wizard = new CircuitBoardToPNGWizard();
		CircuitBoardView  cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		String circuitBoardName = "";
		if(cbv != null)
			circuitBoardName = cbv.getCircuitBoardModel().getLocationString();
		wizard.openWizardAndGetElement(circuitBoardName);
		return null;
	}),
	EXPORT_TO_QUIL((commandResponse, parameters)-> {
		String code = Translator.exportToQUIL(getCurrentProject());
		getConsole().println(code);
		return null;
	}),
	EXPORT_TO_QASM((commandResponse, parameters)-> {
		
		return null;
	}),
	EXPORT_TO_QUIPPER((commandResponse, parameters)-> {
		
		return null;
	}),
	
	
	
	
	IMPORT_FROM_QUIL((commandResponse, parameters)-> {
		
		return null;
	}),
	IMPORT_FROM_QASM((commandResponse, parameters)-> {
		
		return null;
	}),
	IMPORT_FROM_QUIPPER((commandResponse, parameters)-> {
		
		return null;
	}),
	
	
	
	
	OPEN_NEW_PROJECT((commandResponse, parameters)-> {
		getAppStatus().setFocusedProject(Project.createNewTemplateProject());
		return null;
	}),
	OPEN_PROJECT((commandResponse, parameters)-> {
		Project newProject = AppFileIO.openProject(getPrimaryStage());
		if(newProject != null)
			getAppStatus().setFocusedProject(newProject);
		return null;
	}),
	SAVE_PROJECT_TO_FILESYSTEM((commandResponse, parameters)-> {
		if(AppFileIO.saveProjectAs(getCurrentProject(), getPrimaryStage()) == AppFileIO.SUCCESSFUL)
			getAppStatus().setProjectSavedFlag();
		return null;
	}),
	SAVE_PROJECT((commandResponse, parameters)-> {
		if(AppFileIO.saveProject(getCurrentProject(), getPrimaryStage()) == AppFileIO.SUCCESSFUL)
			getAppStatus().setProjectSavedFlag();
		else 
			return false;
		return null;
	}),
	
	
	
	
	RESET_CURRENT_TOOL((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		cbv.getToolActionManager().resetCurrentTool();
		return null;
	}),
	ADD_ROW_TO_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		
		RowType rowType;
		
		GateComputingType computingType = cb.getComputingType();
		if(computingType == GateComputingType.CLASSICAL) {
			rowType = RowType.CLASSICAL;
		} else if (computingType == GateComputingType.QUANTUM){
			rowType = RowType.QUANTUM;
		} else {
			rowType = RowType.CLASSICAL_AND_QUANTUM;
		}
		
		try {
			cb.addRows(cb.getRows(), 1, rowType);
		} catch (IllegalArgumentException e) {
			AppAlerts.showMessage(getPrimaryStage(), "Could not add Row", e.getMessage(), AlertType.ERROR);
		}
		return null;
	}),
	ADD_COLUMN_TO_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		
		try {
			cb.addColumns(cb.getColumns(), 1);
		} catch (IllegalArgumentException e) {
			AppAlerts.showMessage(getPrimaryStage(), "Could not add Column", e.getMessage(), AlertType.ERROR);
		}
		return null;
	}),
	REMOVE_ROW_FROM_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		
		try {
			cb.removeRows(cb.getRows() - 1, cb.getRows());
		} catch (IllegalArgumentException e) {
			AppAlerts.showMessage(getPrimaryStage(), "Could not remove Row", e.getMessage(), AlertType.ERROR);
		}
		return null;
	}),
	REMOVE_COLUMN_FROM_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		
		try {
			cb.removeColumns(cb.getColumns() - 1, cb.getColumns());
		} catch (IllegalArgumentException e) {
			AppAlerts.showMessage(getPrimaryStage(), "Could not remove Column", e.getMessage(), AlertType.ERROR);
		}
		return null;
	}),
	UNDO_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		
		if(cbv == null)
			return null;
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		cb.undo();
		return null;
	}),
	REDO_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		if(cbv == null)
			return null;
		
		CircuitBoardModel cb = cbv.getCircuitBoardModel();
		cb.redo();
		return null;
	}),
	ZOOM_IN_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		if(cbv == null)
			return null;
		
		CircuitBoardRenderer renderer = cbv.getRenderer();
		renderer.zoom(renderer.getZoom() + .1d);
		return null;
	}),
	ZOOM_OUT_FOCUSED_CB((commandResponse, parameters)-> {
		CircuitBoardView cbv = getFocusedCircuitBoardView(getMainScene(), commandResponse);
		if(cbv == null)
			return null;
		
		CircuitBoardRenderer renderer = cbv.getRenderer();
		renderer.zoom(renderer.getZoom() - .1d);
		return null;
	}),
	
	

	
	QUICK_SIM ((commandResponse, parameters) -> {
		try {
			QuickSim.simulate(getCurrentProject());
		} catch (ExportException e) {
			e.printStackTrace();
		}
		return null;
	}),
	
	RUN_QUIL((commandResponse, parameters)-> {
		getConsole().println("Running QUIL", Color.BLUE);
		String quil = Translator.exportToQUIL(getCurrentProject());
		getConsole().println(Executor.execute(quil));
		return null;
	}),
	RUN_QASM((commandResponse, parameters)-> {
		getConsole().println("Running QASM", Color.BLUE);
		getConsole().println("QASM run is Not Supported Yet", Color.RED);
		return null;
	}),
	RUN_SIMULATION((commandResponse, parameters)-> {
		getConsole().println("Running Simulation", Color.BLUE);
		String output = Executor.executeInternal(getCurrentProject());
		getConsole().println(output);
		System.out.println(output);
//		AppCommand.doAction(commandResponse, AppCommand.QUICK_SIM, parameters);
		
		
		return null;
	}),
	
	
	
	REMOVE_GATE((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		for(String param : parameters.stringIterable()) {
			GateModel gm = currentProject.getGateModel(param);
			
			if(!assertExists(param, gm, commandResponse))
				continue;
			
			if (gm.isPreset()) {
				commandResponse.printErrln("Gate \"" + param +  "\" is a preset gate and cannot be removed");
				continue;
			}

			ButtonType buttonType = AppAlerts.showMessage(getPrimaryStage(), "Remove Gate?", "Are you sure you want to remove this gate? "
					+ "All instances of this gate will be removed.", AlertType.CONFIRMATION);
			
			if(buttonType != ButtonType.OK)
				return null;
			
			if(gm instanceof BasicGateModel) {
				currentProject.getCustomGates().remove(gm.getLocationString());
			} else if (gm instanceof CircuitBoardModel) {
				currentProject.getCircuitBoardModels().remove(gm.getLocationString());
			}
		}
		return null;
	}),
	EDIT_GATE((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		for(String param : parameters.stringIterable()) {
			GateModel gm = currentProject.getGateModel(param);
			if(!assertExists(param, gm, commandResponse))
				continue;
			
			if(gm.isPreset()) {
				commandResponse.println("Gate \"" + param +  "\" is a preset gate and cannot be edited");
				continue;
			}
			
			if(gm instanceof BasicGateModel) {
				BasicGateModelEditWizard.editGate(gm.getLocationString());
			} else if (gm instanceof CircuitBoardModel) {
				CircuitBoardPropertiesEditWizard.editGate(gm.getLocationString());
			}
		}
		return null;
	}),
	EDIT_AS_NEW_GATE((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		for(String param : parameters.stringIterable()) {
			GateModel gm = currentProject.getGateModel(param);
			if(!assertExists(param, gm, commandResponse))
				continue;
			
			if(gm instanceof BasicGateModel) {
				BasicGateModelEditWizard.editAsNewGate(gm.getLocationString());
			} else if (gm instanceof CircuitBoardModel) {
				CircuitBoardPropertiesEditWizard.editAsNewGate(gm.getLocationString());
			}
		}
		return null;
	}),
	CREATE_GATE((commandResponse, parameters)-> {
		for(String param : parameters.stringIterable()) {
			String[] parts = param.split("\\.");
			
			String name = parts[0];
			String ext = parts[1]; 
			if(parts.length != 2) {
				commandResponse.printErrln("The formal name \"" + param +  "\" is not a valid name. It must have a proper extension.");
				continue;
			}
			if(ext.equals(BasicGateModel.GATE_MODEL_EXTENSION)) {
				BasicGateModelEditWizard.createNewGate(name);
			} else if (ext.equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				CircuitBoardPropertiesEditWizard.createNewGate(name);
			}
		}
		return null;
	}),
	RENAME_GATE((commandResponse, parameters)-> {
		String oldGateLocationString = parameters.getString(0);
		String newGateLocationString = parameters.getString(1);
		
		if(oldGateLocationString.equals(newGateLocationString)) {
			commandResponse.println("The old name and new name are the same. No refactoring took place."); 
			return null;
		}
		
		Project currentProject = getCurrentProject();
		
		GateModel gmOld = currentProject.getGateModel(oldGateLocationString);
		
		if(!assertExists(oldGateLocationString, gmOld, commandResponse))
			return null;
		
		GateModel gmNew = currentProject.getGateModel(newGateLocationString);
		
		if(gmNew != null) {
			if(gmNew.isPreset()) {
				commandResponse.printErrln("Gate \"" + newGateLocationString +  "\" is a preset gate and cannot be renamed");
				AppAlerts.showMessage(getPrimaryStage(), "Cannot rename Gate", 
						"The gate is renamed to preset Gate which cannot be modified", AlertType.ERROR);
				return null;
			}
			
			ButtonType buttonType = AppAlerts.showMessage(getPrimaryStage(), "Override Gate Model " + gmNew.getName(), 
					"A Gate Model with the name \"" + gmNew.getName() + "\" already exists, "
							+ "do you want to override this gate model?"
							+ " All instances of the previous implementation of \""
							+ gmNew.getName() + "\" in this project will be removed.", AlertType.WARNING);
			if(buttonType != ButtonType.OK)
				return null;
		}
		
		String newGateName = gmOld.getName();
		String newGateSymbol = gmOld.getSymbol();
		String newGateDescription = gmOld.getDescription();
		
		if(parameters.size() > 2) {
			newGateSymbol = parameters.getString(2);
			if(parameters.size() > 3)
				newGateDescription = parameters.getString(3);
		}

		
		GateModel replacement = gmOld.shallowCopyToNewName(newGateLocationString, newGateName, newGateSymbol, newGateDescription);
		
		if(gmOld instanceof CircuitBoardModel) {
			currentProject.getCircuitBoardModels().replace(oldGateLocationString, replacement);
			CircuitBoardView.openCircuitBoard(newGateLocationString);
		} else if (gmOld instanceof BasicGateModel) {
			currentProject.getCustomGates().replace(oldGateLocationString, replacement);
			getMainScene().getViewManager().addView(new BasicGateModelView((BasicGateModel)replacement));
		} else {
			return null;
		}
		return null;
	}),
	CREATE_CIRCUIT_BOARD((commandResponse, parameters)-> {
		CircuitBoardPropertiesEditWizard.createNewGate();
		return null;
	}),
	CREATE_DEFAULT_GATE((commandResponse, parameters)-> {
		BasicGateModelEditWizard.createNewGate();
		return null;
	}),
	OPEN_GATE((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		for(String param : parameters.stringIterable()) {
			GateModel gm = currentProject.getGateModel(param);
			if(!assertExists(param, gm, commandResponse))
				continue;
			
			if(gm instanceof BasicGateModel) {
				getMainScene().getViewManager().addView(new BasicGateModelView((BasicGateModel) gm));
			} else if (gm instanceof CircuitBoardModel) {
				CircuitBoardView.openCircuitBoard(gm.getLocationString());
			}
		}
		return null;
	}),
	OPEN_CIRCUIT_BOARD_AND_FOCUS((commandResponse, parameters)-> {
		String gateName = parameters.getString(0);
		CircuitBoardView cbv = CircuitBoardView.openCircuitBoard(gateName);
		double row = getDoubleFromObject(parameters.get(1));
		double column = getDoubleFromObject(parameters.get(2));
		double zoom = getDoubleFromObject(parameters.get(2));
		cbv.getRenderer().scrollToGrid(row, column, zoom);
		return null;
	}),
	OPEN_CIRCUIT_BOARD_AND_SHOW_ERROR((commandResponse, parameters)-> {
		String gateName = parameters.getString(0);
		CircuitBoardView cbv = CircuitBoardView.openCircuitBoard(gateName);
		int rowStart = getIntFromObject(parameters.get(1));
		int rowEnd 	=  getIntFromObject(parameters.get(2));
		int columnInt = getIntFromObject(parameters.get(3));
		cbv.renderErrorAt(rowStart, rowEnd, columnInt);
		return null;
	}),
	SET_AS_TOP_LEVEL((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		GateModel gm = currentProject.getGateModel(parameters.getString(0));
		
		if(!assertExists(parameters.getString(0), gm, commandResponse))
			return null;
		
		if(gm instanceof CircuitBoardModel) {
			if(gm.getLocationString().equals(currentProject.getTopLevelCircuitLocationString())) {
				commandResponse.println("Circuit Board \"" + parameters.get(0) + "\" is already top level");
				return null;
			} else {
				currentProject.setTopLevelCircuitName(parameters.getString(0));
			}
		} else {
			commandResponse.println("Gate \"" + parameters.get(0) + "\" is not a circuit board");
		}
		return null;
	}),
	REMOVE_TOP_LEVEL((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		if(currentProject.getTopLevelCircuitLocationString() == null) {
			commandResponse.println("There is no circuit board set as top level");
			return null;
		}
		currentProject.setTopLevelCircuitName(null);
		return null;
	}),
	LIST_USER_GATES((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		commandResponse.println("Project Circuit Boards:", Color.BLUE);
		for(String modelName : currentProject.getCircuitBoardModels().getGateNameIterable())
			commandResponse.println(modelName);

		commandResponse.println("\nProject Custom Gates:", Color.BLUE);
		for(String modelName : currentProject.getCustomGates().getGateNameIterable())
			commandResponse.println(modelName);
		return null;
	}),
	
	
	
	ADD_UNTITLED_CIRCUIT_BOARD((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		CircuitBoardView.openCircuitBoard(currentProject.addUntitledSubCircuit());
		return null;
	}),
	
	
	
	
	DEBUG_CIRCUIT_BOARD((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		CircuitBoardModel model = (CircuitBoardModel) currentProject.getCircuitBoardModels().get(parameters.getString(0));
		if(!assertExists(parameters.getString(0), model, commandResponse))
			return null;
			
		int solderID = -1;
		SolderedGate sg = null;
		
		String[][] debugS = new String[model.getColumns()][model.getRows()];
		
		int[] largestSpaces = new int[model.getColumns()];
		
		for(int c = 0; c < model.getColumns(); c++) {
			for(int r = 0; r < model.getRows(); r++) {
				SolderedPin sp = model.getSolderedPinAt(r, c);
				String type = "";
				if(sp instanceof SolderedRegister) {
					type = Integer.toString(((SolderedRegister)sp).getSolderedGatePinNumber());
				} else if(sp instanceof SpacePin) {
					SpacePin spacePin = (SpacePin) sp;
					type = "(";
					if(spacePin.isInputLinked())
						type += spacePin.getInputReg();
					if(sp instanceof SolderedControlPin) {
						type += ((SolderedControlPin)sp).getControlStatus() ? "T" : "F";
					} else {
						type += "_";
					}
					if(spacePin.isOutputLinked())
						type += spacePin.getOutputReg();
					type += ")";
				}
				SolderedGate next  = sp.getSolderedGate();
				if(next != sg) {
					sg = next;
					solderID++;
				}
				
				String inBodyString = sp.isWithinBody()? "I" : "O";
				
				String firstCharName = sg.getGateModelLocationString().substring(0, 1);
				GateModel gm = currentProject.getGateModel(sg.getGateModelLocationString());
				if(gm != null)
					firstCharName = gm.getSymbol();
				
				debugS[c][r] = firstCharName + type + "->(" + inBodyString + solderID + ")";
				
				largestSpaces[c] = largestSpaces[c] > debugS[c][r].length() ? largestSpaces[c] : debugS[c][r].length();
			}
		}
		
		for(int r = 0; r < model.getRows(); r++) {
			for(int c = 0; c < model.getColumns(); c++) {
				commandResponse.print(debugS[c][r]);
				String offsetFix = "  ";
				for(int i = debugS[c][r].length(); i < largestSpaces[c]; i++)
					offsetFix += " ";
				commandResponse.print(offsetFix);
			}
			commandResponse.println("");
		}
		return null;
	}),
	
	
	
	
	GET_GATE((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		GateModel gm = currentProject.getGateModel(parameters.getString(0));
		if(!assertExists(parameters.getString(0), gm, commandResponse))
			return null;
		return gm;
	}),
	GET_GATE_INSTANCES((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		GateModel gm = currentProject.getGateModel(parameters.getString(0));
		
		if(!assertExists(parameters.getString(0), gm, commandResponse))
			return null;
		
		if(!(gm instanceof CircuitBoardModel)) {
			commandResponse.printErrln("Gate \"" + parameters.get(0) +  "\" must be a circuit board");
			return null;
		}
		
		
		if(!currentProject.containsGateModel(parameters.getString(1))) {
			commandResponse.printErrln("Gate \"" + parameters.get(1) +  "\" does not exist");
			return null;
		}
		
		int insts = ((CircuitBoardModel) gm).getOccurrences(parameters.getString(1));
		
		commandResponse.println(Integer.toString(insts));
		
		return insts;
	}),
	
	
	
	
	TEST_SIMULATION((commandResponse, parameters)-> {
		Project currentProject = getCurrentProject();
		try {
			Stream<ExportedGate> exportedGates = GateManager.exportGatesRecursively(currentProject);
			exportedGates = exportedGates.filter((gate) -> {
				if(gate.isPresetGate()) {
					PresetGateType type = gate.getPresetGateType();
					if(type == PresetGateType.IDENTITY)
						return false;
				}
				return true;
			});
			exportedGates.forEach((gate) -> {
				System.out.println("New Gate");
				int[] registers = gate.getGateRegisters();
				for(int i = 0; i < registers.length; i++)
					System.out.print(registers[i] + " ");
				Control[] controls = gate.getQuantumControls();
				for(int i = 0; i < controls.length; i++)
					System.out.print(controls[i].getControlStatus() + " ");
				System.out.println("");
			});
			exportedGates.close();
		} catch (ExportException e) {
			e.printStackTrace();
		}
		return null;
	})
	
	;
	
	private final String helpText;
	private final OnActionRunnable onActionRunnable;
	
	private AppCommand(OnActionRunnable onActionRunnable) {
		this("", onActionRunnable);
	}
	
	private AppCommand(String helpText, OnActionRunnable onActionRunnable) {
		this.helpText = helpText;
		this.onActionRunnable = onActionRunnable;
	}
	
	public String getHelpText() {
		return helpText;
	}
	
	
	private static interface OnActionRunnable {
		public Object onRun(PrintStream commandResponse, CommandParameterList parameters);
	}
	
	public static MainScene getMainScene() {
		return getAppStatus().getMainScene();
	}
	
	public static PrintStream getConsole() {
		return getAppStatus().getConsole();
	}
	
	public static Stage getPrimaryStage() {
		return getAppStatus().getPrimaryStage();
	}
	
	public static Project getCurrentProject() {
		return getAppStatus().getFocusedProject();
	}
	
	public static AppStatus getAppStatus() {
		return AppStatus.get();
	}
	
	public static AppCommand getbyName(String command) { 
		for(AppCommand ac : AppCommand.values())
			if(ac.name().equalsIgnoreCase(command))
				return ac;
		return null;
	}
	
	public static Object doAction(AppCommand actionCommand, Object ... parameters) {
		return doAction(SystemPrintStream.get(), actionCommand, new CommandParameterList(parameters));
	}
	
	
	public static Object doAction(PrintStream commandResponse, AppCommand actionCommand, CommandParameterList parameters) {
		return actionCommand.onActionRunnable.onRun(commandResponse, parameters);
	}
	
	private static int getIntFromObject(Object o) {
		int i;
		if(o instanceof String)
			i = Integer.parseInt((String)o);
		else
			i = (Integer) o;
		return i;
	}
	
	private static double getDoubleFromObject(Object o) {
		double d;
		if(o instanceof String)
			d = Double.parseDouble((String)o);
		else
			d = (Double) o;
		return d;
	}
	
	private static boolean assertExists(String gateModel, GateModel gm, PrintStream commandResponse) {
		if(gm == null) {
			commandResponse.println("Gate \"" + gateModel +  "\" does not exist");
			return false;
		} else {
			return true;
		}
	}
	
	private static CircuitBoardView getFocusedCircuitBoardView(MainScene ms, PrintStream commandResponse) {
		AppTab t = ms.getViewManager().getCenterFocusedView();
		if(t == null) {
			commandResponse.printErrln("No circuit board is opened and focused");
			return null;
		}
		
		AppFXMLComponent component = t.getAppFXMLComponent();
		
		if(!(component instanceof CircuitBoardView)) {
			commandResponse.printErrln("No circuit board is opened and focused");
			return null;
		}
			
		return (CircuitBoardView) component;
	}
	
}
