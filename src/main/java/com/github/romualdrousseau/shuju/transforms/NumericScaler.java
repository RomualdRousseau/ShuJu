package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.DataSummary;

public class NumericScaler implements ITransform {
    public NumericScaler(DataSummary summary) {
        this.min = summary.min.copy();
        this.ratio = summary.min.copy().ones().div(summary.max.copy().sub(summary.min));
    }

    public void apply(Vector feature, int rowIndex, int colIndex) {
        feature.sub(this.min).mul(this.ratio);
    }

    private Vector min;
    private Vector ratio;
}
