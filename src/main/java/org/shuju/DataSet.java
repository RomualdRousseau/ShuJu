package org.shuju;

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

	public DataSet subset(int start, int end) {
		List<DataRow> temp = new ArrayList<DataRow>();
		for(int i = start; i < end; i++) {
			temp.add(this.rows.get(i));
		}
		return new DataSet(temp);
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
