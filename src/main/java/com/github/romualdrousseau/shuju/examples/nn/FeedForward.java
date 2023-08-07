package com.github.romualdrousseau.shuju.examples.nn;

import org.tensorflow.EagerSession;
import org.tensorflow.Operand;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Constant;
import org.tensorflow.op.core.Sum;
import org.tensorflow.op.linalg.MatMul;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;

import com.github.romualdrousseau.shuju.types.Tensor;

public class FeedForward {

    final static float[][] training_features =  {
        {0.0f, 0.0f},
        {0.0f, 1.0f},
        {1.0f, 1.0f},
        {1.0f, 0.0f},
    };

    final static float[][] training_labels =  {
        {0.0f},
        {1.0f},
        {1.0f},
        {0.0f},
    };

    public FeedForward() {
        try (EagerSession session = EagerSession.create()) {
            Ops tf = Ops.create(session);

            final int n = 2;
            final int m = 10;
            final int o = 1;

            // Compute GOROT initializer
            this.layers_W0 = Tensor.full((float) Math.sqrt(6.0 / (n + m)), n, m).toTFloat32();
            tf.math.mul(
                    tf.constant(this.layers_W0),
                    tf.math.sub(
                            tf.constant(1.0f),
                            tf.math.mul(
                                    tf.random.randomUniform(tf.constant(TInt32.vectorOf(n, m)), TFloat32.class),
                                    tf.constant(2.0f))))
                    .asTensor().copyTo(this.layers_W0);
            // Compute ZERO initializer
            this.layers_B0 = Tensor.zeros(1, m).toTFloat32();

            this.layers_W1 = Tensor.full((float) Math.sqrt(6.0 / (m + o)), m, o).toTFloat32();
            // Compute GOROT initializer
            tf.math.mul(
                    tf.constant(this.layers_W1),
                    tf.math.sub(
                            tf.constant(1.0f),
                            tf.math.mul(
                                    tf.random.randomUniform(tf.constant(TInt32.vectorOf(m, o)), TFloat32.class),
                                    tf.constant(2.0f))))
                    .asTensor().copyTo(this.layers_W1);
            // Compute ZERO initializer
            this.layers_B1 = Tensor.zeros(1, o).toTFloat32();
        }
    }

    public void predict(TFloat32 input) {
        try (EagerSession session = EagerSession.create()) {
            Ops tf = Ops.create(session);

            // Evaluate

            Constant<TFloat32> x0 = tf.constant(input);

            Constant<TFloat32> w0 = tf.constant(this.layers_W0);
            Constant<TFloat32> b0 = tf.constant(this.layers_B0);
            Operand<TFloat32> x1 = tf.math.add(tf.linalg.matMul(x0, w0), b0);

            Operand<TFloat32> x2 = tf.math.tanh(x1);

            Constant<TFloat32> w1 = tf.constant(this.layers_W1);
            Constant<TFloat32> b1 = tf.constant(this.layers_B1);
            Operand<TFloat32> x3 = tf.math.add(tf.linalg.matMul(x2, w1), b1);

            Operand<TFloat32> yhat = tf.math.sigmoid(x3);

            yhat.asTensor().scalars().forEach(x -> System.out.print(x.getFloat() + " "));
            System.out.println();
        }
    }

    public void fit(TFloat32 input, TFloat32 output) {
        try (EagerSession session = EagerSession.create()) {
            Ops tf = Ops.create(session);

            Constant<TInt32> AXIS_ZERO = tf.constant(0);
            Constant<TFloat32> ONE = tf.constant(1.0f);
            Constant<TFloat32> TWO = tf.constant(2.0f);

            // Evaluate

            Constant<TFloat32> x0 = tf.constant(input);

            Constant<TFloat32> w0 = tf.constant(this.layers_W0);
            Constant<TFloat32> b0 = tf.constant(this.layers_B0);
            Operand<TFloat32> x1 = tf.math.add(tf.linalg.matMul(x0, w0), b0);

            Operand<TFloat32> x2 = tf.math.tanh(x1);

            Constant<TFloat32> w1 = tf.constant(this.layers_W1);
            Constant<TFloat32> b1 = tf.constant(this.layers_B1);
            Operand<TFloat32> x3 = tf.math.add(tf.linalg.matMul(x2, w1), b1);

            Operand<TFloat32> yhat = tf.math.sigmoid(x3);

            // Back propagation

            Constant<TFloat32> y = tf.constant(output);
            Operand<TFloat32> error = tf.math.div(tf.math.sub(yhat, y), tf.constant((float) yhat.shape().get(0)));

            error = tf.math.mul(error, tf.math.mul(yhat, tf.math.sub(ONE, yhat)));

            Operand<TFloat32> dw1 = tf.linalg.matMul(x2, error, MatMul.transposeA(true));
            Operand<TFloat32> db1 = tf.sum(error, AXIS_ZERO, Sum.keepDims(true));
            error = tf.linalg.matMul(error, w1, MatMul.transposeB(true));

            error = tf.math.mul(error, tf.math.sub(ONE, tf.math.pow(x2, TWO)));

            Operand<TFloat32> dw0 = tf.linalg.matMul(x0, error, MatMul.transposeA(true));
            Operand<TFloat32> db0 = tf.sum(error, AXIS_ZERO, Sum.keepDims(true));
            error = tf.linalg.matMul(error, w0, MatMul.transposeB(true));

            tf.math.add(w1, tf.math.mul(dw1, tf.constant(-alpha))).asTensor().copyTo(this.layers_W1);
            tf.math.add(b1, tf.math.mul(db1, tf.constant(-alpha))).asTensor().copyTo(this.layers_B1);
            tf.math.add(w0, tf.math.mul(dw0, tf.constant(-alpha))).asTensor().copyTo(this.layers_W0);
            tf.math.add(b0, tf.math.mul(db0, tf.constant(-alpha))).asTensor().copyTo(this.layers_B0);
        }

        alpha = Math.max(alpha * 0.99f, 0.01f);
    }

    public static void main(final String[] args) {
        FeedForward ff = new FeedForward();

        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 4; j++) {
                ff.fit(
                    TFloat32.tensorOf(Shape.of(1, 2), DataBuffers.of(training_features[j])),
                    TFloat32.tensorOf(Shape.of(1, 1), DataBuffers.of(training_labels[j])));
            }
        }

        for (int j = 0; j < 4; j++) {
            ff.predict(TFloat32.tensorOf(Shape.of(1, 2), DataBuffers.of(training_features[j])));
        }
    }

    private final TFloat32 layers_W0;
    private final TFloat32 layers_B0;
    private final TFloat32 layers_W1;
    private final TFloat32 layers_B1;
    private float alpha = 0.1f;
}
