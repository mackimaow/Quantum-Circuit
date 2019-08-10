package appFX.appUI.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.gateModels.PresetGateType.PresetGateModel;
import graphicsWrapper.AxisBound.GridBound;
import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.CompiledGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import utils.customCollections.immutableLists.ImmutableArray;

public class GateIcon {
	
	public static final int DEFAULT_ICON_SIZE = 35;
	public static final double DEFAULT_LINE_WIDTH = .5d;
	
	
	
	public static ImageView gateModelToIconNode(GateModel gm) {
		WritableImage wi = GateIcon.gateModelToFxIcon(gm, 1.75d);
		ImageView im = new ImageView(wi);
		return im;
	}
	
	
	
	public static BufferedImage gateModelToIcon(GateModel gm, double size) {
		if(gm.isPreset()) {
			PresetGateModel pgm = (PresetGateModel) gm;
			return presetGateToIcon(pgm.getPresetGateType(), size);
		} else {
			return mkDefaultIcon(gm, size);
		}
	}
	
	
	
	public static WritableImage gateModelToFxIcon(GateModel gm, double size) {
		return toFxImage(gateModelToIcon(gm, size));
	}
	
	
	
	public static BufferedImage presetGateToIcon(PresetGateType pgt, double size) {
		switch(pgt) {
		case CNOT:
			return mkCnotIcon(size);
		case IDENTITY:
			return mkIdentityIcon(size);
		case MEASUREMENT:
			return mkMearsurementIcon(size);
		case SWAP:
			return mkSwap(size);
		case TOFFOLI:
			return mkToffoliIcon(size);
		default:
			return mkDefaultIcon(pgt.getModel(), size);
		}
	}
	
	
	
	public static WritableImage presetGateToFxIcon(PresetGateType pgt, double size) {
		return toFxImage(presetGateToIcon(pgt, size));
	}
	
	
	
