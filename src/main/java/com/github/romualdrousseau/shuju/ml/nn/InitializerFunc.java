package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor3D;

public interface InitializerFunc {
    Tensor2D apply(Tensor2D m);

    Tensor3D apply(Tensor3D m);
}
