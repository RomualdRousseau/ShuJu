package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;

public class VectorColumn implements IColumn<Vector> {

    public Vector valueOf(Vector v) {
        return v;
    }
}
