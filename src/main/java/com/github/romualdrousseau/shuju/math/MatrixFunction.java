package com.github.romualdrousseau.shuju.math;

public interface MatrixFunction<T, R> {
    R apply(T v, int row, int col, Matrix matrix);
}
