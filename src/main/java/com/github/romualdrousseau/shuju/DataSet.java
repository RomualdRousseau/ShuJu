package com.github.romualdrousseau.shuju;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class DataSet
{
	public DataSet() {
		this.rows = new ArrayList<DataRow>();
	}

	public DataSet(List<DataRow> rows) {
		this.rows = rows;
	}

	public List<DataRow> rows() {
		return this.rows;
	}

	public DataSet addRow(DataRow row) {
		this.rows.add(row);
		return this;
	}

	public DataSet shuffle() {
		java.util.Collections.shuffle(this.rows);
		return this;
	}

	public DataSet subset(int rowStart, int rowEnd) {
		List<DataRow> temp = new ArrayList<DataRow>();
		for(int i = rowStart; i < rowEnd; i++) {
			temp.add(this.rows.get(i));
		}
		return new DataSet(temp);
	}
	
	public DataSet transform(ITransform transfomer, int colIndex) {
		int rowIndex = 0;
		if(colIndex == IFeature.LABEL) {
			for(DataRow row: this.rows) {
				transfomer.apply(row.getLabel(), rowIndex++, colIndex);
			}
		}
		else {
			for(DataRow row: this.rows) {
				transfomer.apply(row.features().get(colIndex), rowIndex++, colIndex);
			}
		}
		return this;
	}

	public String toString() {
		String result = "";
		for(DataRow row: this.rows) {
			result += row.toString() + "\n";
		}
		return result;
	}

	private List<DataRow> rows;
}
