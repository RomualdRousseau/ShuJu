package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.Vector;

public interface IColumn<T> {
    Vector valueOf(T v);
}
