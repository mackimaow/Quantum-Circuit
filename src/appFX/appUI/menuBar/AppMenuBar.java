package appFX.appUI.menuBar;

import appFX.appPreferences.AppPreferences;
import appFX.appUI.MainScene;
import appFX.appUI.appViews.ConcreteView;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class AppMenuBar implements AppPreferences{
	
	public static void initializeMenuBar(MainScene mainScene) {
		ObservableList<Menu> menus = mainScene.getMenuBar().getMenus();
		Menu menu, subMenu;
		menu = new Menu("File"); {
			addItemToMenu(menu, mkItem("New Project", AppCommand.OPEN_NEW_PROJECT, null));
			addItemToMenu(menu, mkItem("Open Project", AppCommand.OPEN_PROJECT, KeyShortCuts.OPEN_PROJECT));
			addItemToMenu(menu, mkItem("Save as", AppCommand.SAVE_PROJECT_TO_FILESYSTEM, KeyShortCuts.SAVE_PROJECT_AS));
			addItemToMenu(menu, mkItem("Save", AppCommand.SAVE_PROJECT, KeyShortCuts.SAVE));
			addSeparator(menu);
			subMenu = new Menu("Export to"); {
				addItemToMenu(subMenu, mkItem("PNG Image", AppCommand.EXPORT_TO_PNG_IMAGE, null));
				addItemToMenu(subMenu, mkItem("QUIL", AppCommand.EXPORT_TO_QUIL, KeyShortCuts.EXPORT_QUIL));
				addItemToMenu(subMenu, mkItem("QASM", AppCommand.EXPORT_TO_QASM, KeyShortCuts.EXPORT_QASM));
				addItemToMenu(subMenu, mkItem("Quipper", AppCommand.EXPORT_TO_QUIPPER, KeyShortCuts.EXPORT_QUIPPER));
			} addItemToMenu(menu, subMenu);
			subMenu = new Menu("Load File"); {
				addItemToMenu(subMenu, mkItem("QUIL File", AppCommand.IMPORT_FROM_QUIL, null));
				addItemToMenu(subMenu, mkItem("QASM File", AppCommand.IMPORT_FROM_QASM, null));
				addItemToMenu(subMenu, mkItem("Quipper File", AppCommand.IMPORT_FROM_QUIPPER, null));
			} addItemToMenu(menu, subMenu);
			addSeparator(menu);
			addItemToMenu(menu, mkItem("Preferences", AppCommand.OPEN_USER_PREFERENCES, KeyShortCuts.PREFERENCES));
		} menus.add(menu);
		menu = new Menu("Edit"); {
			addItemToMenu(menu, mkItem("Undo", AppCommand.UNDO_FOCUSED_CB, KeyShortCuts.UNDO));
			addItemToMenu(menu, mkItem("Redo", AppCommand.REDO_FOCUSED_CB, KeyShortCuts.REDO));
			addItemToMenu(menu, mkItem("Reset Current Tool", AppCommand.RESET_CURRENT_TOOL, KeyShortCuts.RESET_CURRENT_TOOL));
			addItemToMenu(menu, mkItem("Add Row", AppCommand.ADD_ROW_TO_FOCUSED_CB, KeyShortCuts.ADD_ROW));
			addItemToMenu(menu, mkItem("Add Column", AppCommand.ADD_COLUMN_TO_FOCUSED_CB, KeyShortCuts.ADD_COLUMN));
			addItemToMenu(menu, mkItem("Remove Last Row", AppCommand.REMOVE_ROW_FROM_FOCUSED_CB, KeyShortCuts.REMOVE_LAST_ROW));
			addItemToMenu(menu, mkItem("Remove Last Column", AppCommand.REMOVE_COLUMN_FROM_FOCUSED_CB, KeyShortCuts.REMOVE_LAST_COLUMN));
		} menus.add(menu);
		menu = new Menu("Run"); {
			addItemToMenu(menu, mkItem("Run Simulation", AppCommand.RUN_SIMULATION, KeyShortCuts.RUN_SIMULATION));
			addItemToMenu(menu, mkItem("Run QUIL", AppCommand.RUN_QUIL, KeyShortCuts.RUN_QUIL));
			addItemToMenu(menu, mkItem("Run QASM", AppCommand.RUN_QASM, KeyShortCuts.RUN_QASM));
		} menus.add(menu);
		menu = new Menu("View"); {
			for( ConcreteView ctv : ConcreteView.values())
				addItemToMenu(menu, mkViewItem("Show " + ctv.getView().getName(), mainScene, ctv));
		} menus.add(menu);
		menu = new Menu("Help"); {
			//TODO: to be implemented
		} menus.add(menu);
	}
	
	private static void addSeparator(Menu menu) {
		addItemToMenu(menu, new SeparatorMenuItem());
	}
	
	private static void addItemToMenu(Menu menu, MenuItem menuItem) {
		ObservableList<MenuItem> items = menu.getItems();
		items.add(menuItem);
	}
	
	
	/**
	 * 
	 * @param label text to display
	 * @param appCommand the command to run when menu is activated (cannot be null)
	 * @param shortCut the keyboard shortcut to activate this item (can be null for no shortcut)
	 * @return 
	 */
	private static AppMenuItem mkItem(String label, AppCommand appCommand, KeyShortCuts shortCut) {
		AppMenuItem item = new AppMenuItem(shortCut);
		item.setText(label);
		item.setOnAction((event) -> {
			AppCommand.doAction(appCommand);
		});
		return item;
	}
	
	private static CheckMenuItem mkViewItem(String label, MainScene mainScene, ConcreteView view) {
		CheckMenuItem item = new CheckMenuItem(label);
		item.setOnAction(event -> {
			if(item.isSelected())
				mainScene.getViewManager().addView(view);
			else
				mainScene.getViewManager().removeView(view);
		});
		view.getView().setOnOpenCloseListener((wasAdded) -> {
			item.setSelected(wasAdded);
		});
		return item;
	}
	
	
	public static void refreshMenuShortCuts() {
		AppStatus status = AppStatus.get();
		ObservableList<Menu> menus = status.getMainScene().getMenuBar().getMenus();
		
		for(int i = 0; i < menus.size(); i++)
			applyShortCutChanges(menus.get(i));
	}
	
	private static void applyShortCutChanges(Menu menu) {
		ObservableList<MenuItem> menuItems = menu.getItems();
		
		MenuItem mi;
		for(int i = 0; i < menuItems.size(); i++) {
			mi = menuItems.get(i);
			
			if(mi instanceof Menu) {
				applyShortCutChanges((Menu) mi);
			}else if(mi instanceof AppMenuItem) {
				((AppMenuItem) mi).applyShortCut();
			}
		}
	}
	
	
	
	
	
	
}
