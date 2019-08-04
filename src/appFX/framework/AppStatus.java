package appFX.framework;

import java.net.URI;

import appFX.appPreferences.AppPreferences;
import appFX.appUI.MainScene;
import appFX.appUI.appViews.AppViewManager;
import appFX.appUI.appViews.ConcreteView;
import appFX.appUI.appViews.ConsoleView;
import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.AppFileIO;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import utils.customCollections.LagArrayList;
import utils.customCollections.eventTracableCollections.Notifier;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;


/**
 * Singleton class;
 * 
 * This instance contains all the states of the current session of this application. <br>
 * For example, a project is set focused here. <br>
 * <p>
 * Access to the console is here as well. <br>
 * 
 * @author Massimiliano Cutugno
 *
 */
public final class AppStatus implements EventHandler<WindowEvent> {
	
	private static AppStatus status = null;
	
	private final Stage primaryStage;
	private final MainScene mainscene;
	private Project project = null;
	private LagArrayList<ReceivedEvent> changeHandlers;
	private final Notifier notifierFan;
	private final Notifier notifier;
	private boolean isProjectModifed;
	
	
	/**
	 * Can only be set once throughout the whole application runtime <br>
	 * 
	 * Used to initiate the status of this application.
	 * 
	 * @param primaryStage
	 * @param mainScene
	 */
	public static void initiateAppStatus(Stage primaryStage, MainScene mainScene) {
		if(status != null)
			return;
		status = new AppStatus(primaryStage, mainScene);
		primaryStage.setOnCloseRequest(status);
	}
	
	/**
	 * @return the {@link AppStatus} instance of this program
	 */
	public static AppStatus get() {
		return status;
	}
	
	
	
	
	
	
	
	
	private AppStatus(Stage primaryStage, MainScene mainScene) {
		this.primaryStage = primaryStage;
		this.mainscene = mainScene;
		this.changeHandlers = new LagArrayList<>();
		this.isProjectModifed = false;
		this.notifierFan = new Notifier();
		this.notifier = new Notifier(notifierFan);
		this.notifier.setReceivedEvent((source, method, args) -> {
			isProjectModifed = true;
		});
		this.notifierFan.setReceivedEvent((source, method, args) -> {
			changeHandlers.setLagged(true);
			for(ReceivedEvent re : changeHandlers)
				re.receive(source, method, args);
			changeHandlers.setLagged(false);
		});
		
		addAppChangedListener(mainScene);
	}
	
	
	
	
	
	
	
	/**
	 * @return the console controller of this application
	 */
	public ConsoleView getConsole() {
		return (ConsoleView) ConcreteView.CONSOLE.getView();
	}
	
	
	/**
	 * 
	 * Makes a {@link Project} instance focusable to this application
	 * 
	 * <b>REQUIRES:</b> that the project is not null <br>
	 * <b>ENSURES:</b> the GUI is notified of this change <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param project
	 */
	public void setFocusedProject(Project project) {
		
		if(project == null)
			return;
		
		if(isProjectModifed) {
			ButtonType type1 = new ButtonType("Save");
			ButtonType type2 = new ButtonType("Continue without saving");
			ButtonType type3 = new ButtonType("Cancel");
			
			ButtonType buttonType = AppAlerts.showButtonMessage(primaryStage,
					"The current project is not saved",
					"Do you want to continue without saving?", AlertType.CONFIRMATION, type1, type2, type3);
			
			
			if(buttonType == type1) {
				Object o = AppCommand.doAction(AppCommand.SAVE_PROJECT);
				if(o != null && !((Boolean) o))
					return;
			} else if(buttonType == type2) {
			} else if(buttonType == type3) {
				return;
			}
		}
		
		// set previously focused project unfocused
		if(this.project != null)
			this.project.setReceiver(null);
		
		
		notifier.sendChange(this, "setFocusedProject", project);
		this.project = project;
		this.project.setReceiver(notifier);
		setProjectSavedFlag();
		
		if(project.getTopLevelCircuitLocationString() != null)
			AppCommand.doAction(AppCommand.OPEN_GATE, project.getTopLevelCircuitLocationString());
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Sets the current {@link Project} status to saved
	 * <b>MODIFIES INSTANCE</b>
	 */
	public void setProjectSavedFlag() {
		isProjectModifed = false;
	}
	
	
	/**
	 * 
	 * <b>MODIFIES INSTANCE</b>
	 * @param changeListener
	 */
	public void addAppChangedListener(ReceivedEvent changeListener) {
		changeHandlers.add(changeListener);
	}
	
	public void removeAppChangedListener(ReceivedEvent changeListener) {
		changeHandlers.remove(changeListener);
	}
	
	
	public Project getFocusedProject() {
		return project;
	}
	
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public MainScene getMainScene() {
		return mainscene;
	}

	@Override
	public void handle(WindowEvent event) {
		if(isProjectModifed && project != null) {
			ButtonType type1 = new ButtonType("Save");
			ButtonType type2 = new ButtonType("Exit Anyway");
			ButtonType type3 = new ButtonType("Cancel");
			
			ButtonType buttonType = AppAlerts.showButtonMessage(primaryStage, "Project is not saved", 
					"The current project is not saved, would you like to save before exiting?", AlertType.CONFIRMATION, type1, type2, type3);
			
			if(buttonType == type1) {
				if(AppFileIO.saveProject(project, primaryStage) != AppFileIO.SUCCESSFUL) {
					event.consume();
					return;
				}
			} else if (buttonType == type3) {
				event.consume();
				return;
			}
		}
		
		AppViewManager viewManager = AppStatus.get().getMainScene().getViewManager();
		AppPreferences.Booleans.CONSOLE_OPEN.set(viewManager.containtsView(ConcreteView.CONSOLE));
		AppPreferences.Booleans.CUSTOM_GATES_OPEN.set(viewManager.containtsView(ConcreteView.CUSTOM_GATES_VIEW));
		AppPreferences.Booleans.PRESET_GATES_OPEN.set(viewManager.containtsView(ConcreteView.PRESET_GATES_VIEW));
		AppPreferences.Booleans.CIRCUITBOARDS_OPEN.set(viewManager.containtsView(ConcreteView.CIRCUITBOARD_VIEW));
		AppPreferences.Booleans.PROJECT_HIERARCHY_OPEN.set(viewManager.containtsView(ConcreteView.PROJECT_HIERARCHY));
		
		URI location = project.getProjectFileLocation();
		if(location != null)
			AppPreferences.Strings.PREVIOUS_PROJ_URL.set(location.toString());
//		AppPreferences.Booleans.CONSOLE_OPEN.set(mainscene.);
	}


}
