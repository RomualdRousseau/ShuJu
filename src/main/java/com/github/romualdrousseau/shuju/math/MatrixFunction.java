package com.github.romualdrousseau.shuju.math;

public interface MatrixFunction extends TensorFunction<Matrix> {
    float apply(float v, int[] loc, Matrix matrix);
}
