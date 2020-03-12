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

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Matrix bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, Vector bias) {
        return weights.matmul(input).add(bias, 1);
    }

    public static Matrix xw_plus_b(Matrix input, Matrix weights, float bias) {
        return weights.matmul(input).add(bias);
    }

    public static Matrix a_mul_b(Matrix a, Matrix b) {
        return (a.colCount() == b.colCount()) ? b.mul(a) : b.matmul(a);
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

    public static Matrix im2col(Matrix m, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (m.rows - size) / stride;
        final int cols_n = 1 + (m.cols - size) / stride;
        assert ((rows_n - 1) * stride + size == m.rows);
        assert ((cols_n - 1) * stride + size == m.cols);
        final Matrix result = new Matrix(size * size, rows_n * cols_n);
        for (int i = 0; i < rows_n; i++) {
            for (int j = 0; j < cols_n; j++) {
                for (int y = 0; y < size; y++) {
                    final float[] b = m.data[i * stride + y];
                    for (int x = 0; x < size; x++) {
                        result.data[y * size + x][i * cols_n + j] = b[j * stride + x];
                    }
                }
            }
        }
        return result;
    }

    public static Matrix col2im(Matrix m, int mrows, int mcols, int size, int stride) {
        assert (stride > 0);
        assert (size > 0);
        final int rows_n = 1 + (mrows - size) / stride;
        final int cols_n = 1 + (mcols - size) / stride;
        assert ((rows_n - 1) * stride + size == mrows);
        assert ((cols_n - 1) * stride + size == mcols);
        final Matrix result = new Matrix(mrows, mcols);
        for (int i = 0; i < rows_n; i++) {
            for (int j = 0; j < cols_n; j++) {
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        result.data[i * stride + y][j * stride + x] = m.data[y * size + x][i * cols_n + j];
                    }
                }
            }
        }
        return result;
    }
}
