package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.Tensor1D;

public interface IColumn<T> {
    Tensor1D valueOf(T v);
}
