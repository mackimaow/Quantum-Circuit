package appFX.appUI.appViews;

import java.util.Iterator;

import appFX.appPreferences.AppPreferences.Doubles;
import appFX.appUI.appViews.AppView.AppViewOnOpenCloseListener;
import appFX.appUI.appViews.AppView.ViewLayout;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.utils.AppFXMLComponent;
import appFX.appUI.utils.AppTab;
import appFX.framework.AppStatus;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import utils.Notifier.ReceivedEvent;

public class AppViewManager implements ReceivedEvent {
	private TabPane leftTabPane, centerTabPane, rightTabPane, bottomTabPane;
	private double[] cachedDividerPositions;
	boolean initialized = false;
	
	public AppViewManager(TabPane leftTabPane, TabPane centerTabPane, TabPane rightTabPane, TabPane bottomTabPane) {
		this.cachedDividerPositions = new double[] { Doubles.LEFT_DIVIDER_POSITION.get(),
				Doubles.BOTTOM_DIVIDER_POSITION.get(), Doubles.RIGHT_DIVIDER_POSITION.get()};
		this.leftTabPane = leftTabPane;
		this.centerTabPane = centerTabPane;
		this.rightTabPane = rightTabPane;
		this.bottomTabPane = bottomTabPane;
	}
	
	
	
	public void addCircuitBoardView(CircuitBoardView circuitBoardView) {
		addView(centerTabPane, circuitBoardView);
	}
	
	
	public void addView(ConcreteView concreteView) {
		addView(concreteView.getView());
	}
	
	public boolean containtsView(ConcreteView concreteView) {
		return containsView(concreteView.getView().getName(), concreteView.getView().getLayout());
	}
	
	
	public boolean containsView(String viewName, ViewLayout viewLayout) {
		switch(viewLayout) {
		case LEFT:
			return containsTab(viewName, leftTabPane);
		case CENTER:
			return containsTab(viewName, centerTabPane);
		case RIGHT:
			return containsTab(viewName, rightTabPane);
		case BOTTOM:
		default:
			return containsTab(viewName, bottomTabPane);
		}
	}
	
