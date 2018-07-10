package appTools;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import appUI.Window;
import utils.ResourceLoader;

@SuppressWarnings("serial")
public class AppToolBar extends JToolBar{
	private ButtonGroup toolGroup = new ButtonGroup();
	private Window w;
	private Tool selected = new SelectionTool(w, new ImageIcon(ResourceLoader.SELECT));
	
	public AppToolBar(Window w) {
		super();
		this.w = w;
		
		setFloatable(false);
		
//		Add Tools Here:
		addTool(selected);
		addTool(new SolderingTool(w, new ImageIcon(ResourceLoader.SOLDER)));
		addTool(new EditTool(w, new ImageIcon(ResourceLoader.EDIT)));
		
		
		((JToggleButton) getComponent(0)).setSelected(true);
	}
	
	private void addTool(Tool tool) {
		JToggleButton button = new JToggleButton(tool.getIcon());
		button.setToolTipText(tool.getName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Component comp = w.getDisplay();
				
				comp.removeMouseListener(selected);
				comp.removeMouseMotionListener(selected);
				comp.removeMouseWheelListener(selected);
				comp.removeKeyListener(selected);
				selected.onUnselected();
				
				selected = tool;
				
				comp.addMouseListener(tool);
				comp.addMouseMotionListener(tool);
				comp.addMouseWheelListener(tool);
				comp.addKeyListener(tool);
				tool.onSelected();
			}
		});
		toolGroup.add(button);
		add(button);
	}
	
}
