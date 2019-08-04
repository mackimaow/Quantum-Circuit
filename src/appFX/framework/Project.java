package appFX.framework;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;

import appFX.appUI.utils.AppFileIO;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.GateComputingType;
import appFX.framework.gateModels.PresetGateType;
import utils.customCollections.Pair;
import utils.customCollections.eventTracableCollections.Notifier;


/**
 * All Quantum Circuits designed within this application are done by modifying an instance of {@link Project} <br>
 * Only one {@link Project} can be focused to be edited upon within one session of this Application <br>
 * <p>
 * All {@link CustomGateModel}s, {@link OracleModel}s, and {@link CircuitBoardModel}s used within this project <br>
 * are stored as lists within a instance of this class <br> 
 * 
 * <p>
 * WARNING: Not thread safe when using object methods that modify <br>
 * internal fields (may cause the GUI to not update as expected) <br>
 * 
 * @author Massimiliano Cutugno
 *
 */
public class Project implements Serializable {
	private static final long serialVersionUID = 8906661352790858317L;
	
	private final ProjectHashtable subCircuits;
    private final ProjectHashtable customGates;
	
	private transient URI projectFileLocation = null;
	
	// Notifies User-Interface of changes
	private Notifier notifier;
	private String topLevelCircuitLocationString = null;
	
	
	
	
	/**
	 * @return A new untitled default {@link Project} which consists of one untitled top-level default circuit board
	 */
	public static Project createNewTemplateProject() {
		Project project = new Project();
		project.topLevelCircuitLocationString = project.addUntitledSubCircuit();
		return project;
	}
	
	
	
	
	/**
	 * @param fileLocation project location on the user's drive
	 * @return the file name of the fileLocation without the extension
	 */
	public static String getProjectNameFromURI(URI fileLocation) {
		if(fileLocation == null) {
			return "Untitled_Project";
		} else {
			String fileName = new File(fileLocation).getName();
			return fileName.substring(0, fileName.length() - AppFileIO.QUANTUM_PROJECT_EXTENSION.length() - 1);
		}
	}
	
	
	
	
	
	/**
	 * Creates an empty Project <br>
	 * Project contains no sub-circuits or top-level-components <br>
	 */
	public Project() {
		notifier = new Notifier();
		subCircuits = new ProjectHashtable();
		customGates = new ProjectHashtable();
	}
	
	
	
	
	
