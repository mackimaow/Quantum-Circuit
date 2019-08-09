package appFX.appUI.wizards;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import appFX.appUI.appViews.circuitBoardView.renderer.GateRenderer;
import appFX.appUI.utils.AppFileChooserTools;
import appFX.appUI.utils.AppFileChooserTools.FileObjectConverter;
import appFX.appUI.utils.SequencePaneElement;
import appFX.appUI.utils.SequencePaneElement.SequencePaneFinish;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.CircuitBoardModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import utils.customCollections.Pair;

public class CircuitBoardToPNGWizard extends Wizard<Pair<BufferedImage, File>> {
	
	public static final String PNG_DESCRIPTION = "Portable Network Graphics (PNG)";
	public static final String PNG_EXTENSION = "png";
	
	
	public CircuitBoardToPNGWizard() {
		super(500, 600);
	}
	
	@Override
	protected boolean onWizardCloseRequest() {
		return true;
	}
	
	@Override
	protected SequencePaneElement getFirstSeqPaneElem() {
		return new FirstElementPane();
	}
	
	private class FirstElementPane extends SequencePaneElement implements SequencePaneFinish<Pair<BufferedImage, File>>, FileObjectConverter<BufferedImage> {
		
		@FXML private TextField circuitBoardNameTextField;
		@FXML private TextField sizeTextField;
		@FXML private TextField locationTextField;
		private AppFileChooserTools<BufferedImage> fileChooserTools;
		
		public FirstElementPane() {
			super("wizards/circuitBoardToPngWizard/CircuitBoardToPNG.fxml");
			fileChooserTools = new AppFileChooserTools<>(getStage(), "Save Image At", this, PNG_DESCRIPTION, PNG_EXTENSION);
		}
		
		@Override
		public void initialize() {}
		
		@FXML
		private void browsePressed (ActionEvent event) {
			File f = fileChooserTools.fileChooserSaveGetFile();
			if(f != null)
				locationTextField.setText(f.getAbsolutePath());
		}
		
		@Override
		public void setStartingFieldData(PaneFieldDataList controlData) {
			controlData.add(circuitBoardNameTextField, "");
			controlData.add(sizeTextField, "");
			controlData.add(locationTextField, "");
		}
		
		@Override
		public boolean checkFinish() {
			Project p = AppStatus.get().getFocusedProject();
			boolean containsGateModel = p.getCircuitBoardModels().containsGateModel(circuitBoardNameTextField.getText());
			if(checkTextFieldError(getStage(), circuitBoardNameTextField, !containsGateModel, "Invalid Circuit Board Name", "The Circuit Board name is not entered correctly or does not exist")) return false;
			Double size = getDouble(sizeTextField.getText());
			if(checkTextFieldError(getStage(), sizeTextField, size == null, "Size error", "Invalid size entered")) return false;
			if(checkTextFieldError(getStage(), sizeTextField, size < .1 || size > 100, "Size error", "Size must be between 0.1 and 100 inclusize.")) return false;
			File f = new File(locationTextField.getText());
			if(!fileChooserTools.checkIfFileIsOkayToSave(f)) return false;
			return true;
		}
		
		@Override
		public BufferedImage convert(File f) throws IOException {
			return null;
		}

		@Override
		public boolean save(BufferedImage o, File file) throws IOException {
			return ImageIO.write(o, "png", file);
		}
		
		@Override
		public Pair<BufferedImage, File> getFinish() {
			Project p = AppStatus.get().getFocusedProject();
			CircuitBoardModel cbm = (CircuitBoardModel) p.getGateModel(circuitBoardNameTextField.getText());
			Double size = getDouble(sizeTextField.getText());
			File f = new File(locationTextField.getText());
			BufferedImage bimg = GateRenderer.getCircuitBoardImage(cbm, size);
			fileChooserTools.writeProject(bimg, f);
			return new Pair<>(bimg, f);
		}

		@Override
		public boolean hasFinish() {
			return true;
		}
	}
}
