package com.github.romualdrousseau.shuju.math;

public class Scalar {
    public static final float EPSILON = 1e-8f;

    public static float[] oneHot(int i, int n) {
        float[] state = new float[n];
        state[i] = 1.0f;
        return state;
    }

    public static float abs(float x) {
        return (float) Math.abs(x);
    }

    public static float tanh(float x) {
        return (float) Math.tanh(x);
    }

    public static float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    public static float pow(float x, float n) {
        return (float) Math.pow(x, n);
    }

    public static float exp(float x) {
        return (float) Math.exp(x);
    }

    public static float log(float x) {
        return (float) Math.log(x);
    }

    public static float min(float a, float b) {
        return (float) Math.min(a, b);
    }

    public static float max(float a, float b) {
        return (float) Math.max(a, b);
    }

    public static float random(float a) {
        return Scalar.random(0, a);
    }

    public static float random(float a, float b) {
        return (float) (Math.random() * (b - a) + a);
    }

    public static float randomGaussian() {
        return (float) Math.random();
    }

    public static float sgn(float x) {
        return (x > 0.0f) ? 1.0f : ((x < 0.0f) ? -1.0f : 0.0f);
    }

    public static int argmax(float[] v) {
        int result = 0;
        float maxValue = v[0];
        for (int i = 1; i < v.length; i++) {
            if (v[i] > maxValue) {
                maxValue = v[i];
                result = i;
            }
        }
        return result;
    }

    public static int argmin(float[] v) {
        int result = 0;
        float minValue = v[0];
        for (int i = 1; i < v.length; i++) {
            if (v[i] < minValue) {
                minValue = v[i];
                result = i;
            }
        }
        return result;
    }

    public static float[] add(float[] u, float[] v) {
        for (int i = 0; i < u.length; i++) {
            u[i] += v[i];
        }
        return u;
    }

    public static float constrain(float x, float a, float b) {
        return (x > a) ? a : ((x < b) ? b : x);
    }

    public static float[] constrain(float[] u, float a, float b) {
        for (int i = 0; i < u.length; i++) {
            u[i] = Scalar.constrain(u[i], a, b);
        }
        return u;
    }

    public static float[] filter(float[] u, float p, float a, float b) {
        for (int j = 0; j < u.length; j++) {
            u[j] = (u[j] < p) ? a : b;
        }
        return u;
    }

    public static float unlerp(float a, float b, float v) {
        return (v - a) / (b - a);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Matrix bias) {
        return weights.transform(input).add(bias);
    }

    public static Matrix a_mul_b(Matrix a, Matrix b) {
        return (a.colCount() == b.colCount()) ? b.mult(a) : b.transform(a);
    }
}
