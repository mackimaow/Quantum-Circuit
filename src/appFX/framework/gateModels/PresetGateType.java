package appFX.framework.gateModels;

import appFX.appUI.utils.AppAlerts;
import appFX.framework.gateModels.BasicGateModel.BasicGateModelType;
import appFX.framework.gateModels.GateModel.GateComputingType;
import appFX.framework.gateModels.GateModel.NameTakenException;
import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", GateComputingType.CLASSICAL_OR_QUANTUM, BasicGateModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, 1] "),
	
	HADAMARD ("Hadamard", "H", BasicGateModelType.UNIVERSAL ,
			"1/sqrt(2) * [1,  1; "
			+ 			" 1, -1] "),
	
	PAULI_X ("Pauli_x", "X", GateComputingType.CLASSICAL_OR_QUANTUM, BasicGateModelType.UNIVERSAL ,
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
	
	SWAP ("Swap", "Swap", GateComputingType.CLASSICAL_OR_QUANTUM, BasicGateModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 0, 1, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1] "),
	
	CNOT ("Cnot", "Cnot", GateComputingType.CLASSICAL_OR_QUANTUM, BasicGateModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1; "
			+ "0, 0, 1, 0] "),
	
	TOFFOLI ("Toffoli", "Toffoli", GateComputingType.CLASSICAL_OR_QUANTUM, BasicGateModelType.UNIVERSAL ,
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
		return PresetGateType.IDENTITY.getModel().getLocationString().equals(name);
	}
	
	
	public static void checkLocationString(String location) {
		for(PresetGateType pgt : values())
			if(pgt.getModel().getLocationString().equals(location)) 
				throw new NameTakenException("The file location \"" + location + "\" is already a preset gate name and cannot be used");
	}
	
	
	public static PresetGateType getPresetTypeByLocation (String location) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getLocationString().equals(location))
				return pgt;
		return null;
	}
	
	public static boolean containsPresetTypeByLocation (String location) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getLocationString().equals(location))
				return true;
		return false;
	}
	
	
	private final BasicGateModel gateModel;
	
	private PresetGateType(String name, String symbol, BasicGateModelType type, String ... expression) {
		this(name, symbol, GateComputingType.QUANTUM, type, expression);
	}
	
	private PresetGateType(String name, String symbol, GateComputingType computingType, BasicGateModelType type, String ... expression) {
		this(name, symbol, "", computingType, type, expression);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, BasicGateModelType type, String ... expression) {
		this(name, symbol, GateComputingType.QUANTUM, parameters, type, expression);
	}
	
	private PresetGateType(String name, String symbol, GateComputingType computingType, String[] parameters, BasicGateModelType type, String ... expression) {
		this(name, symbol, "", computingType, parameters, type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, BasicGateModelType type, String ... expression) {
		this(name, symbol, description, GateComputingType.QUANTUM, type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, GateComputingType computingType, BasicGateModelType type, String ... expression) {
		this(name, symbol, description, computingType, new String[0], type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, String[] parameters, BasicGateModelType type, String ... expression) {
		this(name, symbol, description, GateComputingType.QUANTUM, parameters, type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, GateComputingType computingType, String[] parameters, BasicGateModelType type, String ... expression) {
		BasicGateModel gm = null;
		try {
			gm = new PresetGateModel(name, symbol, description, computingType, parameters, type, this, expression);
		} catch (Exception e) {
			AppAlerts.showJavaExceptionMessage(null, "Program Crashed", "Could not make preset " + name + " gate model", e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			this.gateModel = gm;
		}
	}
	
	public BasicGateModel getModel() {
		return gateModel;
	}
	
	
	
	public static class PresetGateModel extends BasicGateModel {
		private static final long serialVersionUID = 3655123001545022473L;
		
		private final PresetGateType presetModel;
		
		PresetGateModel(String name, String symbol, String description, GateComputingType computingType, String[] parameters, BasicGateModelType gateType, PresetGateType presetModel, String ... userDefinitions) 
				throws DefinitionEvaluatorException {
			super("PresetGates*/" + name + "." + BasicGateModel.GATE_MODEL_EXTENSION, name, symbol, description, computingType, parameters, gateType, userDefinitions);
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
