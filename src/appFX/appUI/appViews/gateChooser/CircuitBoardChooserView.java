package appFX.appUI.appViews.gateChooser;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.GateModel;

public class CircuitBoardChooserView extends AbstractGateChooserView {
	
	public CircuitBoardChooserView() {
		super("Circuit Boards");
	}
	
	public void initializeGates() {
		Project p = AppStatus.get().getFocusedProject();
		if(p != null) {
			for(GateModel s : p.getCircuitBoardModels().getGateModelIterable())
				addGateModel(s);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		button.setVisible(true);
		button.setText("Create Circuit Board");
		initializeGates();
	}
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		AppStatus status = AppStatus.get();
		
		if(source == status && methodName == "setFocusedProject") {
			removeAllGateModels();
			Project p = (Project) args[0];
			for(GateModel s : p.getCircuitBoardModels().getGateModelIterable())
				addGateModel(s);
		}

		Project p = status.getFocusedProject();
		if(p != null && source == p.getCircuitBoardModels()) {
			if(methodName.equals("put")) {
				GateModel replacement = (GateModel) args[0];
				removeGateModelByLocationString(replacement.getLocationString());
				addGateModel(replacement);
			} else if(methodName.equals("replace") ) {
				String locationString = (String) args[0];
				GateModel replacement = (GateModel) args[1];
				removeGateModelByLocationString(locationString);
				
				String newLocationString = replacement.getLocationString();
				if(!newLocationString.equals(locationString))
					removeGateModelByLocationString(newLocationString);	
				addGateModel(replacement);
			} else if(methodName.equals("remove")) {
				String locationString = (String) args[0];
				removeGateModelByLocationString(locationString);
			}
		}
	}

	@Override
	public void buttonAction() {
		AppCommand.doAction(AppCommand.CREATE_CIRCUIT_BOARD);
	}

}
