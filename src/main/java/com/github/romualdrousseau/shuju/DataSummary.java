package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.features.NumericFeature;

public class DataSummary
{
	public int count;
	public double min;
	public double max;
	public double sum;
	public double avg;

	public DataSummary(DataSet dataset, int col) {
		calculateStatistics(dataset, col);
	}

	public String toString() {
		String result = "\n=== Summary ===\n";
		result += String.format("Count: %d\n", this.count);
		result += String.format("Min:   %f\n", this.min);
		result += String.format("Max:   %f\n", this.max);
		result += String.format("Sum:   %f\n", this.sum);
		result += String.format("Avg:   %f\n", this.avg);
		return result;
	}

	private void calculateStatistics(DataSet dataset, int col) {
		this.count = dataset.rows().size();
		this.min = 0;
		this.max = 0;
		this.sum = 0;
		this.avg = 0;

		boolean firstRow = true;
		for(DataRow row: dataset.rows()) {
			IFeature<?> feature = (col == IFeature.LABEL) ? row.getLabel() : row.features().get(col);
			if(feature instanceof NumericFeature) {
				if(firstRow) {
					this.min = (Double) feature.getValue();
					this.max = (Double) feature.getValue();
					this.sum = (Double) feature.getValue();
					firstRow = false;
				}
				else {
					this.min = Math.min(this.min, (Double) feature.getValue());
					this.max = Math.max(this.max, (Double) feature.getValue());
					this.sum += (Double) feature.getValue();
				}
			}
		}
		this.avg = this.sum / (double) this.count;
	}
}