	private boolean containsTab(String name, TabPane tabPane) {
		Iterator<Tab> i = tabPane.getTabs().iterator();
		Tab t;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	private AppTab getTabByName(String name, TabPane tabPane) {
		Iterator<Tab> i = tabPane.getTabs().iterator();
		Tab t;
		while(i.hasNext()) {
			t = i.next();
			if(t.getText().equals(name))
				return (AppTab) t;
		}
		return null;
	}
	
	
	public void addView(AppView view) {
		switch(view.getLayout()) {
		case LEFT:
			addView(leftTabPane, view);
			break;
		case CENTER:
			addView(centerTabPane, view);
			break;
		case RIGHT:
			addView(rightTabPane, view);
			break;
		case BOTTOM:
		default:
			addView(bottomTabPane, view);
			break;
		}
	}
	
	private void addView(TabPane tabPane, AppView view) {
		AppTab tab = view.getTab();
		String name = tab.getText();
		if(containsTab(name, tabPane)) {
			AppTab alreadyContainedTab = getTabByName(name, tabPane);
			tabPane.getSelectionModel().select(alreadyContainedTab);
		} else {
			if(view.onOpenRequest()) {
				AppViewOnOpenCloseListener listener = view.getOnOpenCloseListener();
				if (listener != null)
					listener.appTabOpenClose(true);
				tabPane.getTabs().add(tab);
				tabPane.getSelectionModel().select(tab);
				if(!view.isUpdatedInBackground())
					AppStatus.get().addAppChangedListener(view);
			}
		}
	}
	
	public void removeView(ConcreteView singleView) {
		removeView(singleView.getView());
	}
	
	
	
	public void removeView (AppView view ) {
		
		switch(view.getLayout()) {
		case LEFT:
			removeView(leftTabPane, view);
			break;
		case CENTER:
			removeView(centerTabPane, view);
			break;
		case RIGHT:
			removeView(rightTabPane, view);
			break;
		case BOTTOM:
		default:
			removeView(bottomTabPane, view);
			break;
		}
	}
	
	
	private void removeView(TabPane tabPane, AppView view) {
		ObservableList<Tab> tabs = tabPane.getTabs();
		if(containsTab(view.getName(), tabPane)) {
			if(view.onCloseRequest()) {
				AppViewOnOpenCloseListener listener = view.getOnOpenCloseListener();
				if (listener != null)	
					listener.appTabOpenClose(false);
				tabs.remove(view.getTab());
				if(!view.isUpdatedInBackground())
					AppStatus.get().removeAppChangedListener(view);
			}
		}
	}
	
	public AppFXMLComponent setCenteredFocusedView(String tabName) {
		AppTab toFocus = getTabByName(tabName, centerTabPane);
		centerTabPane.getSelectionModel().select(toFocus);
		return toFocus.getAppFXMLComponent();
	}
	
	public AppTab getCenterFocusedView() {
		AppTab t = (AppTab) centerTabPane.getSelectionModel().getSelectedItem();
		if(t == null)
			return null;
		return t;
	}
	
	public void initializeViews(BorderPane leftBorderPane, BorderPane rightBorderPane, BorderPane bottomBorderPane, SplitPane verticalSplitPane, SplitPane horizontalSplitPane) {
		for(ConcreteView tv : ConcreteView.values())
			AppStatus.get().addAppChangedListener(tv.getView());
		if (!initialized) {
			initialized = true;
			for(ConcreteView ctv :  ConcreteView.values())
				if(ctv.wasOpen().get())
					addView(ctv);
			initializeTabPanes(leftBorderPane, rightBorderPane, bottomBorderPane, verticalSplitPane, horizontalSplitPane);
		}
	}
	
	
	private void initializeTabPanes(BorderPane leftBorderPane, BorderPane rightBorderPane, BorderPane bottomBorderPane, SplitPane verticalSplitPane, SplitPane horizontalSplitPane) {
		linkDividerAndTabPane(leftTabPane, 0, leftBorderPane, horizontalSplitPane);
		linkDividerAndTabPane(bottomTabPane, 1, bottomBorderPane, verticalSplitPane);
		linkDividerAndTabPane(rightTabPane, 2, rightBorderPane, horizontalSplitPane);
	}
	
	
	
	private void linkDividerAndTabPane(TabPane tabPane, int cachedDividerPositionIndex, BorderPane borderPane, SplitPane splitPane) {
		if(cachedDividerPositionIndex > 2 || cachedDividerPositionIndex < 0)
			throw new IllegalArgumentException("cachedDividerPositionIndex must be between 0 and 2 (inclusive)");
		
		ObservableList<Tab> tabs = tabPane.getTabs();
		int dividerIndex = cachedDividerPositionIndex > 1 ? 1 : 0;
		int dividerClosePosition = cachedDividerPositionIndex > 0? 1 : 0;
		
				
		tabs.addListener(new ListChangeListener<Tab>() {
			@Override
			public void onChanged(Change<? extends Tab> c) {
				while(c.next()) {
					
					if(c.wasRemoved() && c.getList().isEmpty()) {
						cachedDividerPositions[cachedDividerPositionIndex] = splitPane.getDividers().get(dividerIndex).getPosition();
						borderPane.setMaxWidth(0);
						borderPane.setMaxHeight(0);
						borderPane.setVisible(false);
						borderPane.setManaged(false);
						
					} else if(c.wasAdded() && c.getAddedSize() == c.getList().size()){
						borderPane.setMaxWidth(BorderPane.USE_COMPUTED_SIZE);
						borderPane.setMaxHeight(BorderPane.USE_COMPUTED_SIZE);
						borderPane.setVisible(true);
						borderPane.setManaged(true);
						
						splitPane.setDividerPosition(dividerIndex, cachedDividerPositions[cachedDividerPositionIndex]);
					}
				}
			}
		});
		
		
		if(tabPane.getTabs().isEmpty()) {
			splitPane.setDividerPosition(dividerIndex, dividerClosePosition);
			borderPane.setMaxWidth(0);
			borderPane.setMaxHeight(0);
			borderPane.setVisible(false);
			borderPane.setManaged(false);
			
		}else {
			borderPane.setMaxWidth(BorderPane.USE_COMPUTED_SIZE);
			borderPane.setMaxHeight(BorderPane.USE_COMPUTED_SIZE);
			borderPane.setVisible(true);
			borderPane.setManaged(true);
			
			splitPane.setDividerPosition(dividerIndex, cachedDividerPositions[cachedDividerPositionIndex]);
		}
	}



	@Override
	public void receive(Object source, String methodName, Object... args) {
		if(source == AppStatus.get() && methodName.equals("setFocusedProject") && initialized)
			centerTabPane.getTabs().clear();
	}
}
