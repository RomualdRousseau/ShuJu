package com.github.romualdrousseau.shuju.math;

public interface TensorFunction<T> {
    float apply(float v, int[] loc, T tensor);
}
