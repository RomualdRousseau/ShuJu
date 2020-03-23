package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class VectorColumn implements IColumn<Tensor1D> {

    public Tensor1D valueOf(Tensor1D v) {
        return v;
    }
}
