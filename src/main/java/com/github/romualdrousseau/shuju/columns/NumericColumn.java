package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;

public class NumericColumn implements IColumn<Float> {

    public Vector valueOf(Float v) {
        return new Vector(1, v);
    }
}
