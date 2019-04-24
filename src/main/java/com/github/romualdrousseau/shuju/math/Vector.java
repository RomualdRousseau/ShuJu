package com.github.romualdrousseau.shuju.math;

public class Vector {
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