	public static BufferedImage mkDefaultIcon(GateModel gm, double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			g.setFont(palette.getDefault(), 11d);
			g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
			g.setFocus(new RimBound(5, 5, true, true), new RimBound(0, 0, true, true)); {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				g.setFocus(new RimBound(5, 5, true, true), new RimBound(5, 5, true, true));{
					g.setColor(Color.BLACK);
					ImmutableArray<String> paramLatex = gm.getParameters();
					String paramString = "\\( \\text{" + gm.getSymbol() + "}";
					
					if(!paramLatex.isEmpty()) {
						paramString += "( " + paramLatex.get(0);
						
						for(int i = 1; i < paramLatex.size(); i++)
							paramString += " , " + paramLatex.get(i);
						
						paramString += " ) ";
					}
					paramString +=  "\\)";
					g.drawLatex(paramString, 0, 0);
				} g.escapeFocus();
			} g.escapeFocus();
		});
		
		return createImage(compiledGraphics, size);
	}
	
	
	
	public static BufferedImage mkCnotIcon(double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setFont(palette.getDefault(), 11d);
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			goInFocus(g, 0, DEFAULT_ICON_SIZE / 2d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawLowerVerticalLineInFocus(g);
				drawControlHeadInFocus(g, DEFAULT_ICON_SIZE / 8d);
			} g.escapeFocus();
			goInFocus(g, 1, DEFAULT_ICON_SIZE / 2d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawUpperVerticalLineInFocus(g);
				drawCnotHeadIconInFocus(g, DEFAULT_ICON_SIZE / 3d);
			} g.escapeFocus();
		});
		
		return createImage(compiledGraphics, size);
	}
	
	
	
	public static BufferedImage mkToffoliIcon(double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setFont(palette.getDefault(), 11d);
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			goInFocus(g, 0, DEFAULT_ICON_SIZE / 3d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawLowerVerticalLineInFocus(g);
				drawControlHeadInFocus(g, DEFAULT_ICON_SIZE / 8d);
			} g.escapeFocus();
			goInFocus(g, 1, DEFAULT_ICON_SIZE / 3d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawVerticalLineInFocus(g);
				drawControlHeadInFocus(g, DEFAULT_ICON_SIZE / 8d);
			} g.escapeFocus();
			goInFocus(g, 2, DEFAULT_ICON_SIZE / 3d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawUpperVerticalLineInFocus(g);
				drawCnotHeadIconInFocus(g, DEFAULT_ICON_SIZE / 3d);
			} g.escapeFocus();
		});
		
		return createImage(compiledGraphics, size);
	}
	
	
	
	public static BufferedImage mkIdentityIcon(double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setFont(palette.getDefault(), 11d);
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			g.setFocus(new RimBound(0, 0, true, true), new RimBound(0, 0, true, true), DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
			} g.escapeFocus();
		});
		return createImage(compiledGraphics, size);
	}
	
	
	
	public static BufferedImage mkSwap(double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setFont(palette.getDefault(), 11d);
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			goInFocus(g, 1, DEFAULT_ICON_SIZE / 3d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawLowerVerticalLineInFocus(g);
				drawSwapHeadInFocus(g, DEFAULT_ICON_SIZE / 3d);
			} g.escapeFocus();
			goInFocus(g, 2, DEFAULT_ICON_SIZE / 3d); {
				g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				drawUpperVerticalLineInFocus(g);
				drawSwapHeadInFocus(g, DEFAULT_ICON_SIZE / 3d);
			} g.escapeFocus();
		});
		return createImage(compiledGraphics, size);
	}
	
	
	
	public static BufferedImage mkMearsurementIcon(double size) {
		RenderPalette<Image, Font, Color> palette = CustomSWTGraphics.RENDER_PALETTE;
		
		CompiledGraphics<Image, Font, Color> compiledGraphics = Graphics.<Image, Font, Color>compileGraphicalBluePrint((g, a)->{
			g.setColor(Color.BLACK);
			g.setLineWidth(DEFAULT_LINE_WIDTH);
			g.setFont(palette.getDefault(), 11d);
			g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			g.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
			g.setFocus(new RimBound(5, 5, true, true), new RimBound(0, 0, true, true)); {
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				g.setFocus(new RimBound(5, 5, true, true), new RimBound(5, 5, true, true));{
					g.setColor(Color.BLACK);
					g.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
					g.drawArc(0, 5, DEFAULT_ICON_SIZE * .4d, DEFAULT_ICON_SIZE * .4d, 0, 180);
					g.drawLine(DEFAULT_ICON_SIZE * .2d, 2, DEFAULT_ICON_SIZE * .2d, DEFAULT_ICON_SIZE * .4d - 7, false);
				} g.escapeFocus();
			} g.escapeFocus();
		});
		
		return createImage(compiledGraphics, size);
	}
	
	
	public static WritableImage toFxImage(BufferedImage bi) {
		return SwingFXUtils.toFXImage(bi, null);
	}
	
	
	public static void goInFocus(Graphics<Image, Font, Color> g, int row, double rowHeight) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		g.setFocus(new RimBound(0, 0, true, true), new GridBound(row), DEFAULT_ICON_SIZE, rowHeight);
	}
	
	
	
	public static void drawVerticalLineInFocus(Graphics<Image, Font, Color> g) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		g.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
	}
	
	
	
	public static void drawUpperVerticalLineInFocus(Graphics<Image, Font, Color> g) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.TOP_ALIGN);
		g.drawLine(0, 0, 0, Graphics.getFocusHeight(.5d), true);
	}
	
	
	
	public static void drawLowerVerticalLineInFocus(Graphics<Image, Font, Color> g) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.BOTTOM_ALIGN);
		g.drawLine(0, 0, 0, Graphics.getFocusHeight(.5d), true);
	}
	
	
	
	public static void drawCnotHeadIconInFocus(Graphics<Image, Font, Color> g, double size) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		g.drawOval(0, 0, size, size);
		g.drawLine(0, 0, 0, size, true);
		g.drawLine(0, 0, size, 0, true);
	}
	
	
	
	public static void drawSwapHeadInFocus(Graphics<Image, Font, Color> g, double size) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		g.drawLine(0, 0, size, size, true);
		g.drawLine(0, 0, size, size, false);
	}
	
	
	
	public static void drawControlHeadInFocus(Graphics<Image, Font, Color> g, double size) {
		g.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		g.fillOval(0, 0, size, size);
	}
	
	
	
	private static BufferedImage createImage(CompiledGraphics<Image, Font, Color> compiledGraphics, double size) {
		FocusData fd = compiledGraphics.getFocusData().getElement();
		
		int width = (int) Math.round((fd.getWidth()+1) * size);
		int height = (int) Math.round((fd.getHeight()+1) * size);
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setRenderingHint(
			    RenderingHints.KEY_ANTIALIASING,
			    RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(
		    RenderingHints.KEY_TEXT_ANTIALIASING,
		    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		CustomSWTGraphics customGraphics = new CustomSWTGraphics(g2d);
		Graphics.graphicsDraw(0, 0, size, compiledGraphics, customGraphics);
		
		return bi;
	}
	
	
	
	
	
}
