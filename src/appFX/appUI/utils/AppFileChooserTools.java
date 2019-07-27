package appFX.appUI.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AppFileChooserTools<T> {
	public static final int QUIT = -1;
	public static final int SUCCESSFUL = 0;
	public static final int ERROR = 1;
	
	private final FileObjectConverter<T> fileObjectConverter;
	private final Stage stage;
	private final String title;
	private final ExtensionFilter[] filters;
	
	public AppFileChooserTools(Stage stage, String title, FileObjectConverter<T> fileToObject, String ... extensionFilterData) {
		if(extensionFilterData.length % 2 != 0) throw new IllegalArgumentException("Each extension must have a description defined as the previous argument");
		this.fileObjectConverter = fileToObject;
		this.stage = stage;
		this.title = title;
		this.filters = new ExtensionFilter[extensionFilterData.length / 2];
		
		for(int i = 0; i < extensionFilterData.length; i+=2)
			this.filters[i] = new ExtensionFilter(extensionFilterData[i], "*." + extensionFilterData[i + 1]);
	}
	
	private FileChooser create() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(title);
		fileChooser.getExtensionFilters().addAll(filters);
		if(filters.length > 0)
			fileChooser.setSelectedExtensionFilter(filters[0]);
		return fileChooser;
	}
	
	public File fileChooserOpenGetFile() {
		FileChooser fileChooser = create();
		// display file chooser and wait for file selection
		File file = fileChooser.showOpenDialog(stage);
		return file;		
	}
	
	public File fileChooserSaveGetFile() {
		FileChooser fileChooser = create();
		// display file chooser and wait for file selection
		File file = fileChooser.showSaveDialog(stage);
		ExtensionFilter ef = fileChooser.getSelectedExtensionFilter();
		file  = appendWithExtIfNeeded(file, ef.getExtensions());
		
		return file;
	}
	
	
	public T openObject() {
		FileChooser fileChooser = create();
		
		// display file chooser and wait for file selection
		File file = fileChooser.showOpenDialog(stage);
		
		// if user did not choose a file
		if(file == null)
			return null;
		
		// load object
		T loadedObject = loadObject(file.toURI());
		
		return loadedObject;
	}
	
	
	public T loadObject(URI location) {
		File file = new File(location);
		
		T loadedObject = null;
		
		if(!file.exists() || file.isDirectory())
			return null;
		
		try {
			loadedObject = fileObjectConverter.convert(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// if object couldn't be loaded
		if(loadedObject == null)
			AppAlerts.showMessage(stage, "Could not open", "The choosen file could be opened. Please check permissions.", AlertType.ERROR);
		
		return loadedObject;
	}
	
	public boolean checkIfFileIsOkayToSave(File f) {
		if(f.exists()) {
			if(f.isDirectory()) {
				AppAlerts.showMessage(stage, "Could not save", "The choosen file is a directory. Please try again.", AlertType.ERROR);
				return false;
			} else {
				ButtonType buttonType = AppAlerts.showMessage(stage, "Comfirmation", "The file selected exists, are you sure you want to replace this file?", AlertType.CONFIRMATION);
				if(buttonType == ButtonType.OK) 
					return true;
				else 
					return false;
			}
		}
		return true;
	}
	
	public int saveAs(T object) {
		FileChooser fileChooser = create();
		
		// display file chooser and wait for file selection
		File file = fileChooser.showSaveDialog(stage);
		
		// if user did not choose a file
		if(file == null)
			return QUIT;
		
		// adds appropriate extension if user did not specify
		ExtensionFilter ef = fileChooser.getSelectedExtensionFilter();
		
		file  = appendWithExtIfNeeded(file, ef.getExtensions());
					
		// finally save the file
		boolean succesful = writeProject(object, file);
		
		return succesful? SUCCESSFUL : ERROR;
	}
	
	
	
	private static File appendWithExtIfNeeded(File file, List<String> exts) {
		if(exts.size() == 0) return file;
		String fileName = file.getName();
		String ext = exts.get(0).substring(2);
		
		if(fileName.length() <= ext.length() || !fileName.endsWith("." + ext))
			return new File(file.getParentFile(), fileName + "." + ext);
		return file;
	}
	
	
	
	public boolean writeProject(T object, File file) {
		boolean savedSuccessfuly = false;
		try {
			savedSuccessfuly = fileObjectConverter.save(object, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!savedSuccessfuly)
			AppAlerts.showMessage(stage, "Could not save", "The file could not save. Please check permissions.", AlertType.ERROR);
		return savedSuccessfuly;
	}
	
	public static interface FileObjectConverter<T> {
		public T convert(File f) throws IOException;
		public boolean save(T o, File file) throws IOException;
	}
}
