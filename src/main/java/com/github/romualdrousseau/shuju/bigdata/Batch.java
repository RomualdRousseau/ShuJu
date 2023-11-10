package com.github.romualdrousseau.shuju.bigdata;

import java.util.ArrayList;
import java.util.List;

public class Batch {

    private final int batchSize;
    private final List<BatchMetaData> batches;

    private Row[] rows;

    public Batch(final int batchSize) {
        this.batchSize = batchSize;
        this.batches = new ArrayList<>();
        this.rows = new Row[this.batchSize];
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public List<BatchMetaData> getBatches() {
        return this.batches;
    }

    public Row[] getRows() {
        return this.rows;
    }

    public void setRows(final Row[] rows) {
        this.rows = rows;
    }

    public void setRow(final int idx, final Row row) {
        this.rows[idx] = row;
    }

    public Row getRow(final int idx) {
        return this.rows[idx];
    }
}
