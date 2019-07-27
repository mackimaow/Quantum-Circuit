package appFX.appUI.wizards;

import java.util.HashSet;
import java.util.Iterator;

import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.LatexNode;
import appFX.appUI.utils.RearrangableListPaneWrapper;
import appFX.appUI.utils.RearrangableMatrixDefinitionPaneWrapper;
import appFX.appUI.utils.RearrangableParameterListPaneWrapper;
import appFX.appUI.utils.SequencePaneElement;
import appFX.appUI.utils.SequencePaneElement.SequencePaneFinish;
import appFX.appUI.utils.SequencePaneElement.SequencePaneNext;
import appFX.appUI.utils.SequencePaneElement.SequencePanePrevious;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.Project;
import appFX.framework.Project.ProjectHashtable;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.BasicGateModel.BasicGateModelType;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.NameTakenException;
import appFX.framework.gateModels.PresetGateType;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.customCollections.immutableLists.ImmutableArray;

public class BasicGateModelEditWizard extends Wizard<BasicGateModel> {
	
	private BasicGateModel referenceGm;
	private BasicGateModel newGm;
	private boolean editAsNewModel;
	private FirstSequenceElement first;
	
	
	public static BasicGateModel createNewGate() {
		return openGateEditableView(null, true);
	}
	
	public static BasicGateModel createNewGate(String name) {
		Wizard<BasicGateModel> wizard = new BasicGateModelEditWizard();
		return wizard.openWizardAndGetElement(name);
	}
	
	
	public static BasicGateModel editGate(String name) {
		return openGateEditableView(name, false);
	}
	
	public static BasicGateModel editAsNewGate(String name) {
		return openGateEditableView(name, true);
	}
	
	
	
	private static BasicGateModel openGateEditableView(String name, boolean editAsNewModel) {
		Project p = AppStatus.get().getFocusedProject();
		BasicGateModel gm = name == null ? null : (BasicGateModel) p.getGateModel(name);

		Wizard<BasicGateModel> wizard = new BasicGateModelEditWizard(gm, editAsNewModel);
		
		if(gm == null)
			wizard.setTitle("Create new Gate");
		else if(editAsNewModel)
			wizard.setTitle("Coping " + gm.getName() + " to new Gate");
		else
			wizard.setTitle("Changing " + gm.getName() + " Gate");
		
		return wizard.openWizardAndGetElement();
	}
	
	
	private BasicGateModelEditWizard () {
		this(null, true);
	}
	
	private BasicGateModelEditWizard (boolean editAsNewModel) {
		this(null, editAsNewModel);
	}
	
	private BasicGateModelEditWizard(BasicGateModel gm, boolean editAsNewModel) {
		super(650, 700);
		this.referenceGm = gm;
		this.editAsNewModel = editAsNewModel;
		this.first = new FirstSequenceElement();
	}

	@Override
	protected boolean onWizardCloseRequest() {
		return true;
	}
	
	@Override
	protected SequencePaneElement getFirstSeqPaneElem() {
		return first;
	}
	
	private class FirstSequenceElement extends SequencePaneElement implements SequencePaneNext {
		
		@FXML TextField name, symbol;
		@FXML TextArea description;
		@FXML ComboBox<BasicGateModelType> modelType;
		@FXML VBox parameters;
		RearrangableListPaneWrapper parameterSelection;
		String[] params;
		
		SingleBodyElementView universalElement;
		SingleBodyElementView hamiltonianElement;
		MultiBodyElementView povmElement;
		MultiBodyElementView krausElement;
		
		FirstSequenceElement() {
			super("wizards/gateEditableWizard/GateEditableHeadView.fxml");
			universalElement = new SingleBodyElementView("\\text{The model is specified by a universal gate } \\( U \\) \\text{ described by the matrix:} ", " $$ U = $$ ");
			hamiltonianElement = new SingleBodyElementView("\\text{The model is specified by a universal gate } \\( U \\) \\text{ described by the matrix:} ", " $$ H = $$ ");
			povmElement = new MultiBodyElementView("\\text{The model is specified by a set of Hermitian positive semidefinite operators } \\( (F_1, F_2, F_3, ... F_i) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } F_i  = I \\) \\text{ : }", "$$ F_{%i} = $$", "Add POVM Operator");
			krausElement = new MultiBodyElementView("\\text{The model is specified by a set of kraus matricies } \\( (K_1, K_2, K_3, ... K_n) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } K_i K_i ^ * = I \\) \\text{ : }", "$$ K_{%i} = $$", "Add Kraus Operator");
		}
		
