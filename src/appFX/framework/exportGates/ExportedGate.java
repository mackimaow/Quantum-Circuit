package appFX.framework.exportGates;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.BasicGateModel.BasicGateModelType;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appFX.framework.utils.InputDefinitions.MatrixDefinition;
import mathLib.Complex;
import mathLib.Matrix;

public class ExportedGate {	
	private final GateModel gateModel;
	private final Hashtable<String, Complex> argParamMap;
	private final int[] gateRegisters;
	private final Control[] quantumControls;
	private final Control[] classicalControls;
	private final Matrix<Complex>[] matrixes;
	private final boolean isClassical;
	
	
	@SuppressWarnings("unchecked")
	public static ExportedGate mkIdentAt(int register, boolean isClassical) {
		BasicGateModel gm = PresetGateType.IDENTITY.getModel();
		Matrix<Complex> m = ((MatrixDefinition) gm.getDefinitions().get(0)).getMatrix();
		
		return new ExportedGate(gm, new Hashtable<>(), new int[] {register}, isClassical, new Control[0], new Control[0], new Matrix[] { m });
	}
	
	
	public ExportedGate(GateModel gateModel, Hashtable<String, Complex> argParamMap, int[] gateRegisters, boolean isClassical, Control[] classicalControls, Control[] quantumControls, Matrix<Complex>[] matrixes) {
		this.gateModel = gateModel;
		this.gateRegisters = gateRegisters;
		this.classicalControls = classicalControls;
		this.quantumControls = quantumControls;
		this.isClassical = isClassical;
		this.matrixes = matrixes;
		this.argParamMap = argParamMap;
	}
	
	public boolean isClassical() {
		return isClassical;
	}
	
	public boolean isQuantum() {
		return !isClassical;
	}
	
	public GateModel getGateModel() {
		return gateModel;
	}
	
	public Complex getParameter (String parameter) {
		return argParamMap.get(parameter);
	}
	
	public Set<String> getArgumentSet() {
		return argParamMap.keySet();
	}
	
	public Collection<Complex> getParameters() {
		return argParamMap.values();
	}
	
	public int[] getGateRegister() {
		return gateRegisters;
	}
	
	public boolean isPresetGate() {
		return gateModel.isPreset();
	}
	
	public PresetGateType getPresetGateType() {
		if(gateModel.isPreset()) 
			return  ((PresetGateModel) gateModel).getPresetGateType();
		else return null;
	}
	
	public BasicGateModelType getGateType() {
		if(gateModel instanceof BasicGateModel) {
			return ((BasicGateModel)gateModel).getGateModelType();
		}
		return null;
	}
	
	
	public Control[] getQuantumControls(){
		return quantumControls;
	}
	
	public Control[] getClassicalControls(){
		return classicalControls;
	}
	
	public Matrix<Complex>[] getInputMatrixes() {
		return matrixes;
	}
	
}
