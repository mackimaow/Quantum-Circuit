package appFX.framework.gateModels;

import appFX.appUI.utils.AppAlerts;
import appFX.framework.gateModels.GateModel.NameTakenException;
import appFX.framework.gateModels.QuantumGateDefinition.QuantumGateType;
import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", 
			new ClassicalGateDefinition("b[0] = b[0]"), 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[1, 0; "
			+ "0, 1] ")),
	
	
	
	HADAMARD ("Hadamard", "H", 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			"1/sqrt(2) * [1,  1; "
			+ 			" 1, -1] ")),
	
	
	
	PAULI_X ("Pauli_x", "X",  
			new ClassicalGateDefinition("b[0] = ~b[0]"), 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			  "[0, 1; "
			+ " 1, 0] ")),

			
			
	PAULI_Y ("Pauli_y", "Y", 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			" [0, -i; "
			+ "i,  0] ")),
	
	
	
	PAULI_Z ("Pauli_z", "Z", 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			  "[1,  0; "
			+ " 0, -1] ")),

	
	
	PHASE ("Phase", "S", 
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			  "[1, 0; "
			+ " 0, i] ")),
	
	
	
	PI_ON_8 ("Pi_over_8", "T",
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			  "[1, 0; "
			+ " 0, (1+i) / sqrt(2)] ")),
	
	
	
	SWAP ("Swap", "Swap", 
			new ClassicalGateDefinition("b[0] = b[1]", "b[1] = b[0]"),
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[1, 0, 0, 0; "
			+ "0, 0, 1, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1] ")),
	
	
	
	CNOT ("Cnot", "Cnot", 
			new ClassicalGateDefinition("b[1] = b[0] ^ b[1]"),
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[1, 0, 0, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1; "
			+ "0, 0, 1, 0] ")),
	
	
	
	TOFFOLI ("Toffoli", "Toffoli", 
			new ClassicalGateDefinition("b[2] = (b[0] & b[1]) ^ b[2]"),
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[1, 0, 0, 0, 0, 0, 0, 0; "
			+ "0, 1, 0, 0, 0, 0, 0, 0; "
			+ "0, 0, 1, 0, 0, 0, 0, 0; "
			+ "0, 0, 0, 1, 0, 0, 0, 0; "
			+ "0, 0, 0, 0, 1, 0, 0, 0; "
			+ "0, 0, 0, 0, 0, 1, 0, 0; "
			+ "0, 0, 0, 0, 0, 0, 0, 1; "
			+ "0, 0, 0, 0, 0, 0, 1, 0] ")),
	
	
	
	PHASE_SHIFT ("Phase_Shift", "R", new String[]{"\\theta"} ,
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[1, 0; "
			+ "0, exp(i * \\theta)] ")),
	
	ROTATE_X ("Rotate_X", "Rx", new String[]{"\\theta"} ,
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[cos(\\theta / 2), - i * sin(\\theta / 2); "
			+ "- i * sin(\\theta / 2), cos(\\theta / 2)] ")),
	
	ROTATE_Y ("Rotate_Y", "Ry", new String[]{"\\theta"} ,
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[cos(\\theta / 2), - sin(\\theta / 2); "
			+ "sin(\\theta / 2), cos(\\theta / 2)] ")),
	
	ROTATE_Z ("Rotate_Z", "Rz", new String[]{"\\theta"} ,
			new QuantumGateDefinition(QuantumGateType.UNIVERSAL,
			 "[exp(-i * \\theta / 2), 0; "
			+ "0, exp( i * \\theta / 2)] ")),
	
	
	MEASUREMENT ("Measurement", "M",
			new QuantumGateDefinition(QuantumGateType.KRAUS_OPERATORS,
			 "[1, 0; "
			+ "0, 0] ",
			
			  "[0, 0; "
			+ " 0, 1] ")),
	
	
	CLASSICAL_SET_0 ("Set_0", "0", 
			new ClassicalGateDefinition("b[0] = 0")),
	
	
	CLASSICAL_SET_1 ("Set_1", "1", 
			new ClassicalGateDefinition("b[0] = 1")), 
	
	
	
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
	
	private PresetGateType(String name, String symbol, QuantumGateDefinition quantumGateDefinition) {
		this(name, symbol, new String[0], null, quantumGateDefinition);
	}
	
	private PresetGateType(String name, String symbol, ClassicalGateDefinition classicalGateDefinition) {
		this(name, symbol, new String[0], classicalGateDefinition, null);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, ClassicalGateDefinition classicalGateDefinition) {
		this(name, symbol, parameters, classicalGateDefinition, null);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, QuantumGateDefinition quantumGateDefinition) {
		this(name, symbol, parameters, null, quantumGateDefinition);
	}
	
	private PresetGateType(String name, String symbol, ClassicalGateDefinition classicalGateDefinition, QuantumGateDefinition quantumGateDefinition) {
		this(name, symbol, new String[0], classicalGateDefinition, quantumGateDefinition);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, ClassicalGateDefinition classicalGateDefinition, QuantumGateDefinition quantumGateDefinition) {
		this(name, symbol, "", parameters, classicalGateDefinition, quantumGateDefinition);
	}
	
	private PresetGateType(String name, String symbol, String description, String[] parameters, ClassicalGateDefinition classicalGateDefinition, QuantumGateDefinition quantumGateDefinition) {
		BasicGateModel gm = null;
		try {
			gm = new PresetGateModel(name, symbol, description,  parameters, this, classicalGateDefinition, quantumGateDefinition);
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
		
		PresetGateModel(String name, String symbol, String description, String[] parameters, PresetGateType presetModel, ClassicalGateDefinition classicalGateDefinition, QuantumGateDefinition quantumGateDefinition) 
				throws DefinitionEvaluatorException {
			super("PresetGates*/" + name + "." + BasicGateModel.GATE_MODEL_EXTENSION, name, symbol, description, parameters, classicalGateDefinition, quantumGateDefinition);
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
