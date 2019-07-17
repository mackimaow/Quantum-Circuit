package graphicsWrapper;

import java.util.ArrayList;

import utils.customCollections.immutableLists.ImmutableList;

public final class FocusData {
	final ArrayList<Double> widthColumnData;
	final ArrayList<Double> heightRowData;
	
	FocusData(ArrayList<Double> widthColumnData, ArrayList<Double> heightRowData) {
		this.widthColumnData = widthColumnData;
		this.heightRowData = heightRowData;
	}
	
	public int getRowCount() {
		return heightRowData.size();
	}
	
	public int getColumnCount() {
		return widthColumnData.size();
	}
	
	public double getCummulativeWidth(int column) {
		return widthColumnData.get(column);
	}
	
	public double getCummulativeHeight(int row) {
		return heightRowData.get(row);
	}
	
	public double getColumnWidthAt(int column) {
		double previousSize = column == 0 ? 0 : widthColumnData.get(column - 1);
		return widthColumnData.get(column) - previousSize;
	}
	
	public double getRowHeightAt(int row) {
		double previousSize = row == 0 ? 0 : heightRowData.get(row - 1);
		return heightRowData.get(row) - previousSize;
	}
	
	public double getColumnWidthSum(int columnStartInclusive, int columnEndExclusive) {
		double firstCummulativeData = columnStartInclusive == 0? 0d : widthColumnData.get(columnStartInclusive - 1);
		return widthColumnData.get(columnEndExclusive - 1) - firstCummulativeData;
	}
	
	public double getRowHeightSum(int rowStartInclusive, int rowEndExclusive) {
		double firstCummulativeData = rowStartInclusive == 0? 0d : heightRowData.get(rowStartInclusive - 1);
		return heightRowData.get(rowEndExclusive - 1) - firstCummulativeData;
	}
	
	public double getWidth() {
		return widthColumnData.get(widthColumnData.size() - 1);
	}
	
	public double getHeight() {
		return heightRowData.get(heightRowData.size() - 1);
	}
	
	public ImmutableList<Double> getCummulativeWidthData() {
		return new ImmutableList<>(widthColumnData);
	}
	
	public ImmutableList<Double> getCummulativeHeightData() {
		return new ImmutableList<>(heightRowData);
	}
}
