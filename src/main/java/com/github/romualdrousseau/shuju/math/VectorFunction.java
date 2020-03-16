package com.github.romualdrousseau.shuju.math;

public interface VectorFunction extends TensorFunction<Vector> {
    float apply(float v, int[] loc, Vector vector);
}