		@Override
		public void initialize() {
			ObservableList<BasicGateModelType> items = modelType.getItems();
			for(BasicGateModelType gmt : BasicGateModelType.values())
				items.add(gmt);
			parameterSelection = new RearrangableParameterListPaneWrapper(parameters);
		}
		
		@Override
		public void setStartingFieldData(PaneFieldDataList controlData) {
			String nameString = "";
			String symbolString = "";
			String descriptionString = "";
			BasicGateModelType modelTypeValue = BasicGateModelType.UNIVERSAL;
			
			if(referenceGm != null) {
				nameString = referenceGm.getName();
				symbolString = referenceGm.getSymbol();
				descriptionString = referenceGm.getDescription();
				modelTypeValue = referenceGm.getGateModelType();
			}
			
			controlData.add(name, nameString);
			controlData.add(symbol, symbolString);
			controlData.add(description, descriptionString);
			controlData.add(modelType, modelTypeValue);
			
			if(referenceGm != null) {
				ImmutableArray<String> argsList = referenceGm.getArguments();
				String[] args = new String[argsList.size()];
				argsList.toArray(args);
				controlData.add(parameterSelection, (Object[]) args);
			}
		}
		
		@Override
		public Object[] giveNext() {
			return null;
		}

		@Override
		public boolean checkNext() {
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
			params = new String[parameterSelection.size()];
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
			
			return true;
		}

		@Override
		public SequencePaneElement getNext() {
			switch(modelType.getSelectionModel().getSelectedItem()) {
			case HAMILTONIAN:
				return hamiltonianElement;
			case KRAUS_OPERATORS:
				return krausElement;
			case POVM:
				return povmElement;
			case UNIVERSAL:
				return universalElement;
			default:
				throw new RuntimeException("Not implemented yet");
			}
		}
		
		@FXML private void addParameter(ActionEvent ae) {
			addParameter("");
		}
		
		private void addParameter(String name) {
			parameterSelection.addElementToEnd(name);
		}
		
	}
	
	private class MultiBodyElementView extends SequencePaneElement implements SequencePanePrevious, SequencePaneFinish<GateModel> {
		RearrangableListPaneWrapper multiListSelection;
		@FXML BorderPane borderPane;
		@FXML Button button;
		@FXML VBox vbox;
		final String labelText, elementLabelFormatString, buttonText;
		String[] definitions;
		
		
		public MultiBodyElementView(String labelText, String elementTextFormatString, String buttonText) {
			super("wizards/gateEditableWizard/GateEditableMultiMatView.fxml");
			this.labelText = labelText;
			this.elementLabelFormatString = elementTextFormatString;
			this.buttonText = buttonText;
		}
		
		@FXML private void buttonPress(ActionEvent ae) {
			multiListSelection.addElementToEnd("");
		}
		
		@Override
		public void setStartingFieldData(PaneFieldDataList fieldDataList) {
			String[] elements;
			if(referenceGm != null) {
				ImmutableArray<String> bodyDefinition = referenceGm.getUserInputMatrixDefinitions();
				elements = new String[bodyDefinition.size()];
				bodyDefinition.toArray(elements);
			} else {
				elements = new String[]{""};
			}
			fieldDataList.add(multiListSelection, elements);
		}
		
		@Override
		public void initialize() {
			LatexNode ln = new LatexNode(labelText, 13f);
			BorderPane pb = new BorderPane();
			pb.setPadding(new Insets(20, 10, 10, 10));
			pb.setLeft(ln);
			borderPane.setTop(pb);
			multiListSelection = new RearrangableMatrixDefinitionPaneWrapper(vbox, elementLabelFormatString);
			button.setText(buttonText);
		}
		
		@Override
		public boolean checkPrevious() {
			return true;
		}

		@Override
		public boolean checkFinish() {
			if(checkPaneError(getStage(), vbox, multiListSelection.size() == 0, "Unfilled prompts", "At least one Matrix must be defined")) return false;
			
			definitions = new String[multiListSelection.size()];
			Iterator<Node> nodeIterator = multiListSelection.nodeIterator();
			int i = 0;
			for(Object[] nodeArgs : multiListSelection) {
				HBox hbox = (HBox) nodeIterator.next();
				TextField tf = (TextField) hbox.getChildren().get(1);
				String definition = (String) nodeArgs[0];
				if(checkTextFieldError(getStage(), tf, definition.equals(""), "Unfilled prompts", "definition can not be blank")) return false;
				definitions[i++] = definition;
			}
			
			DefinitionEvaluatorException exception = null;
			try {
				newGm = new BasicGateModel(first.name.getText(), first.symbol.getText(), first.description.getText(), first.params, first.modelType.getValue(), definitions);
			} catch (DefinitionEvaluatorException e) {
				exception = e;
			}
			
			TextField tf = null;
			String message = "";
			if(exception != null) {
				HBox hbox = (HBox) multiListSelection.getElement(exception.getDefinitionNumber());
				tf = (TextField) hbox.getChildren().get(1);
				message = exception.getMessage();
			}
			if(checkTextFieldError(getStage(), tf, exception != null, "Definition error", message)) return false;
			
			return addGateModelToProject(getStage(), referenceGm, newGm, editAsNewModel);
		}

