package com.github.romualdrousseau.shuju.math;

public class Helper {

    public static boolean checkNaN(Matrix m) {
        for (int i = 0; i < m.rows; i++) {
            final float[] a = m.data[i];
            for (int j = 0; j < m.cols; j++) {
                if (Float.isNaN(a[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Matrix rotate_180(Matrix m) {
        final Matrix mm = m.copy();
        for (int j = 0; j < mm.cols/ 2; j++) {
            mm.swap(j, mm.cols - j - 1, 1);
        }
        for (int i = 0; i < mm.rows / 2; i++) {
            mm.swap(i, mm.rows - i - 1, 0);
        }
        return mm;
    }

    public static Matrix expand_minmax(Matrix a, Matrix b, Matrix c) {
        final int size = b.rows / a.rows;
        assert(a.rows * size == b.rows);
        assert(a.cols * size == b.cols);
        assert(a.rows == c.rows);
        assert(a.cols == c.cols);
        Matrix result  = new Matrix(1, b.rows * b.cols);
        for(int i = 0; i < b.rows; i++) {
            final float[] aa = a.data[i / size];
            final float[] bb = b.data[i];
            final float[] cc = c.data[i / size];
            for(int j = 0; j < b.cols; j++) {
                if(bb[j] == aa[j / size]) {
                    result.data[0][i * b.cols + j] = cc[j / size];
                }
            }
        }
        return result;
    }

    public static Matrix expand_avg(Matrix m, int size) {
        assert (size > 0);
        final int N = size * size;
        Matrix result  = new Matrix(1, m.rows * m.cols * N);
        for(int i = 0; i < m.rows * size; i++) {
            final float[] mm = m.data[i / size];
            for(int j = 0; j < m.cols * size; j++) {
                result.data[0][i * m.cols * size + j] = mm[j / size] / N;
            }
        }
        return result;
    }
}
