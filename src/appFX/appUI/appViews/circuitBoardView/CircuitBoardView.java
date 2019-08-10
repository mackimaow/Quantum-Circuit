package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.appViews.AppView;
import appFX.appUI.appViews.AppView.AppViewOnOpenCloseListener;
import appFX.appUI.appViews.AppView.AppViewOnSelectedListener;
import appFX.appUI.appViews.circuitBoardView.editingTools.ToolActionManager;
//import appFX.appUI.appViews.circuitBoardView.editingTools.SelectCursor;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.NotificationRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.ToolActionRenderLayer;
import appFX.appUI.appViews.gateChooser.AbstractGateChooserView;
import appFX.appUI.utils.GateIcon;
import appFX.appUI.utils.LatexNode;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import utils.Notifier.ReceivedEvent;
import utils.customCollections.immutableLists.ImmutableArray;

public class CircuitBoardView extends AppView implements AppViewOnOpenCloseListener, AppViewOnSelectedListener {
	
	@FXML private ScrollPane description;
	@FXML private TextField fileLocation, name, gateType;
	@FXML private HBox parameters, symbol;
	@FXML private CheckBox grid;
	@FXML private BorderPane circuitBoardIcon;
	@FXML private Pane contentPane;
	
	private final CircuitBoardModel circuitBoard;
	private final Project project;
	private final CircuitBoardRenderer renderer;
	private final ToolActionManager toolActionManager;
	private boolean isSelected = false;
	private boolean mustRender = false;
	private boolean mustRenderToolActionLayer = false;
	
	private double mouseXDragStart = 0;
	private double mouseYDragStart = 0;
	
	public static CircuitBoardView openCircuitBoard(String circuitBoardName) {
		AppStatus status = AppStatus.get();
		if(!status.getMainScene().getViewManager().containsView(circuitBoardName, ViewLayout.CENTER)) {
			CircuitBoardView circuitBoardView = new CircuitBoardView(status.getFocusedProject(), circuitBoardName);
			status.getMainScene().getViewManager().addView(circuitBoardView);
			return circuitBoardView;
		} else {
			return (CircuitBoardView) status.getMainScene().getViewManager().setCenteredFocusedView(circuitBoardName);
		}
	}
	
	private CircuitBoardView(Project project, String circuitBoard) {
		super("views/CircuitBoardView.fxml", circuitBoard, ViewLayout.CENTER);
		this.circuitBoard = (CircuitBoardModel) project.getCircuitBoardModels().get(circuitBoard);
		this.project = project;
		this.circuitBoard.setRenderEventHandler(new CircuitBoardEventHandler());
		this.renderer = new CircuitBoardRenderer(this);
		this.toolActionManager = new ToolActionManager(this);
		setOnOpenCloseListener(this);
		setOnTabSelectedListener(this);
		initialize();
	}
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		Project p = AppStatus.get().getFocusedProject();
		if(p.getCircuitBoardModels() == source) {
			if(methodName.equals("put")) {
				if(((GateModel)args[0]).getLocationString().equals(getName())) {
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
		AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, circuitBoard.getLocationString());
	}
	
	public void editProperties(ActionEvent ae) {
		AppCommand.doAction(AppCommand.EDIT_GATE, circuitBoard.getLocationString());
	}
	
	
	private void initialize() {
		grid.setSelected(true);
		
		setUpPropertiesUI();
		setUpRenderer();
		setUpToolManager();
	}
	
	public void setUpPropertiesUI() {
		fileLocation.setText(circuitBoard.getLocationString());
		fileLocation.setEditable(false);
		
		name.setText(circuitBoard.getName());
		name.setEditable(false);
		
		symbol.getChildren().add(new LatexNode(circuitBoard.getSymbol(), 20));
		
		gateType.setText(circuitBoard.getComputingType().toString());
		gateType.setEditable(false);
		
		ImageView imageView = GateIcon.gateModelToIconNode(circuitBoard);
		circuitBoardIcon.setCenter(imageView);
		
		
		description.setContent(new LatexNode(circuitBoard.getDescription()));
		
		ImmutableArray<String> args = circuitBoard.getParameters();
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
		AbstractGateChooserView.addToggleListener(toolActionManager.getModelChangedListener());
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
			AbstractGateChooserView.removeToggleListener(toolActionManager.getModelChangedListener());
		}
	}
	
	public void renderWhenSelected() {
		if(isSelected)
			renderAll();
		else
			mustRender = true;
	}
	
	private void renderAll() {
		renderer.calculateBounds();
		renderer.render();
	}
	
	public void renderToolActionLayerWhenSelected() {
		if(isSelected)
			renderToolActionLayer();
		else
			mustRenderToolActionLayer = true;
	}
	
	private void renderToolActionLayer() {
		ToolActionRenderLayer rl = (ToolActionRenderLayer) renderer.getLayer(CircuitBoardRenderer.TOOL_RENDER_LAYER_INDEX);
		rl.calculateBounds();
		rl.render();
	}
	
	public void renderErrorAt(int rowStart, int rowEnd, int column) {
		int notificationLayerIndex = CircuitBoardRenderer.NOTIFICATION_RENDER_LAYER_INDEX;
		NotificationRenderLayer rl = (NotificationRenderLayer) renderer.getLayer(notificationLayerIndex);
		rl.calculateDrawErrorBounds(rowStart, rowEnd, column, renderer.getGridData(), renderer.getRowTypeList());
		double moveX = column + .5d;
		double moveY = rowStart + (rowEnd - rowStart) / 2d + .5d;
		renderer.scrollToGrid(moveY, moveX, 1d);
	}
	
	@Override
	public void appTabSelect(boolean isSelected) {
		this.isSelected = isSelected;
		if(isSelected) { 
			if(mustRender)
				renderAll();
			else if (mustRenderToolActionLayer)
				renderToolActionLayer();
			mustRender = false;
			mustRenderToolActionLayer = false;
		}
	}
	
	private class CircuitBoardEventHandler implements ReceivedEvent {
		@Override
		public void receive(Object source, String methodName, Object... args) {
			toolActionManager.getCurrentTool().reset();
			renderWhenSelected();
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