		@Override
		public GateModel getFinish() {
			return newGm;
		}
	}
	
	private class SingleBodyElementView extends SequencePaneElement implements SequencePanePrevious, SequencePaneFinish<GateModel> {
		@FXML BorderPane borderPane;
		@FXML HBox hbox;
		@FXML TextField textField;
		final String labelText, elementLabelText;

		public SingleBodyElementView(String labelText, String elementLabelText) {
			super("wizards/gateEditableWizard/GateEditableSingleMatView.fxml");
			this.labelText = labelText;
			this.elementLabelText = elementLabelText;
		}
		
		@Override
		public void setStartingFieldData(PaneFieldDataList fieldData) {
			String userDefinition = "";
			if(referenceGm != null) 
				userDefinition = referenceGm.getUserInputMatrixDefinitions().get(0);
			fieldData.add(textField, userDefinition);
		}
		
		@Override
		public void initialize() {
			LatexNode ln = new LatexNode(labelText, 13f);
			BorderPane pb = new BorderPane();
			pb.setPadding(new Insets(20, 10, 10, 10));
			pb.setLeft(ln);
			borderPane.setTop(pb);
			hbox.getChildren().add(0, new LatexNode(elementLabelText));
		}
		
		@Override
		public boolean checkPrevious() {
			return true;
		}

		@Override
		public boolean checkFinish() {
			String definition = textField.getText();
			if(checkTextFieldError(getStage(), textField, definition.equals(""), "Unfilled prompts", "definition can not be blank")) return false;
			
			DefinitionEvaluatorException exception = null;
			String message = "";
			try {
				newGm = new BasicGateModel(first.name.getText(), first.symbol.getText(), first.description.getText(), first.params, first.modelType.getValue(), definition);
			} catch (DefinitionEvaluatorException e) {
				exception = e;
				message = exception.getMessage();
			}
			if(checkTextFieldError(getStage(), textField, exception != null, "Definition error", message)) return false;
			return true;
		}

		@Override
		public GateModel getFinish() {
			addGateModelToProject(getStage(), referenceGm, newGm, editAsNewModel);
			return newGm;
		}
	}
	
	
	private static boolean addGateModelToProject(Stage stage, GateModel referencedGm, GateModel newGm, boolean editAsNew) {
		Project p = AppStatus.get().getFocusedProject();
		ProjectHashtable pht = p.getCustomGates();
		
		if (!editAsNew && referencedGm != null) {
			ButtonType buttonType = AppAlerts.showMessage(stage, "Apply changes to " + referencedGm.getName(), 
					"Are you sure you want to change gate model \"" + referencedGm.getName() + "\"? "
							+ "All instances of the previous gate model in this project will be changed to the new implementation.", AlertType.WARNING);
			if(buttonType == ButtonType.CANCEL)
				return false;
			if(!newGm.getName().equals(referencedGm.getName()) && p.containsGateModel(newGm.getFormalName())) {
				buttonType = AppAlerts.showMessage(stage, "Override Gate Model " + newGm.getName(), 
						"A Gate Model with the name \"" + newGm.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ newGm.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(buttonType == ButtonType.CANCEL)
					return false;
			}
			if(buttonType == ButtonType.CANCEL)
				return false;
			
			pht.replace(referencedGm.getFormalName(), newGm);
		} else {
			if(pht.containsGateModel(newGm.getFormalName())) {
				ButtonType buttonType = AppAlerts.showMessage(stage, "Override Gate Model " + newGm.getName(), 
						"A Gate Model with the name \"" + newGm.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ newGm.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(buttonType == ButtonType.CANCEL)
					return false;
			}
			
			pht.put(newGm);
		}
		
		AppCommand.doAction(AppCommand.OPEN_GATE, newGm.getFormalName());
		
		return true;
	}
	
}
