package appFX.appUI.wizards;

import java.util.HashSet;
import java.util.Iterator;

import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.RearrangableParameterListPaneWrapper;
import appFX.appUI.utils.SequencePaneElement;
import appFX.appUI.utils.SequencePaneElement.SequencePaneFinish;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.Project.ProjectHashtable;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.NameTakenException;
import appFX.framework.gateModels.PresetGateType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.customCollections.immutableLists.ImmutableArray;

public class CircuitBoardPropertiesEditWizard extends Wizard<CircuitBoardModel> {

	
	
	private boolean editAsNew;
	private CircuitBoardModel referencedCb;
	private CircuitBoardModel newCb;
	
	private FirstElementPane first;
	
	
	
	public static CircuitBoardModel createNewGate() {
		return openGateEditableView(null, true);
	}
	
	
	public static CircuitBoardModel createNewGate(String name) {
		Wizard<CircuitBoardModel> wizard = new CircuitBoardPropertiesEditWizard();
		return wizard.openWizardAndGetElement(name);
	}
	
	
	public static CircuitBoardModel editGate(String name) {
		return openGateEditableView(name, false);
	}
	
	public static CircuitBoardModel editAsNewGate(String name) {
		return openGateEditableView(name, true);
	}
	
	
	
	private static CircuitBoardModel openGateEditableView(String name, boolean editAsNewModel) {
		Project p = AppStatus.get().getFocusedProject();
		CircuitBoardModel gm = name == null ? null : (CircuitBoardModel) p.getCircuitBoardModels().get(name);

		Wizard<CircuitBoardModel> wizard = new CircuitBoardPropertiesEditWizard(gm, editAsNewModel);
		
		if(gm == null)
			wizard.setTitle("Create new Gate");
		else if(editAsNewModel)
			wizard.setTitle("Coping " + gm.getName() + " to new Gate");
		else
			wizard.setTitle("Changing " + gm.getName() + " Gate");
		
		return wizard.openWizardAndGetElement();
	}
	
	
	public CircuitBoardPropertiesEditWizard() {
		this(null, true);
	}
	
	public CircuitBoardPropertiesEditWizard(CircuitBoardModel referencedCb, boolean editAsNew) {
		super(650, 700);
		this.referencedCb = referencedCb;
		this.editAsNew = editAsNew;
		this.first = new FirstElementPane();
	}
	
	
	@Override
	protected SequencePaneElement getFirstSeqPaneElem() {
		return first;
	}
	
	@Override
	protected boolean onWizardCloseRequest() {
		return true;
	}
	
	private class FirstElementPane extends SequencePaneElement implements SequencePaneFinish<CircuitBoardModel> {
		
		@FXML private TextField name, symbol;
		@FXML private TextArea description;
		@FXML private VBox parameters;
		
		private RearrangableParameterListPaneWrapper parameterSelection;
		
		public FirstElementPane() {
			super("wizards/circuitBoardPropertiesWizard/CircuitBoardProperties.fxml");
		}

		@Override
		public void initialize() {
			parameterSelection = new RearrangableParameterListPaneWrapper(parameters);
		}
		
		@Override
		public void setStartingFieldData(PaneFieldDataList fieldData) {
			String nameString = "";
			String symbolString = "";
			String descriptionString = "";
			
			if(referencedCb != null) {
				nameString = referencedCb.getName();
				symbolString = referencedCb.getSymbol();
				descriptionString = referencedCb.getDescription();
			}
			
			fieldData.add(name, nameString);
			fieldData.add(symbol, symbolString);
			fieldData.add(description, descriptionString);
			
			if(referencedCb != null) {
				ImmutableArray<String> argsList = referencedCb.getArguments();
				String[] args = new String[argsList.size()];
				argsList.toArray(args);
				fieldData.add(parameterSelection, (Object[]) args);
			}
		}
		
