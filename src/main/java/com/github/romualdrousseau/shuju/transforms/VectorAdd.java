package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.DataRow;
import com.github.romualdrousseau.shuju.DataSet;

public class VectorAdd implements ITransform {
    public VectorAdd(DataSet other, int part) {
        this.other = other;
        this.part = part;
        this.a = 1.0f;
    }

    public VectorAdd(DataSet other, int part, float a) {
        this.other = other;
        this.part = part;
        this.a = a;
    }

    public void apply(Vector feature, int rowIndex, int colIndex) {
        Vector otherFeature = (this.part == DataRow.LABELS) ? this.other.rows().get(rowIndex).label()
                : this.other.rows().get(rowIndex).features().get(colIndex);
        feature.add(otherFeature.copy().mult(this.a));
    }

    private DataSet other;
    private int part;
    private float a;
}
