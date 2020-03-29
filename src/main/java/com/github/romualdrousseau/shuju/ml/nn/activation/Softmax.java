package com.github.romualdrousseau.shuju.ml.nn.activation;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Tensor2D;
import com.github.romualdrousseau.shuju.math.TensorFunction;
import com.github.romualdrousseau.shuju.ml.nn.ActivationFunc;

public class Softmax implements ActivationFunc {
    public Tensor2D apply(Tensor2D input) {
      final float c = -input.get(input.argmax(0, 0), 0);

      float temp = 0.0f;
      for (int k = 0; k < input.shape[0]; k++) {
        temp += Scalar.exp(input.get(k, 0) + c);
      }
      final float sum = temp;

      final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
        public final float apply(float x, int[] ij, Tensor2D matrix) {
          return Scalar.exp(x + c) / sum;
        }
      };

      return input.map(fn);
    }

    public Tensor2D derivate(Tensor2D output) {
        final TensorFunction<Tensor2D> fn = new TensorFunction<Tensor2D>() {
            public final float apply(float y, int[] ij, Tensor2D output) {
          final float k = (ij[0] == ij[1]) ? 1.0f : 0.0f;
          return output.get(ij[1], 0) * (k - output.get(ij[0], 0));
        }
      };
      return new Tensor2D(output.shape[0], output.shape[0]).map(fn, output);
    }
  }
