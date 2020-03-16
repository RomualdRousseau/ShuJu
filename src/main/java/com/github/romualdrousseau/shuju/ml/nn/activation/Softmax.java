package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Matrix;
import com.github.romualdrousseau.shuju.math.MatrixFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Softmax implements ActivationFunc {
    public Matrix apply(Matrix input) {
      final float c = -input.get(input.argmax(0, 0), 0);

      float temp = 0.0f;
      for (int k = 0; k < input.rowCount(); k++) {
        temp += Scalar.exp(input.get(k, 0) + c);
      }
      final float sum = temp;

      final MatrixFunction fn = new MatrixFunction() {
        public final float apply(float x, int[] ij, Matrix matrix) {
          return Scalar.exp(x + c) / sum;
        }
      };

      return input.map(fn);
    }

    public Matrix derivate(Matrix output) {
        final MatrixFunction fn = new MatrixFunction() {
            public final float apply(float y, int[] ij, Matrix output) {
          final float k = (ij[0] == ij[1]) ? 1.0f : 0.0f;
          return output.get(ij[1], 0) * (k - output.get(ij[0], 0));
        }
      };
      return new Matrix(output.rowCount(), output.rowCount()).map(fn, output);
    }
  }
