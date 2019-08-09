package appFX.appUI.appViews;

import appFX.appUI.utils.GateIcon;
import appFX.appUI.utils.LatexNode;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.ClassicalGateDefinition;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.GateComputingType;
import appFX.framework.gateModels.QuantumGateDefinition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.customCollections.immutableLists.ImmutableArray;

public class BasicGateModelView extends AppView {
	
	@FXML private TextField fileLocation, name, symbol, registers, computingType;
	@FXML private ScrollPane description;
	@FXML private BorderPane iconSpace;
	@FXML private HBox parameters, editBar;
	@FXML private Button editButton, editNewButton;
	
	//quantum stuff
	@FXML private Separator quantumSeparator;
	@FXML private Label quantumDefinitionLabel, quantumModelTypeLabel;
	@FXML private TextField modelType;
	@FXML private HBox definitionStatement;
	@FXML private VBox definition;
	
	//classical stuff
	@FXML private Separator classicalSeparator;
	@FXML private Label classicalDefinitionLabel;
	@FXML private HBox classicalDefinitionStatement;
	@FXML private VBox classicalDefinition;
	
	
	private boolean initialized = false;
	
	private BasicGateModel gm;
	
	public BasicGateModelView(BasicGateModel gm) {
		super("views/BasicGateModelView.fxml", gm.getLocationString(), ViewLayout.CENTER);
		this.gm = gm;
		initialize();
	}

	public void onButtonPress(ActionEvent e) {
		AppCommand.doAction(AppCommand.EDIT_GATE, gm.getLocationString());
	}
	
	public void onButton2Press(ActionEvent e) {
		AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, gm.getLocationString());
	}
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		Project p = AppStatus.get().getFocusedProject();
		if(initialized && p.getCustomGates() == source) {

			if(methodName.equals("put")) {
				if(((GateModel)args[0]).getLocationString().equals(getName()))
					closeView();
			} else if (methodName.equals("replace") || methodName.equals("remove")){
				if(args[0].equals(getName()))
					closeView();
			}
		}
	}

	
	private void initialize() {
		initialized = true;
		updateDefinitionUI();
	}
	
	private void updateDefinitionUI() {
		fileLocation.setEditable(false);
		name.setEditable(false);
		symbol.setEditable(false);
		registers.setEditable(false);
		computingType.setEditable(false);
		
		modelType.setEditable(false);
		
		
		fileLocation.setText(gm.getLocationString());
		name.setText(gm.getName());
		symbol.setText(gm.getSymbol());
		computingType.setText(gm.getComputingType().toString());
		
		Node n = new LatexNode(gm.getDescription(), 20);
		
		description.setContent(n);
		
		parameters.getChildren().clear();
		
		ImmutableArray<String> args = gm.getParameters();
		
		if(!args.isEmpty()) {
			String parametersLatex = "\\(" + args.get(0) + "\\)";
			for(int i = 1; i < args.size(); i++)
				parametersLatex += ", \\(" + args.get(i) + "\\)";
			
			parameters.getChildren().add(new LatexNode(parametersLatex, 20));
		}
		
		registers.setText(Integer.toString(gm.getNumberOfRegisters()));
		
		iconSpace.getChildren().clear();
		
		GateIcon gi = GateIcon.getGateIcon(gm);
		
		iconSpace.setCenter(gi.getView());
		
		GateComputingType type = gm.getComputingType();
		if(type.isQuantum()) {
			QuantumGateDefinition gateDefinition = gm.getQuantumGateDefinition();
			modelType.setText(gateDefinition.getQuantumGateType().toString());
			
			definitionStatement.getChildren().clear();
			definition.getChildren().clear();
			
			switch (gateDefinition.getQuantumGateType()) {
			case HAMILTONIAN:
				String latex = "\\text{The model is specified by matrix designated by } \\( e ^ {Ht}\\) \\text{ where } \\( H \\) \\text{ is defined by:} ";
				definitionStatement.getChildren().add(new LatexNode(latex, 15));
				
				definition.getChildren().add(new LatexNode("$$ H = " + gateDefinition.getLatex().get(0) + " $$", 20));
				
				break;
			case POVM:
				latex = "\\text{The model is specified by a set of Hermitian positive semidefinite operators } \\( (F_1, F_2, F_3, ... F_i) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } F_i  = I \\) \\text{ : }";
				definitionStatement.getChildren().add(new LatexNode(latex, 15));
				
				ImmutableArray<String> povmLatex = gateDefinition.getLatex();
				
				latex = "\\begin{eqnarray}";
				
				int i = 1;
				for(String l : povmLatex)
					latex += "F_{" + (i++) + "} = " + l + " \\\\";
				
				latex += "\\end{eqnarray}";
				
				definition.getChildren().add(new LatexNode(latex, 20));
				
				break;
			case UNIVERSAL:
				latex = "\\text{The model is specified by a universal gate } \\( U \\) \\text{ described by the matrix:}";
				definitionStatement.getChildren().add(new LatexNode(latex, 15));
				
				definition.getChildren().add(new LatexNode(" $$ U = " + gateDefinition.getLatex().get(0) + " $$ ", 20));
				
				break;
			case KRAUS_OPERATORS:
				latex = "\\text{The model is specified by a set of kraus matricies } \\( (K_1, K_2, K_3, ... K_n) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } K_i K_i ^ * = I \\) \\text{ : }";
				definitionStatement.getChildren().add(new LatexNode(latex, 15));
				
				ImmutableArray<String> krausLatex = gateDefinition.getLatex();
				
				latex = "\\begin{eqnarray}";
				
				i = 1;
				for(String l : krausLatex)
					latex += "K_{" + (i++) + "} = " + l + " \\\\";
				
				latex += "\\end{eqnarray}";
				
				definition.getChildren().add(new LatexNode(latex, 20));
				break;
			default:
				break;
			}
		} else {
			quantumDefinitionLabel.setVisible(false);
			quantumModelTypeLabel.setVisible(false);
			modelType.setVisible(false);
			definitionStatement.setVisible(false);
			definition.setVisible(false);
			quantumSeparator.setVisible(false);
			
			quantumDefinitionLabel.setManaged(false);
			quantumModelTypeLabel.setManaged(false);
			modelType.setManaged(false);
			definitionStatement.setManaged(false);
			definition.setManaged(false);
			quantumSeparator.setManaged(false);
		}
		
		if(type.isClassical()) {
			ClassicalGateDefinition classicalDef = gm.getClassicalGateDefinition();
			
			String latex = "\\text{The model is specified by a set of boolean equations in the form of \"b[outputRegister] = b[inputRegister1] * b[inputRegister2] + ...\" } ";
			classicalDefinitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			ImmutableArray<String> definitionLatex = classicalDef.getLatex();
			
			latex = "\\begin{eqnarray}";
			for(String l : definitionLatex)
				latex += l + " \\\\";
			latex += "\\end{eqnarray}";
			
			classicalDefinition.getChildren().add(new LatexNode(latex, 20));
		} else {
			classicalSeparator.setVisible(false);
			classicalDefinitionLabel.setVisible(false);
			classicalDefinitionStatement.setVisible(false);
			classicalDefinition.setVisible(false);
			
			classicalSeparator.setManaged(false);
			classicalDefinitionLabel.setManaged(false);
			classicalDefinitionStatement.setManaged(false);
			classicalDefinition.setManaged(false);
		}
		
		
		
		editButton.setDisable(gm.isPreset());
		editButton.setVisible(!gm.isPreset());
		editButton.setManaged(!gm.isPreset());
	}

}
