package com.github.romualdrousseau.shuju.math;

public interface VectorFunction<T, R> {
    R apply(T v, int row, Vector vector);
}
