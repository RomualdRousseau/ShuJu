package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class VectorShift implements ITransform {
    public VectorShift(float a) {
        this.a = a;
    }

    public void apply(Tensor1D feature, int rowIndex, int colIndex) {
        feature.add(this.a);
    }

    private float a;
}
