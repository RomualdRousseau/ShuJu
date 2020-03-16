package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Sigmoid implements ActivationFunc {
  public Matrix apply(Matrix input) {
    final MatrixFunction fn = new MatrixFunction() {
        public final float apply(float x, int[] ij, Matrix matrix) {
        return 1.0f / (1.0f + Scalar.exp(-x));
      }
    };
    return input.map(fn);
  }

  public Matrix derivate(Matrix output) {
    final MatrixFunction fn = new MatrixFunction() {
        public final float apply(float y, int[] ij, Matrix matrix) {
        return y * (1.0f - y);
      }
    };
    return output.copy().map(fn);
  }
}
