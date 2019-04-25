package com.github.romualdrousseau.shuju.math;

public class Vector {
    public static float[] oneHot(int i, int n) {
        float[] state = new float[n];
        state[i] = 1.0f;
        return state;
    }

    public static <T extends Enum<T>> float[] oneHot(T e, int n) {
        return Vector.oneHot(e.ordinal(), n);
    }

    public static <T extends Enum<T>> float[] oneHot(T[] l, int n) {
        float[] state = new float[n];
        for (int i = 0; i < l.length; i++) {
            T e = l[i];
            if(e != null) {
                state[e.ordinal()] = 1.0f;
            }
        }
        return state;
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
            u[i] = Vector.constrain(u[i], a, b);
        }
        return u;
    }

    public static float[] filter(float[] u, float p, float a, float b) {
        for (int j = 0; j < u.length; j++) {
            u[j] = (u[j] < p) ? a : b;
        }
        return u;
    }

    public static float scalar(float[] v1, float[] v2) {
        float sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }

    public static double scalar(double[] v1, double[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += v1[i] * v2[i];
        }
        return sum;
    }

    public static double norm(double[] v) {
        return Math.sqrt(scalar(v, v));
    }
}
