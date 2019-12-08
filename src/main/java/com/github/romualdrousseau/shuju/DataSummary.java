package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.Vector;

public class DataSummary {
    public int count;
    public Vector min;
    public Vector max;
    public Vector sum;
    public Vector avg;

    public DataSummary(DataSet dataset, int part, int col) {
        this.dataset = dataset;
        this.part = part;
        this.col = col;
        this.calculateStatistics(dataset, part, col);
    }

    public DataSet getDataSet() {
        return this.dataset;
    }

    public int getPart() {
        return this.part;
    }

    public int getColumn() {
        return this.col;
    }

    public String toString() {
        String result = "\n=== Summary ===\n";
        result += String.format("Count: %d\n", this.count);
        result += String.format("Min:   %s\n", this.min);
        result += String.format("Max:   %s\n", this.max);
        result += String.format("Sum:   %s\n", this.sum);
        result += String.format("Avg:   %s\n", this.avg);
        return result;
    }

    private void calculateStatistics(DataSet dataset, int part, int col) {
        this.count = dataset.rows().size();
        this.min = new Vector(0);
        this.max = new Vector(0);
        this.sum = new Vector(0);
        this.avg = new Vector(0);

        boolean firstRow = true;
        for (DataRow row : dataset.rows()) {
            Vector feature = (part == DataRow.LABELS) ? row.label() : row.features().get(col);
            if (firstRow) {
                this.min = feature.copy();
                this.max = feature.copy();
                this.sum = feature.copy();
                firstRow = false;
            } else {
                this.min.min(feature);
                this.max.max(feature);
                this.sum.add(feature);
            }
        }
        this.avg = this.sum.copy().div((float) this.count);
    }

    private DataSet dataset;
    private int part;
    private int col;
}