	/**
	 * <b>ENSURES:</b>  cb is added to project as a untitled sub-circuit<br>
	 * <b>MODIFIES INSTANCE</b>
	 * @return the generated name of this sub-circuit
	 * @throws {@link RuntimeException} if sub-circuit count exceeds Integer.MAX_VALUE
	 */
	public String addUntitledSubCircuit(){
		Pair<String, Integer> nameAttr = makeUnusedUntitledBoardName();
		
		String name = nameAttr.first();
		String symbol = "U" + nameAttr.second();

		notifier.sendChange(this, "addUntitledSubCircuit");
		String location = name + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION;
		subCircuits.put(new CircuitBoardModel(location, name, symbol, "", GateComputingType.QUANTUM, 5, 5));
		
		return location;
	}
	
	
	public Pair<String, Integer> makeUnusedUntitledBoardName() {
		final String untitledString = "Untitled";
		
		if(!subCircuits.containsGateModel(untitledString + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION))
			return new Pair<>(untitledString, 0);
		
		int i = 1;
		while(subCircuits.containsGateModel(untitledString + "_" + Integer.toString(i) + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
			if(i == Integer.MAX_VALUE)
				throw new RuntimeException("Could not add any more untitled sub-ciruits");
			i++;
		}
		
		return new Pair<>(untitledString + "_" + Integer.toString(i), i);
	}
	
	
	
	
	
	
	/**
	 * @return the name of this project (the given file name without extension)
	 */
	public String getProjectName() {
		return getProjectNameFromURI(projectFileLocation);
	}
	
	
	
	
	
	/**
	 * @return the list of the sub-circuits for this project
	 */
	public ProjectHashtable getCircuitBoardModels() {
		return subCircuits;
	}
	
	
	
	
	
	/**
	 * @return the list of the custom-gates for this project
	 */
	public ProjectHashtable getCustomGates() {
		return customGates;
	}
	
	
	

	
	/**
	 * @return the name of the top-level sub-circuit
	 */
	public String getTopLevelCircuitLocationString() {
		return topLevelCircuitLocationString;
	}
	
	
	public boolean hasTopLevel() {
		return topLevelCircuitLocationString != null;
	}
	
	
	/**
	 * <b>REQUIRES:</b> name is not null <br>
	 * <b>ENSURES:</b> name is set to the top-level sub-circuit && <br>
	 * GUI is notified of the change (if this project is focused) <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param name
	 */
	public void setTopLevelCircuitName(String circuitBoardLocationString) {
		if(circuitBoardLocationString == null) {
			notifier.sendChange(this, "setTopLevelCircuitName", circuitBoardLocationString);
			topLevelCircuitLocationString = null;
		} else {
			String[] parts = circuitBoardLocationString.split("\\.");
			if(parts.length == 2 && parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				if(subCircuits.containsGateModel(circuitBoardLocationString)) {
					notifier.sendChange(this, "setTopLevelCircuitName", circuitBoardLocationString);
					topLevelCircuitLocationString = circuitBoardLocationString;
				}
			}
		}
	}
	
	
	
	/**
	 * @return this project's location on the drive
	 */
	public URI getProjectFileLocation() {
		return projectFileLocation;
	}
	
	
	
	
	
	/**
	 * <b>REQUIRES:<b> fileLocation is not null
	 * <b>ENSURES:</b> 
	 * <b>MODIFIES INSTANCE</b>
	 * @param fileLocation location of the 
	 */
	public void setProjectFileLocation(URI fileLocation) {
		notifier.sendChange(this, "setProjectFileLocation", fileLocation);
		this.projectFileLocation = fileLocation;
	}
	
	
	
	/**
	 * <b>ENSURES:</b> that all changes to this project are notified to receiver. <br>
	 * If null, then all changes will not be notified to any receiver <br>
	 * @param receiver
	 */
	public void setReceiver(Notifier receiver) {
		this.notifier.setReceiver(receiver);
	}
	
	public GateModel getGateModel(String gateModelLocationString) {
		String[] parts = gateModelLocationString.split("\\.");
		
		if(parts.length == 2) {
			if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				return subCircuits.get(gateModelLocationString);
			} else if(parts[1].equals(BasicGateModel.GATE_MODEL_EXTENSION)) {
				PresetGateType pgt = PresetGateType.getPresetTypeByLocation(gateModelLocationString);
				if(pgt != null) 
					return pgt.getModel();
				else
					return customGates.get(gateModelLocationString);
			}
		}
		return null;
	}
	
