package com.github.romualdrousseau.shuju.math;

public interface TensorFunction<T extends AbstractTensor<?>> {
    float apply(float v, int[] loc, T tensor);
}