		@Override
		public boolean checkFinish() {
			if(checkTextFieldError(getStage(), name, name.getText() == null, "Unfilled prompts", "Name must be defined")) return false;
			if(checkTextFieldError(getStage(), name, !name.getText().matches(GateModel.NAME_REGEX), "Inproper name scheme", GateModel.IMPROPER_NAME_SCHEME_MSG)) return false;
			boolean isPreset = false;
			try {
				PresetGateType.checkName(name.getText());
			} catch (NameTakenException e) {
				isPreset = true;
			}
			if(checkTextFieldError(getStage(), name, isPreset, "Cannot used the specified name", "The name chosen is exclusive to a Preset Gate")) return false;
			

			if(checkTextFieldError(getStage(), symbol, symbol.getText() == null, "Unfilled prompts", "Symbol must be defined")) return false;
			if(checkTextFieldError(getStage(), symbol, !symbol.getText().matches(GateModel.SYMBOL_REGEX), "Inproper symbol scheme", GateModel.IMPROPER_SYMBOL_SCHEME_MSG)) return false;
			
			HashSet<String> paramSet = new HashSet<>();
			Iterator<Node> nodeIterator = parameterSelection.nodeIterator();
			String[] params = new String[parameterSelection.size()];
			int i = 0;
			for(Object[] nodeArgs : parameterSelection) {
				TextField tf = (TextField) nodeIterator.next();
				String param = (String) nodeArgs[0];
				
				if(checkTextFieldError(getStage(), tf, param.equals(""), "Unfilled prompts", "Parameter name can not be blank")) return false;
				if(checkTextFieldError(getStage(), tf, !param.matches(GateModel.PARAMETER_REGEX), "Inproper parameter scheme", GateModel.IMPROPER_PARAMETER_SCHEME_MSG)) return false;
				if(checkTextFieldError(getStage(), tf, paramSet.contains(param), "Duplicate Parameters", "There are more than one parameter with the same name")) return false;
				
				paramSet.add(param);
				params[i++] = param;
			}
			
			if(referencedCb == null)
				newCb = new CircuitBoardModel(name.getText(), symbol.getText(), description.getText(), 5, 5, params);
			else
				newCb = referencedCb.createDeepCopyToNewName(name.getText(), symbol.getText(), description.getText(), params);
			
			return addCircuitBoardToProject(getStage(), referencedCb, newCb, editAsNew);
		}

		@Override
		public CircuitBoardModel getFinish() {
			return newCb;
		}
		
		@FXML private void addParameter(ActionEvent ae) {
			parameterSelection.addElementToEnd("");
		}
	}
	
	
	private static boolean addCircuitBoardToProject(Stage stage, GateModel referencedCb, GateModel newCb, boolean editAsNew) {
		Project p = AppStatus.get().getFocusedProject();
		ProjectHashtable pht = p.getCircuitBoardModels();
		
		if (!editAsNew && referencedCb != null) {
			ButtonType buttonType = AppAlerts.showMessage(stage, "Apply changes to " + referencedCb.getName(), 
					"Are you sure you want to change circuit board model \"" + referencedCb.getName() + "\"? "
							+ "All instances of the previous circuit board model in this project will be changed to the new implementation.", AlertType.WARNING);
			if(buttonType == ButtonType.CANCEL)
				return false;
			if(!newCb.getName().equals(referencedCb.getName()) && p.containsGateModel(newCb.getFormalName())) {
				buttonType = AppAlerts.showMessage(stage, "Override circuit board model " + newCb.getName(), 
						"A circuit board model with the name \"" + newCb.getName() + "\" already exists, "
								+ "do you want to override this circuit board model?"
								+ " All instances of the previous implementation of \""
								+ newCb.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(buttonType == ButtonType.CANCEL)
					return false;
			}
			
			if(buttonType == ButtonType.CANCEL)
				return false;
			
			pht.replace(referencedCb.getFormalName(), newCb);
		} else {
			if(pht.containsGateModel(newCb.getFormalName())) {
				ButtonType buttonType = AppAlerts.showMessage(stage, "Override circuit board model " + newCb.getName(), 
						"A circuit board model with the name \"" + newCb.getName() + "\" already exists, "
								+ "do you want to override this circuit board model?"
								+ " All instances of the previous implementation of \""
								+ newCb.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(buttonType == ButtonType.CANCEL)
					return false;
			}
			
			pht.put(newCb);
		}
		
		AppCommand.doAction(AppCommand.OPEN_GATE, newCb.getFormalName());
		
		return true;
	}
	
}
