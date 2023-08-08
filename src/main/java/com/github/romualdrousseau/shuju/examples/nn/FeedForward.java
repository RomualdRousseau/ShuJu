package com.github.romualdrousseau.shuju.examples.nn;

import java.util.List;

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
            final Ops tf = Ops.create(session);

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

    public void predict(final TFloat32 input) {
        try (EagerSession session = EagerSession.create()) {
            final Ops tf = Ops.create(session);

            final List<Operand<TFloat32>> args = this.model(tf, tf.constant(input));

            args.get(8).asTensor().scalars().forEach(x -> System.out.print(x.getFloat() + " "));
            System.out.println();
        }
    }

    public void fit(final TFloat32 input, final TFloat32 output) {
        try (EagerSession session = EagerSession.create()) {
            final Ops tf = Ops.create(session);
            List<Operand<TFloat32>> tmp;
            Operand<TFloat32> err;

            // Evaluate

            tmp = this.model(tf, tf.constant(input));
            final Operand<TFloat32> x0   = tmp.get(0);
            final Operand<TFloat32> w0   = tmp.get(1);
            final Operand<TFloat32> b0   = tmp.get(2);
            // Operand<TFloat32> x1   = args.get(3);
            final Operand<TFloat32> x2   = tmp.get(4);
            final Operand<TFloat32> w1   = tmp.get(5);
            final Operand<TFloat32> b1   = tmp.get(6);
            // Operand<TFloat32> x3   = args.get(7);
            final Operand<TFloat32> yhat = tmp.get(8);

            // Back propagation

            err = this.mse_gd(tf, yhat, tf.constant(output));

            err = this.sigmoid_gd(tf, yhat, err);

            tmp = dense_gd(tf, x2, w1, err);
            final Operand<TFloat32> dw1 = tmp.get(0);
            final Operand<TFloat32> db1 = tmp.get(1);
            err = tmp.get(2);

            err = this.tanh_gd(tf, x2, err);

            tmp = this.dense_gd(tf, x0, w0, err);
            final Operand<TFloat32> dw0 = tmp.get(0);
            final Operand<TFloat32> db0 = tmp.get(1);
            err = tmp.get(2);

            // Update weights

            tf.math.add(w1, tf.math.mul(dw1, tf.constant(-this.alpha))).asTensor().copyTo(this.layers_W1);
            tf.math.add(b1, tf.math.mul(db1, tf.constant(-this.alpha))).asTensor().copyTo(this.layers_B1);
            tf.math.add(w0, tf.math.mul(dw0, tf.constant(-this.alpha))).asTensor().copyTo(this.layers_W0);
            tf.math.add(b0, tf.math.mul(db0, tf.constant(-this.alpha))).asTensor().copyTo(this.layers_B0);
        }

        this.alpha = Math.max(alpha * 0.99f, 0.01f);
    }

    private List<Operand<TFloat32>> model(final Ops tf, final Operand<TFloat32> x) {

        final Operand<TFloat32> x0 = x;

        final Operand<TFloat32> w0 = tf.constant(this.layers_W0);
        final Operand<TFloat32> b0 = tf.constant(this.layers_B0);
        final Operand<TFloat32> x1 = this.dense(tf, x0, w0, b0);

        final Operand<TFloat32> x2 = tf.math.tanh(x1);

        final Operand<TFloat32> w1 = tf.constant(this.layers_W1);
        final Operand<TFloat32> b1 = tf.constant(this.layers_B1);
        final Operand<TFloat32> x3 = this.dense(tf, x2, w1, b1);

        final Operand<TFloat32> y = tf.math.sigmoid(x3);

        return List.of(x0, w0, b0, x1, x2, w1, b1, x3, y);
    }

    private Operand<TFloat32> dense(final Ops tf, final Operand<TFloat32> x, final Operand<TFloat32> w, final Operand<TFloat32> b) {
        return tf.math.add(tf.linalg.matMul(x, w), b);
    }

    private List<Operand<TFloat32>> dense_gd(final Ops tf, final Operand<TFloat32> x, final Operand<TFloat32> w, final Operand<TFloat32> dy) {
        final Constant<TInt32> AXIS_ZERO = tf.constant(0);
        final Operand<TFloat32> dw = tf.linalg.matMul(x, dy, MatMul.transposeA(true));
        final Operand<TFloat32> db = tf.sum(dy, AXIS_ZERO, Sum.keepDims(true));
        final Operand<TFloat32> dx = tf.linalg.matMul(dy, w, MatMul.transposeB(true));
        return List.of(dw, db, dx);
    }

    private Operand<TFloat32> tanh_gd(final Ops tf, final Operand<TFloat32> y, final Operand<TFloat32> dy) {
        final Constant<TFloat32> ONE = tf.constant(1.0f);
        final Constant<TFloat32> TWO = tf.constant(2.0f);
        return tf.math.mul(dy, tf.math.sub(ONE, tf.math.pow(y, TWO)));
    }

    private Operand<TFloat32> sigmoid_gd(final Ops tf, final Operand<TFloat32> y, final Operand<TFloat32> dy) {
        final Constant<TFloat32> ONE = tf.constant(1.0f);
        return tf.math.mul(dy, tf.math.mul(y, tf.math.sub(ONE, y)));
    }

    private Operand<TFloat32> mse_gd(final Ops tf, final Operand<TFloat32> yhat, final Operand<TFloat32> y) {
        return tf.math.div(tf.math.sub(yhat, y), tf.constant((float) yhat.shape().get(0)));
    }

    public static void main(final String[] args) {
        final FeedForward ff = new FeedForward();

        for (int i = 0; i < 2000; i++) {
            for (int j = 0; j < 4; j++) {
                ff.fit(
                    Tensor.of(training_features[j]).reshape(1, 2).toTFloat32(),
                    Tensor.of(training_labels[j]).reshape(1, 1).toTFloat32());
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
