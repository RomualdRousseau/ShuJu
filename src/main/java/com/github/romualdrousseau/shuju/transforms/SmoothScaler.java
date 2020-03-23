package com.github.romualdrousseau.shuju.transforms;

import com.github.romualdrousseau.shuju.ITransform;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class SmoothScaler implements ITransform {
    public SmoothScaler(float coef) {
        this.firstRow = true;
        this.coef = coef;
    }

    public void apply(Tensor1D feature, int rowIndex, int colIndex) {
        if (this.firstRow) {
            this.lastValue = feature;
            firstRow = false;
        } else {
            this.lastValue = (Tensor1D) feature.mul(this.coef).add(this.lastValue.mul(1.0f - this.coef));
        }
    }

    private boolean firstRow;
    private Tensor1D lastValue;
    private float coef;
}
