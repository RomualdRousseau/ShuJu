package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Tensor1D;
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

    public void apply(Tensor1D feature, int rowIndex, int colIndex) {
        Tensor1D otherFeature = (this.part == DataRow.LABELS) ? this.other.rows().get(rowIndex).label()
                : this.other.rows().get(rowIndex).features().get(colIndex);
        feature.add(otherFeature.copy().mul(this.a));
    }

    private DataSet other;
    private int part;
    private float a;
}
