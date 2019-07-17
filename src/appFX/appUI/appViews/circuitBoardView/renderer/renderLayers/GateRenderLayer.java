package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appFX.framework.solderedGates.SolderedGate;
import graphicsWrapper.AxisBound.GridBound;
import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableArray;

public final class GateRenderLayer extends RenderLayer {
	public static int GRID_SIZE = 50;
	
	private CircuitBoardModel circuitBoard;
	
	public GateRenderLayer(double width, double height, CircuitBoardModel circuitBoard) {
		super(width, height);
		this.circuitBoard = circuitBoard;
	}
	
	@Override
	public void onDraw(Graphics<Image, Font, Color> graphics, Object ... userArgs) {
		Project p = AppStatus.get().getFocusedProject();
		graphics.setFont(CustomFXGraphics.DEFAULT, 7);
		for(RawExportableGateData rawGateData : circuitBoard) {
			int column = rawGateData.getColumn();
			String name = rawGateData.getSolderedGate().getGateModelFormalName();
			if(name.equals("Identity.gm")) {
				renderIdentity(graphics, rawGateData.getGateRowBodyStart(), column);
				continue;
			}
			GateModel gm = p.getGateModel(name);
			
			if(gm == null) {
				renderUnknownGate(graphics, rawGateData, name, column);
			} else {
				if(gm.isPreset())
					renderPresetGate(graphics, rawGateData, (PresetGateModel) gm, column);
				else
					renderGate(graphics, rawGateData, gm, column);
			}
		}
	}

	public static void renderUnknownGate(Graphics<Image, Font, Color> graphics, RawExportableGateData rawData, String name, int column) {
		ListIterator<Control> controlsWithinBodyIterator = renderOutOfBodyControls(graphics, rawData, column);
		renderBody(graphics, rawData, name, controlsWithinBodyIterator, column, true);
	}
	
	public static void renderGate(Graphics<Image, Font, Color> graphics, RawExportableGateData rawData, GateModel gm, int column) {
		ListIterator<Control> controlsWithinBodyIterator = renderOutOfBodyControls(graphics, rawData, column);
		renderBody(graphics, rawData, gm.getSymbol(), controlsWithinBodyIterator, column, false);
	}
	
