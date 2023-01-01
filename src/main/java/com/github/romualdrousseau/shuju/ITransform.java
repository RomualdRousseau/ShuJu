package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;

public interface ITransform {
    public void apply(Tensor1D feature, int rowIndex, int colIndex);
}
