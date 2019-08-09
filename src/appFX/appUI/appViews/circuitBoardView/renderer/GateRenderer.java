package appFX.appUI.appViews.circuitBoardView.renderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.RawExportableGateData;
import appFX.framework.exportGates.RawExportableGateData.RawExportControl;
import appFX.framework.exportGates.RawExportableGateData.RawExportLink;
import appFX.framework.exportGates.RawExportableGateData.RawExportRegister;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.CircuitBoardModel.RowType;
import appFX.framework.gateModels.CircuitBoardModel.RowTypeElement;
import appFX.framework.gateModels.CircuitBoardModel.RowTypeList;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import appFX.framework.solderedGates.SolderedGate;
import graphicsWrapper.AxisBound.GridBound;
import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.CompiledGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.GraphicalBluePrint;
import graphicsWrapper.Graphics;
import utils.customCollections.Pair;
import utils.customCollections.immutableLists.ImmutableArray;

public class GateRenderer {
	
	public static final double GRID_SIZE = 50;
	public static final double QUBIT_REGS_SIZE = GRID_SIZE * 1.6d;
	
	public static BufferedImage getCircuitBoardImage(CircuitBoardModel circuitBoard, double zoom) {
		CompiledGraphics<Image, Font, Color> compiledGateGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.drawCircuitBoard(graphics, CustomSWTGraphics.RENDER_PALETTE, circuitBoard);
			}
		});
		FocusData gridData = compiledGateGraphics.getFocusData().getElement();
		RowTypeList rowTypes = circuitBoard.getCopyOfRowTypeList();
		CompiledGraphics<Image, Font, Color> compiledQubitLineGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.renderQubitLines(graphics, CustomSWTGraphics.RENDER_PALETTE, gridData, rowTypes);
			}
		});
		CompiledGraphics<Image, Font, Color> compiledQubitRegsGraphics = Graphics.compileGraphicalBluePrint(new GraphicalBluePrint<Image, Font, Color>() {
			@Override
			public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
				GateRenderer.renderQubitRegs(graphics, CustomSWTGraphics.RENDER_PALETTE, gridData, rowTypes, false);
			}
		});
		int width  = (int) Math.round((GRID_SIZE + gridData.getWidth()) * zoom);
		int height = (int) Math.round(gridData.getHeight() * zoom);
		
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		CustomSWTGraphics customGraphics = new CustomSWTGraphics((Graphics2D) bimg.getGraphics());
		Graphics.graphicsDraw(0,  0, zoom, compiledQubitRegsGraphics, customGraphics);
		Graphics.graphicsDraw(QUBIT_REGS_SIZE,  0, zoom, compiledQubitLineGraphics, customGraphics);
		Graphics.graphicsDraw(QUBIT_REGS_SIZE,  0, zoom, compiledGateGraphics, customGraphics);
		
		return bimg;
	}
	
	public static <ImageType, FontType, ColorType> void drawCircuitBoard(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, CircuitBoardModel circuitBoard) {
		Project p = AppStatus.get().getFocusedProject();
		graphics.setFont(palette.getDefault(), 7);
		graphics.setLineWidth(1d);
		String identityLocationString = PresetGateType.IDENTITY.getModel().getLocationString();
		for(RawExportableGateData rawGateData : circuitBoard) {
			int column = rawGateData.getColumn();
			String locationString = rawGateData.getSolderedGate().getGateModelLocationString();
			if(locationString.equals(identityLocationString)) {
				try {
					renderIdentity(graphics, rawGateData.getGateRowBodyStart(), column);
				} catch(Exception e) {
					System.out.println(rawGateData.getGateRowBodyStart());
				}
				continue;
			}
			GateModel gm = p.getGateModel(locationString);
			
			if(gm == null) {
				renderUnknownGate(graphics, palette, rawGateData, locationString, column);
			} else {
				if(gm.isPreset())
					renderPresetGate(graphics, palette, rawGateData, (PresetGateModel) gm, column);
				else
					renderGate(graphics, palette, rawGateData, gm, column);
			}
		}
	}
	
	
	public static <ImageType, FontType, ColorType> void renderUnknownGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, String name, int column) {
		renderGate(graphics, palette, rawData, name, column, true);
	}
	
	public static <ImageType, FontType, ColorType> void renderGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, GateModel gm, int column) {
		renderGate(graphics, palette, rawData, gm.getSymbol(), column, false);
	}
	
	
	public static <ImageType, FontType, ColorType> Pair<ListIterator<RawExportControl>,ListIterator<RawExportControl>>  
		renderOutOfBodyControls(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, int column, int focusGroupsAbove) {
		
		LinkedList<RawExportControl> classicalControls = rawData.getClassicalControls();
		LinkedList<RawExportControl> quantumControls = rawData.getQuantumControls();
		
		int[] controlBounds = rawData.getControlRowBounds();
		
		int bodyStart = rawData.getGateRowBodyStart();
		int bodyEnd = rawData.getGateRowBodyEnd();
		
		int controlLineStart;
		int controlLineEnd;
		if(controlBounds != null) {
			controlLineStart = controlBounds[0] < bodyStart? controlBounds[0] : bodyStart;
			controlLineEnd = controlBounds[1] > bodyEnd? controlBounds[1] : bodyEnd;
		} else {
			controlLineStart = bodyStart;
			controlLineEnd = bodyEnd;
		}
		
		ListIterator<RawExportControl> quantumControlIterator = quantumControls.listIterator(quantumControls.size());
		ListIterator<RawExportControl> classicalControlIterator = classicalControls.listIterator(classicalControls.size());
		
		if(bodyEnd != controlLineEnd) {
			drawVerticalLine(graphics, palette, bodyEnd, controlLineEnd, column, focusGroupsAbove);
			while(quantumControlIterator.hasPrevious()) {
				RawExportControl currentControl = quantumControlIterator.previous();
				int row = currentControl.row;
				if(row > bodyEnd)
					renderControl(graphics, palette, row, column, currentControl.controlStatus, focusGroupsAbove);
				else break;
			}
			while(classicalControlIterator.hasPrevious()) {
				RawExportControl currentControl = classicalControlIterator.previous();
				int row = currentControl.row;
				if(row > bodyEnd)
					renderControl(graphics, palette, row, column, currentControl.controlStatus, focusGroupsAbove);
				else break;
			}
		}
		
		quantumControlIterator = quantumControls.listIterator();
		classicalControlIterator = classicalControls.listIterator();
		
		if(controlLineStart != bodyStart) {
			drawVerticalLine(graphics, palette, controlLineStart, bodyStart, column, focusGroupsAbove);
			while(quantumControlIterator.hasNext()) {
				RawExportControl currentControl = quantumControlIterator.next();
				int row = currentControl.row;
				if(row < bodyStart) {
					renderControl(graphics, palette, row, column, currentControl.controlStatus, focusGroupsAbove);
				} else {
					quantumControlIterator.previous();
					break;
				}
			}
			
			while(classicalControlIterator.hasNext()) {
				RawExportControl currentControl = classicalControlIterator.next();
				int row = currentControl.row;
				if(row < bodyStart) {
					renderControl(graphics, palette, row, column, currentControl.controlStatus, focusGroupsAbove);
				} else {
					classicalControlIterator.previous();
					break;
				}
			}
		}
		
		return new Pair<>(classicalControlIterator, quantumControlIterator);
	}
	
	
	public static <ImageType, FontType, ColorType> void renderGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, 
			RawExportableGateData rawData, String gateSymbol, int column, boolean unknownName) {
		int rowStart = rawData.getGateRowBodyStart();
		int rowEnd = rawData.getGateRowBodyEnd();
		
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setFocus(new GridBound(column), new GridBound(rowStart, rowEnd + 1), GRID_SIZE, GRID_SIZE * (rowEnd - rowStart + 1)); {
			graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
				GateBodyRender renderCenter = (quantumControlIterator, classicalControlIterator)->renderBodyCenter(graphics, palette, rawData, gateSymbol, quantumControlIterator, classicalControlIterator, unknownName);
				renderLinksAndControlsAroundGate(graphics, palette, rawData, renderCenter);
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	private static <ImageType, FontType, ColorType> void renderBodyCenter(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette,
			RawExportableGateData rawData, String gateSymbol, ListIterator<RawExportControl> classicalControlIterator, ListIterator<RawExportControl> quantumControlIterator, 
			 boolean unknownName) {
		int rowStart = rawData.getGateRowBodyStart();
		int rowEnd = rawData.getGateRowBodyEnd();
		graphics.setColor(palette.getWhite());
		graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		graphics.setColor(palette.getBlack());
		graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		int bodyColumnGrid = 0;
		
		if(checkIfControlIsInBody(classicalControlIterator, rowStart, rowEnd) || checkIfControlIsInBody(quantumControlIterator, rowStart, rowEnd)) {
			graphics.setFocus(new GridBound(0), new RimBound(0, 0, true, true)); {
				RimBound horizontalRimBound = new RimBound(10, 0, false, false);
				while(classicalControlIterator.hasNext()) {
					RawExportControl control = classicalControlIterator.next();
					int row = control.row;
					if(row <= rowEnd) {
						GridBound verticalGridBound = new GridBound(row, row + 1, 4);
						graphics.setFocus(horizontalRimBound, verticalGridBound); {
							renderControlInFocus(graphics, palette, control.controlStatus);
						} graphics.escapeFocus();
					} else {
						break;
					}
				}
				while(quantumControlIterator.hasNext()) {
					RawExportControl control = quantumControlIterator.next();
					int row = control.row;
					if(row <= rowEnd) {
						GridBound verticalGridBound = new GridBound(row, row + 1, 4);
						graphics.setFocus(horizontalRimBound, verticalGridBound); {
							renderControlInFocus(graphics, palette, control.controlStatus);
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

		Hashtable<Integer, RawExportRegister> regs = rawData.getRegisters();
		if(regs.size() > 1) {
			graphics.setFontHeight(7);
			graphics.setFocus(new GridBound(0), new RimBound(0, 0, true, true)); {
				RimBound horizontalRimBound = new RimBound(10, 0, false, false);
				for(int i = 0; i < regs.size(); i++) {
					int row = regs.get(i).row;
					GridBound verticalGridBound = new GridBound(row, row + 1, 4);
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
				
				String paramString = "\\( \\text{" + gateSymbol + "}";
				
				if(!paramLatex.isEmpty()) {
					paramString += "( " + paramLatex.get(0);
					
					for(int i = 1; i < paramLatex.size(); i++)
						paramString += " , " + paramLatex.get(i);
					
					paramString += " ) ";
				}
				paramString +=  "\\)";
				graphics.setColor(unknownName? palette.getRed() : palette.getBlack());
				graphics.drawLatex(paramString, 0, 0);
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderLinksAndControlsAroundGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette,
			RawExportableGateData rawData, GateBodyRender gateBodyRender) {
		int rowEnd = rawData.getGateRowBodyEnd();
		
		int localGridColumn = 0;
		if(rawData.hasInputLinks()) {
			graphics.setFocus(new GridBound(localGridColumn++), new GridBound(rowEnd, rowEnd + 1, 2), GRID_SIZE / 5, 0, false, true); {
				int[] bounds = rawData.getInputLinksRowBounds();
				renderLinks(graphics, palette, rowEnd, rawData.getInputLinks(), bounds, false);
			} graphics.escapeFocus();
		}

		Pair<ListIterator<RawExportControl>,ListIterator<RawExportControl>> controlsWithinBodyIterators = renderOutOfBodyControls(graphics, palette, rawData, localGridColumn, 2);
		graphics.setFocus(new GridBound(localGridColumn++), new RimBound(0, 0, true, true)); {
			gateBodyRender.render(controlsWithinBodyIterators.first(), controlsWithinBodyIterators.second());
		} graphics.escapeFocus();
		if(rawData.hasOutputLinks()) {
			graphics.setFocus(new GridBound(localGridColumn++), new GridBound(rowEnd, rowEnd + 1, 2), GRID_SIZE / 5, 0, false, true); {
				int[] bounds = rawData.getOutputLinksRowBounds();
				renderLinks(graphics, palette, rowEnd, rawData.getOutputLinks(), bounds, true);
			} graphics.escapeFocus();
		}
	}
	
	private static <ImageType, FontType, ColorType> void renderLinks(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, 
			int lastBodyRow, LinkedList<? extends RawExportLink> links, int[] bounds, boolean output) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setColor(palette.getBlack());
		
		if(bounds[0] < lastBodyRow) {
			graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(0), GRID_SIZE / 3d, 0); {
				graphics.drawLine(2, 0, 0, Graphics.FOCUS_HEIGHT, true);
				graphics.drawLine(-2, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
			if(bounds[0] != lastBodyRow - 1) {
				graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(bounds[0] + 1, lastBodyRow, 3), GRID_SIZE, GRID_SIZE * (lastBodyRow - bounds[0] - 1)); {
					graphics.drawLine(2, 0, 0, Graphics.FOCUS_HEIGHT, true);
					graphics.drawLine(-2, 0, 0, Graphics.FOCUS_HEIGHT, true);
				} graphics.escapeFocus();
			}
			graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(bounds[0], bounds[0] + 1, 3), GRID_SIZE, GRID_SIZE * (bounds[0] + 1 - bounds[0])); {
				graphics.setVerticalyLayout(Graphics.BOTTOM_ALIGN);
				graphics.drawLine(2, 0, 0, Graphics.getFocusHeight(.5d), true);
				graphics.drawLine(-2, 0, 0, Graphics.getFocusHeight(.5d), true);
			} graphics.escapeFocus();
		}
		
		graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(1), GRID_SIZE / 3d, GRID_SIZE * 5d / 8d); {
			if(bounds[0] < lastBodyRow) {
				graphics.setVerticalyLayout(Graphics.TOP_ALIGN);
				graphics.drawLine(2, 0, 0, Graphics.getFocusHeight(.5d), true);
				graphics.drawLine(-2, 0, 0, Graphics.getFocusHeight(.5d), true);
			}
			if(bounds[1] > lastBodyRow) {
				graphics.setVerticalyLayout(Graphics.BOTTOM_ALIGN);
				graphics.drawLine(2, 0, 0, Graphics.getFocusHeight(.5d), true);
				graphics.drawLine(-2, 0, 0, Graphics.getFocusHeight(.5d), true);
			}
			if(output)
				graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.CENTER_ALIGN);
			else
				graphics.setLayout(Graphics.RIGHT_ALIGN, Graphics.CENTER_ALIGN);

			graphics.drawLine(0, -2, Graphics.getFocusWidth(.5d), 0, true);
			graphics.drawLine(0, 2, Graphics.getFocusWidth(.5d), 0, true);
			
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.fillRect(0, 0, 6, 6);
			
			
		} graphics.escapeFocus();
		
		graphics.setColor(palette.getBlack());
		if(bounds[1] > lastBodyRow) {
			if(bounds[1] != lastBodyRow + 1) {
				graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(lastBodyRow + 1, bounds[1], 3), GRID_SIZE, GRID_SIZE * ( bounds[1] - lastBodyRow - 1)); {
					graphics.drawLine(2, 0, 0, Graphics.FOCUS_HEIGHT, true);
					graphics.drawLine(-2, 0, 0, Graphics.FOCUS_HEIGHT, true);
				} graphics.escapeFocus();
			}
			graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(bounds[1], bounds[1] + 1, 3), GRID_SIZE, GRID_SIZE); {
				graphics.setVerticalyLayout(Graphics.TOP_ALIGN);
				graphics.drawLine(2, 0, 0, Graphics.getFocusHeight(.5d), true);
				graphics.drawLine(-2, 0, 0, Graphics.getFocusHeight(.5d), true);
			} graphics.escapeFocus();
		}

		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setFontHeight(8d);
		for(RawExportLink rel : links) {
			int row = rel.row;
			String text = Integer.toString(rel.localReg);
			graphics.setFocus(new RimBound(0, 0, true, true), new GridBound(row, row + 1, 3)); {
				graphics.setFocus(new RimBound(0, 0, false, false), new RimBound(0, 0, false, false), GRID_SIZE / 5, GRID_SIZE / 5); {
					graphics.setColor(palette.getWhite());
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setColor(palette.getBlack());
					graphics.setFocus(new RimBound(3, 3, true, true), new RimBound(3, 3, true, true), GRID_SIZE / 5, GRID_SIZE / 5); {
						graphics.drawText(text, 0, 0);
					} graphics.escapeFocus();
					graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				} graphics.escapeFocus();
			} graphics.escapeFocus();
		}
	}
	
	private static boolean checkIfControlIsInBody(ListIterator<RawExportControl> iterator, int rowBodyStart, int rowBodyEnd) {
		if(!iterator.hasNext()) return false;
		RawExportControl control = iterator.next();
		int row = control.row;
		boolean isInBound = row >= rowBodyStart && row <= rowBodyEnd;
		iterator.previous();
		return isInBound;
	}
	
	
	public static <ImageType, FontType, ColorType> void renderPresetGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, RawExportableGateData rawData, PresetGateModel gm, int column) {
		Hashtable<Integer, RawExportRegister> regs = rawData.getRegisters();
		LinkedList<RawExportControl> classicalControls = rawData.getClassicalControls();
		LinkedList<RawExportControl> quantumControls = rawData.getQuantumControls();
		int[] rowBounds = rawData.getControlRowBounds();
		
		int bodyStart = rawData.getGateRowBodyStart();
		int bodyEnd = rawData.getGateRowBodyEnd();
		
		if(rowBounds == null) {
			rowBounds = new int[] {bodyStart, bodyEnd};
		} else {
			if(bodyStart < rowBounds[0])
				rowBounds[0] = bodyStart;
			if(bodyEnd > rowBounds[1])
				rowBounds[1] = bodyEnd;
		}
		
		switch (gm.getPresetGateType()) {
		case CNOT:
			renderAllControls(graphics, palette, classicalControls, quantumControls, rowBounds, column);
			renderControl(graphics, palette, regs.get(0).row, column, Control.CONTROL_TRUE, 0);
			renderCNotHead(graphics, palette, regs.get(1).row, column);
			break;
		case SWAP:
			renderAllControls(graphics, palette, classicalControls, quantumControls, rowBounds, column);
			renderSwapHead(graphics, palette, regs.get(0).row, column);
			renderSwapHead(graphics, palette, regs.get(1).row, column);
			break;
		case TOFFOLI:
			renderAllControls(graphics, palette, classicalControls, quantumControls, rowBounds, column);
			renderControl(graphics, palette, regs.get(0).row, column, Control.CONTROL_TRUE, 0);
			renderControl(graphics, palette, regs.get(1).row, column, Control.CONTROL_TRUE, 0);
			renderCNotHead(graphics, palette, regs.get(2).row, column);
			break;
		case MEASUREMENT:
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			int row = regs.get(0).row;
			graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
				graphics.setFocus(new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, false, false), new RimBound(GRID_SIZE / 6, GRID_SIZE / 6, true, true)); {
					renderLinksAndControlsAroundGate(graphics, palette, rawData, (a, b) -> {renderMeasurementGate(graphics, palette, column);});
				} graphics.escapeFocus();
			} graphics.escapeFocus();
			break;
		default:
			renderGate(graphics, palette, rawData, gm, column);
			break;
		}
	}
	
	public static <ImageType, FontType, ColorType> void renderMeasurementGate(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int column) {
		graphics.setColor(palette.getWhite());
		graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		graphics.setColor(palette.getBlack());
		graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		
		graphics.setFocus(new RimBound(5, 5, false, false), new RimBound(0, 0, false, false)); {
			graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
			graphics.drawArc(0, 5, GRID_SIZE * .4d, GRID_SIZE * .4d, 0, 180);
			graphics.drawLine(GRID_SIZE * .2d, 2, GRID_SIZE * .2d, GRID_SIZE * .4d - 7, false);
		} graphics.escapeFocus();
	}
	
	
	public static <ImageType, FontType, ColorType> void drawVerticalLine(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int row2, int column, int focusGroupsAbove) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		graphics.setColor(palette.getBlack());
		graphics.setLineWidth(1);
		if(row2 - row > 1) {
			graphics.setFocus(new GridBound(column), new GridBound(row + 1, row2, focusGroupsAbove), 0, GRID_SIZE * (row2 - row - 1)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
		}
		
		graphics.setFocus(new GridBound(column), new GridBound(row, row + 1, focusGroupsAbove), 0, GRID_SIZE, false, true); {
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(1)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
		} graphics.escapeFocus();
				
		graphics.setFocus(new GridBound(column), new GridBound(row2, row2 + 1, focusGroupsAbove), 0, GRID_SIZE, false, true); {
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(0)); {
				graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
			} graphics.escapeFocus();
			graphics.setFocus(new RimBound(0, 0, false, false), new GridBound(1)); {
			} graphics.escapeFocus();
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderAllControls(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, LinkedList<RawExportControl> classicalControls,
			LinkedList<RawExportControl> quantumControls, int[] controlsBounds, int column) {
		if(controlsBounds == null) return;
		drawVerticalLine(graphics, palette, controlsBounds[0], controlsBounds[1], column, 0);
		for(RawExportControl control : classicalControls)
			renderControl(graphics, palette, control.row, column, control.controlStatus, 0);
		for(RawExportControl control : quantumControls)
			renderControl(graphics, palette, control.row, column, control.controlStatus, 0);
	}
	
	public static <ImageType, FontType, ColorType> void renderControl(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column, boolean controlType, int focusGroupsAbove) {
		graphics.setFocus(new GridBound(column), new GridBound(row, row + 1, focusGroupsAbove), 0, GRID_SIZE); {
			renderControlInFocus(graphics, palette, controlType);
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderCNotHead(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.setColor(palette.getBlack());
			graphics.drawOval(0, 0, GRID_SIZE / 2, GRID_SIZE / 2);
			graphics.drawLine(0, 0, GRID_SIZE / 2, 0, true);
			graphics.drawLine(0, 0, 0, GRID_SIZE / 2, true);
		} graphics.escapeFocus();
	}
	
	public static <ImageType, FontType, ColorType> void renderSwapHead(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, int row, int column) {
		graphics.setFocus(new GridBound(column), new GridBound(row), GRID_SIZE, GRID_SIZE); {
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
	
	
	public static <ImageType, FontType, ColorType> void renderQubitLines(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, FocusData gridData, RowTypeList rowTypeList) {
		graphics.setBoundsManaged(false);
		graphics.setColor(palette.getBlack());
		graphics.setLineWidth(1d);
		
		Iterator<RowTypeElement> iterator = rowTypeList.iterator();
		for(int i = 0; i < gridData.getRowCount(); i++) {
			RowTypeElement rowTypeElement = iterator.next();
			setFocusQubitLine(graphics, gridData, i); {
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				if(rowTypeElement.getType() == RowType.CLASSICAL) {
					graphics.drawLine(0, -2, Graphics.FOCUS_WIDTH, 0, true);
					graphics.drawLine(0, 2, Graphics.FOCUS_WIDTH, 0, true);
				} else if(rowTypeElement.getType() == RowType.QUANTUM) {
					graphics.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				} else if(rowTypeElement.getType() == RowType.CLASSICAL_AND_QUANTUM) {
					graphics.drawLine(0, -3, Graphics.FOCUS_WIDTH, 0, true);
					graphics.drawLine(0, 3, Graphics.FOCUS_WIDTH, 0, true);
					graphics.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				}
			} graphics.escapeFocus();
		}
	}
	
	public static <ImageType, FontType, ColorType> void renderQubitRegs(Graphics<ImageType, FontType, ColorType> graphics, RenderPalette<ImageType, FontType, ColorType> palette, FocusData gridData, RowTypeList rowTypeList, boolean renderBackColor) {
		graphics.setBoundsManaged(false);
		graphics.setFont(palette.getDefault(), 15);
		

		Iterator<RowTypeElement> iterator = rowTypeList.iterator();
		if(renderBackColor) {
			for(int r = 0; r < gridData.getRowCount(); r++) {
				RowTypeElement rowTypeElement = iterator.next();
				setFocusQubitRegister(graphics, gridData, r); {
					graphics.setLayout(Graphics.RIGHT_ALIGN, Graphics.CENTER_ALIGN);
					graphics.setColor(palette.getColor(1d, 1d, 1d, .75d));
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setColor(palette.getBlack());
					RowType type = rowTypeElement.getType();
					if(type == RowType.CLASSICAL) {
						graphics.drawLatex("\\(b_{" + rowTypeElement.getReg() + "}\\)", 5, 0);
					} else if (type == RowType.QUANTUM) {
						graphics.drawLatex("\\(\\vert\\Psi_{" + rowTypeElement.getReg() + "}\\rangle\\)", 5, 0);
					} else if(type == RowType.CLASSICAL_AND_QUANTUM) {
						graphics.drawLatex("\\(b_{" + rowTypeElement.getReg() + "} \\text{ / } \\vert\\Psi_{" + rowTypeElement.getReg() + "}\\rangle\\)", 5, 0);
					}
				} graphics.escapeFocus();
			}
		} else {
			graphics.setColor(palette.getBlack());
			for(int r = 0; r < gridData.getRowCount(); r++) {
				RowTypeElement rowTypeElement = iterator.next();
				setFocusQubitRegister(graphics, gridData, r); {
					graphics.setLayout(Graphics.RIGHT_ALIGN, Graphics.CENTER_ALIGN);
					RowType type = rowTypeElement.getType();
					if(type == RowType.CLASSICAL) {
						graphics.drawLatex("\\(b_{" + rowTypeElement.getReg() + "}\\)", 5, 0);
					} else if (type == RowType.QUANTUM) {
						graphics.drawLatex("\\(\\vert\\Psi_{" + rowTypeElement.getReg() + "}\\rangle\\)", 5, 0);
					} else if(type == RowType.CLASSICAL_AND_QUANTUM) {
						graphics.drawLatex("\\(b_{" + rowTypeElement.getReg() + "} \\text{ / } \\vert\\Psi_{" + rowTypeElement.getReg() + "}\\rangle\\)", 5, 0);
					}
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
		graphics.setFocus(new RimBound(0, 0, false, false), new RimBound(topMargin, 0, false, false), GateRenderer.QUBIT_REGS_SIZE, height);
	}
	
	private static interface GateBodyRender {
		public void render(ListIterator<RawExportControl> quantumControlIterator, ListIterator<RawExportControl> classicalControlIterator);
	}
	
}
