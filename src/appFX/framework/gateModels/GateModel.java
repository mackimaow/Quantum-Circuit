package appFX.framework.gateModels;

import java.io.Serializable;

import utils.customCollections.immutableLists.ImmutableArray;

public abstract class GateModel implements Serializable {
	private static final long serialVersionUID = 3195910933230664750L;
	
	public static enum GateComputingType {
		CLASSICAL(true, false, "Classical"),
		QUANTUM(false, true, "Quantum"),
		CLASSICAL_AND_QUANTUM(true, true, "Classical and Quantum");
		
		final boolean isClassical, isQuantum;
		final String name;
		
		private GateComputingType (boolean isClassical, boolean isQuantum, String name) {
			this.isClassical = isClassical;
			this.isQuantum = isQuantum;
			this.name = name;
		}
		
		public boolean isQuantum() {
			return isQuantum;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public boolean isClassical() {
			return isClassical;
		}
	}
	
	public static final String PARAMETER_REGEX = "\\\\?[a-zA-Z][\\w]*";
	public static final String IMPROPER_PARAMETER_SCHEME_MSG = "Parameter name must be a letter followed "
			+ "by letters, digits, or underscores. For special mathematical symbols, the \"\\\" character"
			+ "can be used to escape the name to use the proper mathematical symbol";
	
	private final String location;
	private final String name;
	private final String symbol;
	private final String description;
	private final GateComputingType gateComputingType;
	private final String[] parameters;
	
	public GateModel (String location, String name, String symbol, String description, GateComputingType gateComputingType, String ... parameters) {
		checkLocationString(location, isPreset(), getExtString());
		
		this.location = location.trim();
		this.name = name.trim();
		this.symbol = symbol.trim();
		this.description = description.trim();
		this.gateComputingType = gateComputingType;
		this.parameters = parameters;
		
		int i = 0;
		for(String param : parameters) {
			if(param == null) {
				throw new ImproperNameSchemeException("Symbol must be defined");
			} else if(!symbol.matches(PARAMETER_REGEX)) {
				throw new ImproperNameSchemeException(IMPROPER_PARAMETER_SCHEME_MSG);
			}
			for(int j = 0; j < i; j++)
				if(param.equals(parameters[j]))
					throw new IllegalArgumentException("There are two parameters with the same name");
			i++;
		}
	}
	
	public static void checkLocationString(String location, boolean isPreset, String extString) {
		if(!isPreset)
			PresetGateType.checkLocationString(location);
		
		if(location == null) {
			throw new ImproperNameSchemeException("Location must be defined");
		} else if (!location.trim().endsWith("." + extString)) {
			throw new ImproperNameSchemeException("Location extension must be " + "." + extString);
		}
	}
	
	public abstract int getNumberOfRegisters();
	public abstract String getExtString();
	public abstract boolean isPreset();
	public abstract GateModel shallowCopyToNewName(String location, String name, String symbol, String description, GateComputingType gateComputingType, String ... parameters);
	
	public GateModel shallowCopyToNewName(String location, String name, String symbol, String description) {
		ImmutableArray<String> array = getParameters();
		return shallowCopyToNewName(location, name, symbol, description, gateComputingType, array.toArray(new String[array.size()]));
	}
	
	public ImmutableArray<String> getParameters() {
		return new ImmutableArray<>(parameters);
	}
	
	public boolean isClassical() {
		return gateComputingType.isClassical;
	}
	
	public boolean isQuantum() {
		return gateComputingType.isQuantum;
	}
	
	public String getLocationString() {
		return location;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getDescription() {
		return description;
	}
	
	public GateComputingType getComputingType() {
		return gateComputingType;
	}
	
	@SuppressWarnings("serial")
	public static class ImproperNameSchemeException extends RuntimeException {
		private ImproperNameSchemeException (String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NameTakenException extends RuntimeException {
		public NameTakenException (String message) {
			super(message);
		}
	}
	
}
