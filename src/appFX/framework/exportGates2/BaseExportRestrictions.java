package appFX.framework.exportGates2;

import java.util.HashSet;

import appFX.framework.Project;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.GateModel;

public class BaseExportRestrictions {
	
	public static class CheckGateModelExists extends ExportRestriction {
		public CheckGateModelExists() {
			super("Gate Model Exists");
		}
		
		@Override
		protected ExportRestrictionData createExportRestrictionData() {
			return new ExportRestrictionData() {
				String fileLocation;
				
				@Override
				protected String getRestrictionExceptionMessage() {
					return "Gate model cannot be found at: " + fileLocation;
				}
				
				@Override
				protected boolean validateRawGateData(Project p, GateModel parent, GateModel child, RawExportableGateData gateData) {
					return child != null;
				}
			};
		}
		
	}
	
	
	public static class CheckInfiniteRecursion extends ExportRestriction {
		public CheckInfiniteRecursion() {
			super("Infinite Quantum Gate Recursion");
		}
		
		@Override
		protected ExportRestrictionData createExportRestrictionData() {
			
			return new CheckInfiniteRecursionData();
		}
		
		private class CheckInfiniteRecursionData extends ExportRestrictionData {
			private HashSet<String> circuitBoardFileNameManifest;
			
			
			public CheckInfiniteRecursionData() {
				circuitBoardFileNameManifest = new HashSet<>();
			}
			
			@Override
			protected String getRestrictionExceptionMessage() {
				
				return null;
			}
			
			@Override
			protected boolean validateRawGateData(Project p, GateModel parent, GateModel child, RawExportableGateData gateData) {
				return false;
			}
		}
	}
	
	
	
}