	public static ListIterator<Control> renderOutOfBodyControls(Graphics<Image,Font,Color> graphics, RawExportableGateData rawData, int column) {
		LinkedList<Control> controls = rawData.getControls();
		
		int bodyEnd = rawData.getGateRowBodyEnd();
		int spaceEnd = rawData.getGateRowSpaceEnd();
		
		ListIterator<Control> controlsIterator = controls.listIterator(controls.size());
		
		if(bodyEnd != spaceEnd) {
			drawVerticalLine(graphics, bodyEnd, spaceEnd, column);
			while(controlsIterator.hasPrevious()) {
				Control currentControl = controlsIterator.previous();
				int reg = currentControl.getRegister();
				if(reg > bodyEnd)
					renderControl(graphics, reg, column, currentControl.getControlStatus());
				else break;
			}
		}
		
		int spaceStart = rawData.getGateRowSpaceStart();
		int bodyStart = rawData.getGateRowBodyStart();
		
		controlsIterator = controls.listIterator();
		
		if(spaceStart != bodyStart) {
			drawVerticalLine(graphics, spaceStart, bodyStart, column);
			while(controlsIterator.hasNext()) {
				Control currentControl = controlsIterator.next();
				int reg = currentControl.getRegister();
				if(reg < bodyStart) {
					renderControl(graphics, reg, column, currentControl.getControlStatus());
				} else {
					controlsIterator.previous();
					break;
				}
			}
		}
		return controlsIterator;
	}
	
	
	public static void renderBody(Graphics<Image,Font,Color> graphics, RawExportableGateData rawData, String gateSymbol, ListIterator<Control> controlsIterator, int column, boolean unknownName) {
		int rowStart = rawData.getGateRowBodyStart();
		int rowEnd = rawData.getGateRowBodyEnd();
		
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setFocus(new GridBound(column), new GridBound(rowStart, rowEnd + 1), GRID_SIZE, GRID_SIZE * (rowEnd - rowStart + 1)); {
			graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				int bodyColumnGrid = 0;
				
				if(checkIfControlIsInBody(controlsIterator, rowStart, rowEnd)) {
					graphics.setFocus(new GridBound(0), new RimBound(0, 0, true, true)); {
						RimBound horizontalRimBound = new RimBound(10, 0, false, false);
						while(controlsIterator.hasNext()) {
							Control control = controlsIterator.next();
							int reg = control.getRegister();
							if(reg <= rowEnd) {
								GridBound verticalGridBound = new GridBound(reg, reg + 1, 3);
								graphics.setFocus(horizontalRimBound, verticalGridBound); {
									renderControlInFocus(graphics, control.getControlStatus());
								} graphics.escapeFocus();
							} else {
								break;
							}
						}
					} graphics.escapeFocus();
					bodyColumnGrid = 1;
				}
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				graphics.setColor(Color.BLACK);

				Hashtable<Integer, Integer> regs = rawData.getRegisters();
				if(regs.size() > 1) {
					graphics.setFontHeight(7);
					graphics.setFocus(new GridBound(0), new RimBound(0, 0, true, true)); {
						RimBound horizontalRimBound = new RimBound(10, 0, false, false);
						for(int i = 0; i < regs.size(); i++) {
							int reg = regs.get(i);
							GridBound verticalGridBound = new GridBound(reg, reg + 1, 3);
							graphics.setFocus(horizontalRimBound, verticalGridBound); {
								graphics.drawText(Integer.toString(i), 0, 0);
							} graphics.escapeFocus();
						}
					} graphics.escapeFocus();
					bodyColumnGrid = 1;
				}
				graphics.setFocus(new GridBound(bodyColumnGrid), new RimBound(0, 0, true, true)); {
					graphics.setFontHeight(15);
					graphics.setFocus(new RimBound(10, 10, true, true), new RimBound(3, 3, true, true)); {
						SolderedGate sg = rawData.getSolderedGate();
						
						ImmutableArray<String> paramLatex = sg.getParameterSet().getLatexRepresentations();
						
						String paramString = gateSymbol;
						
						if(!paramLatex.isEmpty()) {
							paramString += "\\( ( " + paramLatex.get(0);
							
							for(int i = 1; i < paramLatex.size(); i++)
								paramString += " , " + paramLatex.get(i);
							
							paramString += " ) \\)";
						}
						graphics.setColor(unknownName? Color.RED : Color.BLACK);
						graphics.drawLatex(paramString, 0, 0);
					} graphics.escapeFocus();
				} graphics.escapeFocus();
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	
	public static boolean checkIfControlIsInBody(ListIterator<Control> iterator, int rowBodyStart, int rowBodyEnd) {
		if(!iterator.hasNext()) return false;
		Control control = iterator.next();
		int reg = control.getRegister();
		boolean isInBound = reg >= rowBodyStart && reg <= rowBodyEnd;
		iterator.previous();
		return isInBound;
	}
	
	
	public static void renderPresetGate(Graphics<Image, Font, Color> graphics, RawExportableGateData rawData, PresetGateModel gm, int column) {
		Hashtable<Integer, Integer> regs = rawData.getRegisters();
		switch (gm.getPresetGateType()) {
		case CNOT:
			renderAllControls(graphics, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderControl(graphics, regs.get(0), column, Control.CONTROL_TRUE);
			renderCNotHead(graphics, regs.get(1), column);
			break;
		case SWAP:
			renderAllControls(graphics, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderSwapHead(graphics, regs.get(0), column);
			renderSwapHead(graphics, regs.get(1), column);
			break;
		case TOFFOLI:
			renderAllControls(graphics, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderControl(graphics, regs.get(0), column, Control.CONTROL_TRUE);
			renderSwapHead(graphics, regs.get(1), column);
			renderSwapHead(graphics, regs.get(2), column);
			break;
		case MEASUREMENT:
			renderMeasurementGate(graphics, rawData, column);
			break;
		default:
			renderGate(graphics, rawData, gm, column);
			break;
		}
	}
	
	public static void renderMeasurementGate(Graphics<Image, Font, Color> graphics, RawExportableGateData rawData, int column) {
		renderOutOfBodyControls(graphics, rawData, column);
		Hashtable<Integer, Integer> regs = rawData.getRegisters();
		graphics.setFocus(new GridBound(column), new GridBound(regs.get(0)), GRID_SIZE, GRID_SIZE); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			
			graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				
				graphics.setFocus(new RimBound(5, 5, false, false), new RimBound(0, 0, false, false)); {
					graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
					graphics.drawArc(0, 5, GRID_SIZE * .4d, GRID_SIZE * .4d, 0, 180);
					graphics.drawLine(GRID_SIZE * .2d, 2, GRID_SIZE * .2d, GRID_SIZE * .4d - 7, false);
				} graphics.escapeFocus();
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	
	public static void drawVerticalLine(Graphics<Image, Font, Color> graphics, int row, int row2, int column) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setColor(Color.BLACK);
		graphics.setLineWidth(1);
		if(row2 - row > 1) {
			graphics.setFocus(new GridBound(column), new GridBound(row + 1, row2), GRID_SIZE, GRID_SIZE * (row2 - row - 1)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
		}
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE, false, true); {
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(1)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
		} graphics.escapeFocus();
				
		graphics.setFocus(new GridBound(column), new GridBound(row2), GRID_SIZE, GRID_SIZE, false, true); {
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(0)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(1)); {
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	public static void renderAllControls(Graphics<Image, Font, Color> graphics, LinkedList<Control> controls, int spaceStart, int spaceEnd, int column) {
		drawVerticalLine(graphics, spaceStart, spaceEnd, column);
		for(Control control : controls)
			renderControl(graphics, control.getRegister(), column, control.getControlStatus());
	}
	
	public static void renderControl(Graphics<Image, Font, Color> graphics, int row, int column, boolean controlType) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
			renderControlInFocus(graphics, controlType);
		} graphics.escapeFocus();
	}
	
	public static void renderCNotHead(Graphics<Image, Font, Color> graphics, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row)); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.setColor(Color.BLACK);
			graphics.drawOval(0, 0, GRID_SIZE / 2, GRID_SIZE / 2);
			graphics.drawLine(0, 0, GRID_SIZE / 2, 0, true);
			graphics.drawLine(0, 0, 0, GRID_SIZE / 2, true);
		} graphics.escapeFocus();
	}
	
	public static void renderSwapHead(Graphics<Image, Font, Color> graphics, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row)); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.setColor(Color.BLACK);
			graphics.drawLine(0, 0, GRID_SIZE / 2, GRID_SIZE / 2, true);
			graphics.drawLine(0, 0, GRID_SIZE / 2, GRID_SIZE / 2, false);
		} graphics.escapeFocus();
	}
	
	public static void renderControlInFocus(Graphics<Image, Font, Color> graphics, boolean controlType) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setLineWidth(1);
		if(controlType == Control.CONTROL_TRUE) {
			graphics.setColor(Color.BLACK);
			graphics.fillOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
		} else {
			graphics.setColor(Color.WHITE);
			graphics.fillOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
			graphics.setColor(Color.BLACK);
			graphics.drawOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
		}
	}
	
	
	public static void renderIdentity(Graphics<Image, Font, Color> graphics, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
		} graphics.escapeFocus();
	}
	
}
