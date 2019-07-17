package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.appViews.AppView;
import appFX.appUI.appViews.AppView.AppViewOnOpenCloseListener;
import appFX.appUI.appViews.circuitBoardView.editingTools.ToolActionManager;
//import appFX.appUI.appViews.circuitBoardView.editingTools.SelectCursor;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.appViews.gateChooser.AbstractGateChooser;
import appFX.appUI.utils.LatexNode;
import appFX.appUI.utils.SolderableIcon;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;
import utils.customCollections.immutableLists.ImmutableArray;

public class CircuitBoardView extends AppView implements AppViewOnOpenCloseListener {
	
	@FXML
	private ScrollPane description;
	@FXML
	private TextField name, symbol;
	@FXML
	private HBox parameters;
	@FXML
	private CheckBox grid;
	@FXML
	private BorderPane circuitBoardIcon;
	@FXML
	private Pane contentPane;
	
	private final CircuitBoardModel circuitBoard;
	private final Project project;
	private final CircuitBoardRenderer renderer;
	private final ToolActionManager toolActionManager;
	
	private double mouseXDragStart = 0;
	private double mouseYDragStart = 0;
	
	public static void openCircuitBoard(String circuitBoardName) {
		AppStatus status = AppStatus.get();
		if(!status.getMainScene().getViewManager().containsView(circuitBoardName, ViewLayout.CENTER)) {
			CircuitBoardView circuitBoardView = new CircuitBoardView(status.getFocusedProject(), circuitBoardName);
			status.getMainScene().getViewManager().addView(circuitBoardView);
		} else {
			status.getMainScene().getViewManager().setCenteredFocusedView(circuitBoardName);
		}
	}
	
	private CircuitBoardView(Project project, String circuitBoard) {
		super("CircuitBoardView.fxml", circuitBoard, ViewLayout.CENTER);
		this.circuitBoard = (CircuitBoardModel) project.getCircuitBoardModels().get(circuitBoard);
		this.project = project;
		this.circuitBoard.setRenderEventHandler(new CircuitBoardEventHandler());
		this.renderer = new CircuitBoardRenderer(this);
		this.toolActionManager = new ToolActionManager(this);
		setOnOpenCloseListener(this);
		initialize();
	}
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		Project p = AppStatus.get().getFocusedProject();
		if(p.getCircuitBoardModels() == source) {
			if(methodName.equals("put")) {
				if(((GateModel)args[0]).getFormalName().equals(getName())) {
					closeView();
				}
			} else if (methodName.equals("replace") || methodName.equals("remove")){
				if(args[0].equals(getName())) {
					closeView();
				}
			}
		}
	}
	
	@FXML
	private void toggleGrid(ActionEvent ae) {
		renderer.setGridVisible(grid.isSelected());
	}
	
	public void editAsNew(ActionEvent ae) {
		AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, circuitBoard.getFormalName());
	}
	
	public void editProperties(ActionEvent ae) {
		AppCommand.doAction(AppCommand.EDIT_GATE, circuitBoard.getFormalName());
	}
	
	
	private void initialize() {
		grid.setSelected(true);
		
		setUpPropertiesUI();
		setUpRenderer();
		setUpToolManager();
	}
	
	public void setUpPropertiesUI() {
		name.setText(circuitBoard.getName());
		name.setEditable(false);
		
		symbol.setText(circuitBoard.getSymbol());
		symbol.setEditable(false);
		
		Node solderableIcon = SolderableIcon.mkIcon(circuitBoard);
		circuitBoardIcon.setLeft(solderableIcon);
		
		
		description.setContent(new LatexNode(circuitBoard.getDescription()));
		
		ImmutableArray<String> args = circuitBoard.getArguments();
		if(!args.isEmpty()) {
			String parametersLatex = "\\(" + args.get(0) + "\\)";
			for(int i = 1; i < args.size(); i++)
				parametersLatex += ", \\(" + args.get(i) + "\\)";
			
			parameters.getChildren().add(new LatexNode(parametersLatex, 20));
		}
	}
	
	private void setUpRenderer() {
		contentPane.getChildren().add(renderer.getAsNode());
		contentPane.widthProperty().addListener((event, oldV, newV) -> {
			renderer.resizeWidth(Math.round(newV.doubleValue()));
		});
		contentPane.heightProperty().addListener((event, oldV, newV) -> {
			renderer.resizeHeight(Math.round(newV.doubleValue()));
		});
		contentPane.setOnScroll((ScrollEvent event) -> {
			double mouseX = event.getX();
			double mouseY = event.getY();
			double previousZoom = renderer.getZoom();
			
			double deltaY = event.getDeltaY() > 0? 1 : -1;
			
			renderer.zoom(deltaY * .1d + previousZoom, mouseX, mouseY);
			
		});
		contentPane.setOnMousePressed((MouseEvent event) -> {
			mouseXDragStart = event.getX();
			mouseYDragStart = event.getY();
		});
		contentPane.setOnMouseDragged((MouseEvent event) ->{
			double mouseXMoved = event.getX() - mouseXDragStart;
			double mouseYMoved = event.getY() - mouseYDragStart;
			
			renderer.scroll(mouseXMoved, mouseYMoved);
			
			mouseXDragStart = event.getX();
			mouseYDragStart = event.getY();
		});
		renderer.calculateBounds();
		renderer.render();
		renderer.setGridVisible(grid.isSelected());
	}
	
	private void setUpToolManager() {
		toolActionManager.startManager();
		AppStatus.get().getMainScene().getToolManager().addToolButtonListener(toolActionManager.getToolChangedListener());
		AbstractGateChooser.addToggleListener(toolActionManager.getModelChangedListener());
		contentPane.setOnMouseClicked(toolActionManager::handle);
		contentPane.setOnMouseMoved(toolActionManager::handle);
	}
	
	public CircuitBoardModel getCircuitBoardModel() {
		return circuitBoard;
	}
	
	@Override
	public void appTabOpenClose(boolean isOpening) {
		if(!isOpening) {
			AppStatus.get().getMainScene().getToolManager().removeToolButtonListener(toolActionManager.getToolChangedListener());
			AbstractGateChooser.removeToggleListener(toolActionManager.getModelChangedListener());
		}
	}
	
	private class CircuitBoardEventHandler implements ReceivedEvent {
		@Override
		public void receive(Object source, String methodName, Object... args) {
			toolActionManager.getCurrentTool().reset();
			renderer.calculateBounds();
			renderer.render();
		}
	}
	
	public CircuitBoardRenderer getRenderer() {
		return renderer;
	}

	public Project getProject() {
		return project;
	}
	
	public ToolActionManager getToolActionManager() {
		return toolActionManager;
	}
	
}