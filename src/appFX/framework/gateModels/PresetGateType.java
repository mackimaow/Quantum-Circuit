package appFX.framework.gateModels;

import appFX.appUI.utils.AppAlerts;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.gateModels.BasicGateModel.BasicGateModelType;
import appFX.framework.gateModels.GateModel.NameTakenException;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", BasicGateModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, 1] "),
	
	HADAMARD ("Hadamard", "H", BasicGateModelType.UNIVERSAL ,
			"1/sqrt(2) * [1,  1; "
			+ 			" 1, -1] "),
	
	PAULI_X ("Pauli_x", "X", BasicGateModelType.UNIVERSAL ,
			  "[0, 1; "
			+ " 1, 0] "),
	
	PAULI_Y ("Pauli_y", "Y", BasicGateModelType.UNIVERSAL ,
			" [0, -i; "
			+ "i,  0] "),
	
	PAULI_Z ("Pauli_z", "Z", BasicGateModelType.UNIVERSAL ,
			  "[1,  0; "
			+ " 0, -1] "),
	
	PHASE ("Phase", "S", BasicGateModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, i] "),
	
	PI_ON_8 ("Pi_over_8", "T", BasicGateModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, (1+i) / sqrt(2)] "),
	
	SWAP ("Swap", "Swap", BasicGateModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 0, 1, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1] "),
	
	CNOT ("Cnot", "Cnot", BasicGateModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1; "
			+ "0, 0, 1, 0] "),
	
	TOFFOLI ("Toffoli", "Toffoli", BasicGateModelType.UNIVERSAL ,
			 "[1, 0, 0, 0, 0, 0, 0, 0; "
			+ "0, 1, 0, 0, 0, 0, 0, 0; "
			+ "0, 0, 1, 0, 0, 0, 0, 0; "
			+ "0, 0, 0, 1, 0, 0, 0, 0; "
			+ "0, 0, 0, 0, 1, 0, 0, 0; "
			+ "0, 0, 0, 0, 0, 1, 0, 0; "
			+ "0, 0, 0, 0, 0, 0, 0, 1; "
			+ "0, 0, 0, 0, 0, 0, 1, 0] "),
	
	PHASE_SHIFT ("Phase_Shift", "R", new String[]{"\\theta"} , BasicGateModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, exp(i * \\theta)] "),
	
	
	MEASUREMENT ("Measurement", "M", BasicGateModelType.POVM , 
			 "[1, 0; "
			+ "0, 0] ",
			
			  "[0, 0; "
			+ " 0, 1] "), 
	
	
	
	
	;
	
	
	public static boolean isIdentity(String name) {
		return PresetGateType.IDENTITY.getModel().getFormalName().equals(name);
	}
	
	
	public static void checkName(String name) {
		for(PresetGateType pgt : values())
			if(pgt.getModel().getName().equals(name)) 
				throw new NameTakenException("The name \"" + name + "\" is already a preset gate name and cannot be used");
	}
	
	
	public static PresetGateType getPresetTypeByFormalName (String name) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getFormalName().equals(name))
				return pgt;
		return null;
	}
	
	public static boolean containsPresetTypeByFormalName (String name) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getFormalName().equals(name))
				return true;
		return false;
	}
	
	
	private final BasicGateModel gateModel;
	
	private PresetGateType(String name, String symbol, String description, BasicGateModelType type, String ... expression) {
		this(name, symbol, description, new String[0], type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, String[] parameters, BasicGateModelType type, String ... expression) {
		BasicGateModel gm = null;
		try {
			gm = new PresetGateModel(name, symbol, description, parameters, type, this, expression);
		} catch (Exception e) {
			AppAlerts.showJavaExceptionMessage(null, "Program Crashed", "Could not make preset " + name + " gate model", e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			this.gateModel = gm;
		}
	}
	
	private PresetGateType(String name, String symbol, BasicGateModelType type, String ... expression) {
		this(name, symbol, "", type, expression);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, BasicGateModelType type, String ... expression) {
		this(name, symbol, "", parameters, type, expression);
	}
	
	public BasicGateModel getModel() {
		return gateModel;
	}
	
	
	
	public static class PresetGateModel extends BasicGateModel {
		private static final long serialVersionUID = 3655123001545022473L;
		
		private final PresetGateType presetModel;
		
		PresetGateModel(String name, String symbol, String description, String[] parameters, BasicGateModelType gateType, PresetGateType presetModel, String ... userDefinitions) 
				throws DefinitionEvaluatorException {
			super(name, symbol, description, parameters, gateType, userDefinitions);
			this.presetModel = presetModel;
		}
		
		@Override
		public boolean isPreset() {
			return true;
		}
		
		public PresetGateType getPresetGateType() {
			return presetModel;
		}
	}
}
