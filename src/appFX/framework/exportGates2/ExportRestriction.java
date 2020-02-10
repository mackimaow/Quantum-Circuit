package appFX.framework.exportGates2;

import appFX.appUI.utils.AppAlerts;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.GateModel;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;


	
public abstract class ExportRestriction {
	private final String restrictionDescription;
	
	public ExportRestriction(String restrictionDescription) {
		this.restrictionDescription = restrictionDescription;
	}
	
	protected abstract ExportRestrictionData createExportRestrictionData();
	
	public String getDescription() {
		return restrictionDescription;
	}
	
	public abstract class ExportRestrictionData {
		
		protected abstract boolean validateRawGateData(Project p, GateModel parent, GateModel child, RawExportableGateData gateData);
		protected abstract String getRestrictionExceptionMessage();
		
		public void assertPassesRestriction(Project p, GateModel parent, GateModel child, RawExportableGateData gateData) throws ExportRestrictionViolationException {
			if(!validateRawGateData(p, parent, child, gateData))
				throw new ExportRestrictionViolationException(gateData.getSolderedGate().getGateModelLocationString(), gateData.getGateRowBodyStart(), gateData.getGateRowBodyEnd(), gateData.getColumn());
		}
		
		@SuppressWarnings("serial")
		public class ExportRestrictionViolationException extends Exception {
			public final String circuitBoardFileName;
			public final int rowGateBodyStart, rowGateBodyEnd, column;
			
			private ExportRestrictionViolationException(String circuitBoardFileName, int rowGateBodyStart, int rowGateBodyEnd, int column) {
				super(getRestrictionExceptionMessage());
				this.circuitBoardFileName = circuitBoardFileName;
				this.rowGateBodyStart = rowGateBodyStart; 
				this.rowGateBodyEnd = rowGateBodyEnd;
				this.column = column;
			}
			
			public void openErrorInGUI() {
				Window w = AppStatus.get().getPrimaryStage();
	        	AppAlerts.showMessage(w, "Export Failed", getMessage(), AlertType.ERROR);
	        	AppCommand.doAction(AppCommand.OPEN_CIRCUIT_BOARD_AND_SHOW_ERROR, circuitBoardFileName, rowGateBodyStart, rowGateBodyEnd, column);
			}
		}
	}
}

