package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Softmax implements ActivationFunc {
    public Matrix apply(Matrix input) {
      final float c = -input.get(input.argmax(0), 0);

      float temp = 0.0f;
      for (int k = 0; k < input.rowCount(); k++) {
        temp += Scalar.exp(input.get(k, 0) + c);
      }
      final float sum = temp;

      final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
        public final Float apply(Float x, int row, int col, Matrix matrix) {
          return Scalar.exp(x + c) / sum;
        }
      };

      return input.map(fn);
    }

    public Matrix derivate(Matrix output) {
      final MatrixFunction<Float, Float> fn = new MatrixFunction<Float, Float>() {
        public final Float apply(Float y, int row, int col, Matrix output) {
          final float k = (row == col) ? 1.0f : 0.0f;
          return output.get(col, 0) * (k - output.get(row, 0));
        }
      };
      return new Matrix(output.rowCount(), output.rowCount()).map(fn, output);
    }
  }
