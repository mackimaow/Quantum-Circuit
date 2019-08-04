package appFX.framework.solderedGates;

import java.io.Serializable;

import appFX.framework.gateModels.PresetGateType;
import appFX.framework.utils.InputDefinitions.GroupDefinition;
import utils.customCollections.Manifest.ManifestElementHandle;



public class SolderedGate implements Serializable {
	private static final long serialVersionUID = 2595030500395644473L;
	
	@SuppressWarnings("rawtypes")
	private ManifestElementHandle locationHandle;
	private final GroupDefinition parameterSet;
	
	@SuppressWarnings("rawtypes")
	public SolderedGate(ManifestElementHandle locationHandle, GroupDefinition parameterSet) {
		this.locationHandle = locationHandle;
		this.parameterSet = parameterSet;
	}
	
	@SuppressWarnings("rawtypes")
	public SolderedGate(ManifestElementHandle locationHandle) {
		this.locationHandle = locationHandle;
		this.parameterSet = new GroupDefinition();
	}
	
	public String getGateModelLocationString() {
		return (String) locationHandle.getElement();
	}
	
	public GroupDefinition getParameterSet() {
		return parameterSet;
	}
	
	@SuppressWarnings("rawtypes")
	public ManifestElementHandle getManifestHandle() {
		return locationHandle;
	}
	
	@SuppressWarnings("rawtypes")
	public void setManifestHandle(ManifestElementHandle meh) {
		locationHandle = meh;
	}
	
	public boolean isIdentity() {
		return getGateModelLocationString().equals(PresetGateType.IDENTITY.getModel().getLocationString());
	}
	
}
