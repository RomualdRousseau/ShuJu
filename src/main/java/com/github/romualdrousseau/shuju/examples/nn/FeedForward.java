package com.github.romualdrousseau.shuju.examples.nn;

import java.util.List;

import org.tensorflow.EagerSession;
import org.tensorflow.Operand;
import org.tensorflow.op.Ops;
import org.tensorflow.op.core.Sum;
import org.tensorflow.op.linalg.MatMul;
import org.tensorflow.types.TFloat32;
import org.tensorflow.types.TInt32;

import com.github.romualdrousseau.shuju.types.Tensor;

public class FeedForward {

    public final static int EPOCHS = 100;

    public final static Tensor[] training_features = {
            Tensor.of(0.0f, 0.0f),
            Tensor.of(0.0f, 1.0f),
            Tensor.of(1.0f, 1.0f),
            Tensor.of(1.0f, 0.0f)
    };

    public final static Tensor[] training_labels = {
            Tensor.of(0.0f),
            Tensor.of(1.0f),
            Tensor.of(1.0f),
            Tensor.of(0.0f)
    };

    public static void main(final String[] args) {
        final FeedForward ff = new FeedForward();

        for (var i = 0; i < EPOCHS; i++) {
            var acc = 0.0f;
            for (int j = 0; j < training_features.length; j++) {
                 acc += ff.fit(
                        training_features[j].copy().reshape(1, 2).toTFloat32(),
                        training_labels[j].copy().reshape(1, 1).toTFloat32()).getFloat(0);
            }
            System.out.println(String.format("Epoch %d - accuracy: %.02f", i, acc / (float) training_features.length));
        }

        for (var j = 0; j < training_features.length; j++) {
            ff.predict(training_features[j].copy().reshape(1, 2).toTFloat32());
        }
    }

    private final TFloat32 layers_W0;

    private final TFloat32 layers_B0;

    private final TFloat32 layers_W1;

    private final TFloat32 layers_B1;

    private float alpha = 0.1f;

    public FeedForward() {
        try (final var session = EagerSession.create()) {
            final var tf = Ops.create(session);

            final var n = 2;
            final var m = 10;
            final var o = 1;

            // Compute GOROT initializer
            this.layers_W0 = Tensor.full((float) Math.sqrt(6.0f / (n + m)), n, m).toTFloat32();
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
        try (final var session = EagerSession.create()) {
            final var tf = Ops.create(session);

            final var args = this.model(tf, tf.constant(input));

            args.get(8).asTensor().scalars().forEach(x -> System.out.print((x.getFloat() >= 0.5f ? 1 : 0) + " "));
            System.out.println();
        }
    }

    public TFloat32 fit(final TFloat32 input, final TFloat32 output) {
        final var accuracy = Tensor.zeros(1).toTFloat32();

        try (final var session = EagerSession.create()) {
            final var tf = Ops.create(session);

            // Evaluate

            final var p = this.model(tf, tf.constant(input));
            final var yhat = p.get(8);
            final var loss = this.mse_gd(tf, yhat, tf.constant(output));

            // Back propagation

            final var dp = this.model_gd(tf, p, loss);

            // Update weights

            tf.math.add(p.get(5), tf.math.mul(dp.get(3), tf.constant(-this.alpha))).asTensor().copyTo(this.layers_W1);
            tf.math.add(p.get(6), tf.math.mul(dp.get(4), tf.constant(-this.alpha))).asTensor().copyTo(this.layers_B1);
            tf.math.add(p.get(1), tf.math.mul(dp.get(1), tf.constant(-this.alpha))).asTensor().copyTo(this.layers_W0);
            tf.math.add(p.get(2), tf.math.mul(dp.get(2), tf.constant(-this.alpha))).asTensor().copyTo(this.layers_B0);

            // Compute accuracy

            final var AXIS_ZERO = tf.constant(0);
            tf.math.sqrt(tf.sum(tf.math.square(loss), AXIS_ZERO)).asOutput().asTensor().copyTo(accuracy);
        }

        this.alpha = Math.max(alpha * 0.99f, 0.01f);
        return accuracy;
    }

    private List<Operand<TFloat32>> model(final Ops tf, final Operand<TFloat32> x) {

        final var x0 = x;

        final var w0 = tf.constant(this.layers_W0);
        final var b0 = tf.constant(this.layers_B0);
        final var x1 = this.dense(tf, x0, w0, b0);

        final var x2 = tf.math.tanh(x1);

        final var w1 = tf.constant(this.layers_W1);
        final var b1 = tf.constant(this.layers_B1);
        final var x3 = this.dense(tf, x2, w1, b1);

        final var y = tf.math.sigmoid(x3);

        return List.of(x0, w0, b0, x1, x2, w1, b1, x3, y);
    }

    private List<Operand<TFloat32>> model_gd(final Ops tf, final List<Operand<TFloat32>> p,
            final Operand<TFloat32> loss) {
        final var x0 = p.get(0);
        final var w0 = p.get(1);
        final var x2 = p.get(4);
        final var w1 = p.get(5);
        final var yhat = p.get(8);

        var err = this.sigmoid_gd(tf, yhat, loss);

        var tmp = dense_gd(tf, x2, w1, err);
        final var dw1 = tmp.get(0);
        final var db1 = tmp.get(1);
        err = tmp.get(2);

        err = this.tanh_gd(tf, x2, err);

        tmp = this.dense_gd(tf, x0, w0, err);
        final var dw0 = tmp.get(0);
        final var db0 = tmp.get(1);
        err = tmp.get(2);

        return List.of(err, dw0, db0, dw1, db1);
    }

    private Operand<TFloat32> dense(final Ops tf, final Operand<TFloat32> x, final Operand<TFloat32> w,
            final Operand<TFloat32> b) {
        return tf.math.add(tf.linalg.matMul(x, w), b);
    }

    private List<Operand<TFloat32>> dense_gd(final Ops tf, final Operand<TFloat32> x, final Operand<TFloat32> w,
            final Operand<TFloat32> dy) {
        final var AXIS_ZERO = tf.constant(0);
        final var dw = tf.linalg.matMul(x, dy, MatMul.transposeA(true));
        final var db = tf.sum(dy, AXIS_ZERO, Sum.keepDims(true));
        final var dx = tf.linalg.matMul(dy, w, MatMul.transposeB(true));
        return List.of(dw, db, dx);
    }

    private Operand<TFloat32> tanh_gd(final Ops tf, final Operand<TFloat32> y, final Operand<TFloat32> dy) {
        final var ONE = tf.constant(1.0f);
        final var TWO = tf.constant(2.0f);
        return tf.math.mul(dy, tf.math.sub(ONE, tf.math.pow(y, TWO)));
    }

    private Operand<TFloat32> sigmoid_gd(final Ops tf, final Operand<TFloat32> y, final Operand<TFloat32> dy) {
        final var ONE = tf.constant(1.0f);
        return tf.math.mul(dy, tf.math.mul(y, tf.math.sub(ONE, y)));
    }

    private Operand<TFloat32> mse_gd(final Ops tf, final Operand<TFloat32> yhat, final Operand<TFloat32> y) {
        return tf.math.div(tf.math.sub(yhat, y), tf.constant((float) yhat.shape().get(0)));
    }
}