	public boolean containsGateModel(String gateModelLocationString) {
		String[] parts = gateModelLocationString.split("\\.");
		
		if(parts.length == 2) {
			if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				return subCircuits.containsGateModel(gateModelLocationString);
			} else if(parts[1].equals(BasicGateModel.GATE_MODEL_EXTENSION)) {
				if(PresetGateType.containsPresetTypeByLocation(gateModelLocationString))
					return true;
				else
					return customGates.containsGateModel(gateModelLocationString);
			}
		}
		return false;
	}
	
	
	public class ProjectHashtable implements Serializable {
		private static final long serialVersionUID = -903225963940733391L;
		private Hashtable <String, GateModel> elements;
		
		private ProjectHashtable () {
			elements = new Hashtable<>();
		}
		
		public void put(GateModel newValue) {
			
			if(elements.containsKey(newValue.getLocationString())) {
				GateModel gm = elements.get(newValue.getLocationString());
				
				if(gm == newValue)
					return;
				
				notifier.sendChange(this, "put", newValue);
				
				elements.put(newValue.getLocationString(), newValue);
				
				removeCircuitBoardTraits(gm, true);
				removeAllOccurances(gm.getLocationString());
				
			} else {
				notifier.sendChange(this, "put", newValue);
				
				elements.put(newValue.getLocationString(), newValue);
			}

			addCircuitBoardTraits(newValue);
		}
		
		
		
		
		public void replace(String gateModelLocationStringToReplace, GateModel newValue) {
			if(newValue == null)
				throw new NullPointerException("Gate to replace cannot be null");
			
			GateModel toReplace = elements.get(gateModelLocationStringToReplace);
			
			
			if(toReplace == null) {
				throw new RuntimeException("Gate \"" + gateModelLocationStringToReplace + "\" does not exist and cannot be replaced");
			} else {
				if(toReplace == newValue)
					return;
				if(gateModelLocationStringToReplace.equals(newValue.getLocationString())) {
					notifier.sendChange(this, "replace", gateModelLocationStringToReplace, newValue);
					
					elements.put(newValue.getLocationString(), newValue);
					removeCircuitBoardTraits(toReplace, false);
				} else {
					notifier.sendChange(this, "replace", gateModelLocationStringToReplace, newValue);
					
					for(GateModel circ : subCircuits.getGateModelIterable())
						if(circ != toReplace)
							((CircuitBoardModel)circ).changeAllOccurrences(gateModelLocationStringToReplace, newValue.getLocationString());
					
					elements.remove(gateModelLocationStringToReplace);
					elements.put(newValue.getLocationString(), newValue);
					
					if(removeCircuitBoardTraits(toReplace, true))
						setTopLevelCircuitName(newValue.getLocationString());
				}
			}
		}
		
		public GateModel get (String gateModelLocationString) {
			return elements.get(gateModelLocationString);
		}
		
		public void remove (String gateModelLocationString) {
			if(elements.containsKey(gateModelLocationString)) {
				
				notifier.sendChange(this, "remove", gateModelLocationString);
				
				GateModel gm = elements.get(gateModelLocationString);

				elements.remove(gateModelLocationString);
				removeCircuitBoardTraits(gm, true);
				removeAllOccurances(gateModelLocationString);
			}
		}
		
		public boolean containsGateModel (GateModel gateModel) {
			return elements.contains(gateModel);
		}
		
		public boolean containsGateModel (String gateModelLocationString) {
			return elements.containsKey(gateModelLocationString);
		}
		
		public Iterable<String> getGateNameIterable() {
			return elements.keySet();
		}
		
		public Iterable<GateModel> getGateModelIterable() {
			return elements.values();
		}
		
		public int size() {
			return elements.size();
		}
		
		private void removeAllOccurances (String gmLocationString) {
			for(GateModel gm : subCircuits.getGateModelIterable()) {
				CircuitBoardModel cbm = (CircuitBoardModel) gm;
				cbm.removeAllOccurrences(gmLocationString);
			}
		}
		
		private void addCircuitBoardTraits (GateModel gm) {
			if(gm instanceof CircuitBoardModel) {
				CircuitBoardModel cb = (CircuitBoardModel) gm;
				cb.getNotifier().setReceiver(notifier);
			}
		}
		
		private boolean removeCircuitBoardTraits (GateModel gm, boolean removeTopLevel) {
			if(gm instanceof CircuitBoardModel) {
				CircuitBoardModel cb = (CircuitBoardModel) gm;
				cb.getNotifier().setReceiver(null);
				if(removeTopLevel && topLevelCircuitLocationString != null && cb.getLocationString().equals(topLevelCircuitLocationString)) {
					setTopLevelCircuitName(null);
					return true;
				}
			}
			return false;
		}
		
	}
}
