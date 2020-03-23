package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class NumericColumn implements IColumn<Float> {

    public Tensor1D valueOf(Float v) {
        return new Tensor1D(1, v);
    }
}
