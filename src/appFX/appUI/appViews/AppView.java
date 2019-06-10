package appFX.appUI.appViews;

import appFX.appUI.AppFXMLComponent;
import appFX.appUI.AppTab;
import appFX.framework.AppStatus;
import utils.customCollections.eventTracableCollections.Notifier.ReceivedEvent;

public abstract class AppView extends AppFXMLComponent implements ReceivedEvent {
	
	public static enum ViewLayout {
		CENTER, LEFT, RIGHT, BOTTOM;
	}
	
	private final ViewLayout layout;
	private final AppTab appTab;
	private final boolean updateInBackground;
	private AppViewOnOpenCloseListener openCloseListener;
	
	
	public AppView(String fxmlFilename, String viewName, ViewLayout layout) {
		this(fxmlFilename, viewName, layout, false);
	}
	
	public AppView(String fxmlFilename, String viewName, ViewLayout layout, boolean updateInBackground) {
		super(fxmlFilename);
		this.layout = layout;
		this.updateInBackground = updateInBackground;
		this.appTab = new AppTab(viewName, this);
		appTab.setContent(loadAsNode());
		appTab.setOnCloseRequest((event) -> {
			if(!onCloseRequest())
				event.consume();
			else
				if(!updateInBackground)
					AppStatus.get().removeAppChangedListener(this);
		});
		if(updateInBackground)
			AppStatus.get().addAppChangedListener(this);
	}
	
	// overridable
	public boolean onCloseRequest() {
		return true;
	}
	
	// overridable
	public boolean onOpenRequest() { return true; }
	
	public void setOnOpenCloseListener(AppViewOnOpenCloseListener openCloseListener) {
		this.openCloseListener = openCloseListener;
		appTab.setOnClosed((event) -> {openCloseListener.appTabOpenClose(false);});
	}
	
	public AppViewOnOpenCloseListener getOnOpenCloseListener() {
		return openCloseListener;
	}
	
	
	public static interface AppViewOnOpenCloseListener {
		public void appTabOpenClose(boolean isOpening);
	}
	
	
	public ViewLayout getLayout() {
		return layout;
	}
	
	public AppTab getTab() {
		return appTab;
	}
	
	public String getName() {
		return appTab.getText();
	}
	
	public boolean isUpdatedInBackground() {
		return updateInBackground;
	}
	
	public void closeView() {
		AppStatus.get().getMainScene().getViewManager().removeView(this);
	}
	
}
