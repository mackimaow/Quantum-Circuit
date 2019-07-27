package appFX.appUI.appViews.circuitBoardView.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;

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
import graphicsWrapper.CompiledGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.GraphicalBluePrint;
import graphicsWrapper.Graphics;
import utils.customCollections.immutableLists.ImmutableArray;

public class GateRenderer {
	
	public static final double GRID_SIZE = 50;
	
	public static BufferedImage getCircuitBoardImage(CircuitBoardModel circuitBoard, double zoom) {
		CompiledGraphics<Image, Font, Color> compiledGateGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.drawCircuitBoard(graphics, CustomSWTGraphics.RENDER_PALETTE, circuitBoard);
			}
		});
		FocusData gridData = compiledGateGraphics.getFocusData().getElement();
		CompiledGraphics<Image, Font, Color> compiledQubitLineGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.renderQubitLines(graphics, CustomSWTGraphics.RENDER_PALETTE, gridData);
			}
		});
		CompiledGraphics<Image, Font, Color> compiledQubitRegsGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.renderQubitRegs(graphics, CustomSWTGraphics.RENDER_PALETTE, gridData, false);
			}
		});
		int width  = (int) Math.round((GRID_SIZE + gridData.getWidth()) * zoom);
		int height = (int) Math.round(gridData.getHeight() * zoom);
		
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		CustomSWTGraphics customGraphics = new CustomSWTGraphics((Graphics2D) bimg.getGraphics());
		Graphics.graphicsDraw(0,  0, zoom, compiledQubitRegsGraphics, customGraphics);
		Graphics.graphicsDraw(GRID_SIZE,  0, zoom, compiledQubitLineGraphics, customGraphics);
		Graphics.graphicsDraw(GRID_SIZE,  0, zoom, compiledGateGraphics, customGraphics);
		
		return bimg;
	}
	
	public static <ImageType, FontType, ColorType> void drawCircuitBoard(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, CircuitBoardModel circuitBoard) {
		Project p = AppStatus.get().getFocusedProject();
		graphics.setFont(palette.getDefault(), 7);
		for(RawExportableGateData rawGateData : circuitBoard) {
			int column = rawGateData.getColumn();
			String name = rawGateData.getSolderedGate().getGateModelFormalName();
			if(name.equals("Identity.gm")) {
				renderIdentity(graphics, rawGateData.getGateRowBodyStart(), column);
				continue;
			}
			GateModel gm = p.getGateModel(name);
			
			if(gm == null) {
				renderUnknownGate(graphics, palette, rawGateData, name, column);
			} else {
				if(gm.isPreset())
					renderPresetGate(graphics, palette, rawGateData, (PresetGateModel) gm, column);
				else
					renderGate(graphics, palette, rawGateData, gm, column);
			}
		}
	}
	
	
	public static <ImageType, FontType, ColorType> void renderUnknownGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, String name, int column) {
		ListIterator<Control> controlsWithinBodyIterator = renderOutOfBodyControls(graphics, palette, rawData, column);
		renderBody(graphics, palette, rawData, name, controlsWithinBodyIterator, column, true);
	}
	
	public static <ImageType, FontType, ColorType> void renderGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, GateModel gm, int column) {
		ListIterator<Control> controlsWithinBodyIterator = renderOutOfBodyControls(graphics, palette, rawData, column);
		renderBody(graphics, palette, rawData, gm.getSymbol(), controlsWithinBodyIterator, column, false);
	}
	
	public static <ImageType, FontType, ColorType> ListIterator<Control> renderOutOfBodyControls(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, int column) {
		LinkedList<Control> controls = rawData.getControls();
		
		int bodyEnd = rawData.getGateRowBodyEnd();
		int spaceEnd = rawData.getGateRowSpaceEnd();
		
		ListIterator<Control> controlsIterator = controls.listIterator(controls.size());
		
		if(bodyEnd != spaceEnd) {
			drawVerticalLine(graphics, palette, bodyEnd, spaceEnd, column);
			while(controlsIterator.hasPrevious()) {
				Control currentControl = controlsIterator.previous();
				int reg = currentControl.getRegister();
				if(reg > bodyEnd)
					renderControl(graphics, palette, reg, column, currentControl.getControlStatus());
				else break;
			}
		}
		
		int spaceStart = rawData.getGateRowSpaceStart();
		int bodyStart = rawData.getGateRowBodyStart();
		
		controlsIterator = controls.listIterator();
		
		if(spaceStart != bodyStart) {
			drawVerticalLine(graphics, palette, spaceStart, bodyStart, column);
			while(controlsIterator.hasNext()) {
				Control currentControl = controlsIterator.next();
				int reg = currentControl.getRegister();
				if(reg < bodyStart) {
					renderControl(graphics, palette, reg, column, currentControl.getControlStatus());
				} else {
					controlsIterator.previous();
					break;
				}
			}
		}
		return controlsIterator;
	}
	
	
	public static <ImageType, FontType, ColorType> void renderBody(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, String gateSymbol, ListIterator<Control> controlsIterator, int column, boolean unknownName) {
		int rowStart = rawData.getGateRowBodyStart();
		int rowEnd = rawData.getGateRowBodyEnd();
		
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setFocus(new GridBound(column), new GridBound(rowStart, rowEnd + 1), GRID_SIZE, GRID_SIZE * (rowEnd - rowStart + 1)); {
			graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
				graphics.setColor(palette.getWhite());
				graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.setColor(palette.getBlack());
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
									renderControlInFocus(graphics, palette, control.getControlStatus());
								} graphics.escapeFocus();
							} else {
								break;
							}
						}
					} graphics.escapeFocus();
					bodyColumnGrid = 1;
				}
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				graphics.setColor(palette.getBlack());

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
						graphics.setColor(unknownName? palette.getRed() : palette.getBlack());
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
	
	
	public static <ImageType, FontType, ColorType> void renderPresetGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, PresetGateModel gm, int column) {
		Hashtable<Integer, Integer> regs = rawData.getRegisters();
		switch (gm.getPresetGateType()) {
		case CNOT:
			renderAllControls(graphics, palette, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderControl(graphics, palette, regs.get(0), column, Control.CONTROL_TRUE);
			renderCNotHead(graphics, palette, regs.get(1), column);
			break;
		case SWAP:
			renderAllControls(graphics, palette, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderSwapHead(graphics, palette, regs.get(0), column);
			renderSwapHead(graphics, palette, regs.get(1), column);
			break;
		case TOFFOLI:
			renderAllControls(graphics, palette, rawData.getControls(), rawData.getGateRowSpaceStart(), rawData.getGateRowSpaceEnd(), column);
			renderControl(graphics, palette, regs.get(0), column, Control.CONTROL_TRUE);
			renderSwapHead(graphics, palette, regs.get(1), column);
			renderSwapHead(graphics, palette, regs.get(2), column);
			break;
		case MEASUREMENT:
			renderMeasurementGate(graphics, palette, rawData, column);
			break;
		default:
			renderGate(graphics, palette, rawData, gm, column);
			break;
		}
	}
	
	public static <ImageType, FontType, ColorType> void renderMeasurementGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, int column) {
		renderOutOfBodyControls(graphics, palette, rawData, column);
		Hashtable<Integer, Integer> regs = rawData.getRegisters();
		graphics.setFocus(new GridBound(column), new GridBound(regs.get(0)), GRID_SIZE, GRID_SIZE); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			
			graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
				graphics.setColor(palette.getWhite());
				graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.setColor(palette.getBlack());
				graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				
				graphics.setFocus(new RimBound(5, 5, false, false), new RimBound(0, 0, false, false)); {
					graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
					graphics.drawArc(0, 5, GRID_SIZE * .4d, GRID_SIZE * .4d, 0, 180);
					graphics.drawLine(GRID_SIZE * .2d, 2, GRID_SIZE * .2d, GRID_SIZE * .4d - 7, false);
				} graphics.escapeFocus();
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	
	public static <ImageType, FontType, ColorType> void drawVerticalLine(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int row2, int column) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setColor(palette.getBlack());
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
	
	public static <ImageType, FontType, ColorType> void renderAllControls(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, LinkedList<Control> controls, int spaceStart, int spaceEnd, int column) {
		drawVerticalLine(graphics, palette, spaceStart, spaceEnd, column);
		for(Control control : controls)
			renderControl(graphics, palette, control.getRegister(), column, control.getControlStatus());
	}
	
	public static <ImageType, FontType, ColorType> void renderControl(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column, boolean controlType) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
			renderControlInFocus(graphics, palette, controlType);
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderCNotHead(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row)); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.setColor(palette.getBlack());
			graphics.drawOval(0, 0, GRID_SIZE / 2, GRID_SIZE / 2);
			graphics.drawLine(0, 0, GRID_SIZE / 2, 0, true);
			graphics.drawLine(0, 0, 0, GRID_SIZE / 2, true);
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderSwapHead(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row)); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.setColor(palette.getBlack());
			graphics.drawLine(0, 0, GRID_SIZE / 2, GRID_SIZE / 2, true);
			graphics.drawLine(0, 0, GRID_SIZE / 2, GRID_SIZE / 2, false);
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderControlInFocus(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, boolean controlType) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setLineWidth(1);
		if(controlType == Control.CONTROL_TRUE) {
			graphics.setColor(palette.getBlack());
			graphics.fillOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
		} else {
			graphics.setColor(palette.getWhite());
			graphics.fillOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
			graphics.setColor(palette.getBlack());
			graphics.drawOval(0, 0, GRID_SIZE / 6, GRID_SIZE / 6);
		}
	}
	
	
	public static <ImageType, FontType, ColorType> void renderIdentity(Graphics<ImageType, FontType, ColorType> graphics, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
		} graphics.escapeFocus();
	}
	
	
	public static <ImageType, FontType, ColorType> void renderQubitLines(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, FocusData gridData) {
		graphics.setBoundsManaged(false);
		graphics.setColor(palette.getBlack());

		for(int i = 0; i < gridData.getRowCount(); i++) {
			setFocusQubitLine(graphics, gridData, i); {
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				graphics.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
			} graphics.escapeFocus();
		}
	}
	
	public static <ImageType, FontType, ColorType> void renderQubitRegs(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, FocusData gridData, boolean renderBackColor) {
		graphics.setBoundsManaged(false);
		graphics.setFont(palette.getDefault(), 15);
		
		if(renderBackColor) {
			for(int r = 0; r < gridData.getRowCount(); r++) {
				setFocusQubitRegister(graphics, gridData, r); {
					graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
					graphics.setColor(palette.getColor(1d, 1d, 1d, .75d));
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setColor(palette.getBlack());
					graphics.drawLatex("\\(\\vert\\Psi_{" + r + "}\\rangle\\)", 0, 0);
				} graphics.escapeFocus();
			}
		} else {
			graphics.setColor(palette.getBlack());
			for(int r = 0; r < gridData.getRowCount(); r++) {
				setFocusQubitRegister(graphics, gridData, r); {
					graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
					graphics.drawLatex("\\(\\vert\\Psi_{" + r + "}\\rangle\\)", 0, 0);
				} graphics.escapeFocus();
			}
		}
	}
	
	private static <ImageType, FontType, ColorType> void setFocusQubitLine(Graphics<ImageType, FontType, ColorType> graphics, FocusData gridData, int row) {
		double lowerMargin = row == 0? 0 : gridData.getCummulativeHeight(row - 1);
		double width = gridData.getWidth();
		double height = gridData.getRowHeightAt(row);
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
		graphics.setFocus(new RimBound(0, 0, true, true), new RimBound(lowerMargin, 0, false, false), width, height);
	}
	
	private static <ImageType, FontType, ColorType> void setFocusQubitRegister(Graphics<ImageType, FontType, ColorType> graphics, FocusData gridData, int row) {
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
		double topMargin = row == 0? 0d : gridData.getCummulativeHeight(row - 1);
		double height = gridData.getRowHeightAt(row);
		graphics.setFocus(new RimBound(0, 0, false, false), new RimBound(topMargin, 0, false, false), GateRenderer.GRID_SIZE, height);
	}
	
}
