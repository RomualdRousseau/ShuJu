package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.Tensor3D;

public interface RegularizerFunc {
    Tensor2D apply(Tensor2D w);

    Tensor3D apply(Tensor3D m);
}
