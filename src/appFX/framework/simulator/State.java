package appFX.framework.simulator;

import appFX.framework.gateModels.GateModel;
import mathLib.expression.MathSet;
import utils.customMaps.IndexMap;

public interface State {
	public int apply(GateModel gm, MathSet mathSet, IndexMap map, Object ... args);
	public int size();
}
