package com.github.romualdrousseau.shuju.math.deprecated;

@Deprecated
public interface TensorFunction<T extends AbstractTensor<?>> {
    float apply(float v, int[] loc, T tensor);
}
