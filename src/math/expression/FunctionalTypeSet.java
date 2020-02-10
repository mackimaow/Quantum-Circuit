package math.expression;

import utils.customCollections.immutableLists.ArrayListWrapper;
import utils.customCollections.immutableLists.ImmutableList;

public class FunctionalTypeSet {
	public final MathValueTypeSet outputTypeSet;
	private final MathValueTypeSet[] parameterTypeSets;
	
	public FunctionalTypeSet(MathValueTypeSet outputTypeSet, MathValueTypeSet ... parameterTypeSets) {
		this.outputTypeSet = outputTypeSet;
		this.parameterTypeSets = parameterTypeSets;
	}
	
	public ImmutableList<MathValueTypeSet> getParameterTypeSets() {
		return new ImmutableList<>(new ArrayListWrapper<>(parameterTypeSets));
	}
	
	public MathValueTypeSet getOutputTypeSet() {
		return outputTypeSet;
	}
}
