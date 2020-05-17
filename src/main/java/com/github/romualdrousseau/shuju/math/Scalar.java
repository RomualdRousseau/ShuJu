package com.github.romualdrousseau.shuju.math;

import java.util.Random;

public class Scalar {
    public static final float EPSILON = 1e-8f;
    public static final float LAM = 1e-3f;

    private static Random randomGenerator;

    static {
        Scalar.randomGenerator = new Random(System.currentTimeMillis());
    }

    public static float sign(final float x) {
        return x >= 0 ? 1.0f : -1.0f;
    }

    public static float abs(final float x) {
        return (float) Math.abs(x);
    }

    public static float tanh(final float x) {
        return (float) Math.tanh(x);
    }

    public static float sqrt(final float x) {
        return (float) Math.sqrt(x);
    }

    public static float pow(final float x, final float n) {
        return (float) Math.pow(x, n);
    }

    public static float exp(final float x) {
        return (float) Math.exp(x);
    }

    public static float log(final float x) {
        return (float) Math.log(x);
    }

    public static float min(final float a, final float b) {
        return (float) Math.min(a, b);
    }

    public static float max(final float a, final float b) {
        return (float) Math.max(a, b);
    }

    public static float random(final float a) {
        return Scalar.random(0, a);
    }

    public static float random(final float a, final float b) {
        return randomGenerator.nextFloat() * (b - a) + a;
    }

    public static float randomGaussian() {
        return (float) randomGenerator.nextGaussian();
    }

    public static float mutate(final float rate, final float variance, final float x) {
        if (Scalar.random(1.0f) < rate) {
            return Scalar.randomGaussian() * variance;
        } else {
            return x;
        }
	}

    public static float sgn(final float x) {
        return (x > 0.0f) ? 1.0f : ((x < 0.0f) ? -1.0f : 0.0f);
    }

    public static float map(final float t, final float start1, final float stop1, final float start2,
            final float stop2) {
        final float m = (stop2 - start2) / (stop1 - start1);
        return m * (t - start1) + start2;
    }

    public static float unlerp(final float a, final float b, final float v) {
        return (v - a) / (b - a);
    }

    public static float constrain(final float x, final float a, final float b) {
        return (x < a) ? a : ((x > b) ? b : x);
    }

    public static float if_lt_then(final float x, final float p, final float a, final float b) {
        return (x < p) ? a : b;
    }
}
